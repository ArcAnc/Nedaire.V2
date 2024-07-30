/**
 * @author ArcAnc
 * Created at: 23.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.util.helpers;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.pipeline.VertexConsumerWrapper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import java.util.ArrayList;
import java.util.List;

public class RenderHelper
{
    private static final RenderType TRANSLUCENT = RenderType.entityTranslucentCull(InventoryMenu.BLOCK_ATLAS);

    public static @NotNull Minecraft mc()
    {
        return Minecraft.getInstance();
    }

    public static LocalPlayer clientPlayer()
    {
        return mc().player;
    }

    public static @NotNull TextureAtlas textureMap()
    {
        return mc().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
    }

    public static @NotNull TextureAtlasSprite getTexture(ResourceLocation location)
    {
        return textureMap().getSprite(location);
    }

    public static @NotNull ItemRenderer renderItem()
    {
        return mc().getItemRenderer();
    }

    public static void renderItemStack(GuiGraphics guiGraphics, ItemStack stack, int x, int y, boolean overlay)
    {
        renderItemStack(guiGraphics, stack, x, y, overlay, null);
    }

    public static void renderItemStack(@NotNull GuiGraphics guiGraphics, @NotNull ItemStack stack, int x, int y, boolean overlay, String count)
    {
        if(!stack.isEmpty())
        {
            // Counteract the zlevel increase, because multiplied with the matrix, it goes out of view
            guiGraphics.renderItem(stack, x, y, 0,-50);

            if(overlay)
            {
                // Use the Item's font renderer, if available
                Font font = IClientItemExtensions.of(stack.getItem()).getFont(stack, IClientItemExtensions.FontContext.ITEM_COUNT);
                font = font != null ? font : mc().font;
                guiGraphics.renderItemDecorations(font, stack, x, y, count);
            }
        }
    }

    public static void renderFakeItemTransparent(@NotNull GuiGraphics gui, @NotNull ItemStack stack, int x, int y, float alpha)
    {
        renderFakeItemColored(gui, stack, x, y, 1.f, 1.f, 1.f, alpha);
    }

    public static void renderFakeItemColored(@NotNull GuiGraphics gui, @NotNull ItemStack stack, int x, int y, float red, float green, float blue, float alpha)
    {
        if (stack.isEmpty())
            return;

        Minecraft mc = mc();
        BakedModel model = renderItem().getModel(stack, mc().level, mc.player, 0);
        renderItemModel(gui, stack, x, y, red, green, blue, alpha, model);
    }

    /**
     * {@link GuiGraphics::renderItem} but with color
     */
    public static void renderItemModel(@NotNull GuiGraphics gui, @NotNull ItemStack stack, int x, int y, float red, float green, float blue, float alpha, @NotNull BakedModel model)
    {
        Matrix4fStack poseStack = RenderSystem.getModelViewStack();
        poseStack.pushMatrix();
        {
            poseStack.translate(x + 8, y + 8, 100);
            poseStack.scale(16.f, -16.f, 16.f);
            RenderSystem.applyModelViewMatrix();

            boolean flag = !model.usesBlockLight();
            if (flag)
                Lighting.setupForFlatItems();

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

            MultiBufferSource.BufferSource buffer = mc().renderBuffers().bufferSource();
            renderItem().render(stack,
                    ItemDisplayContext.GUI,
                    false,
                    new PoseStack(),
                    wrapBuffer(buffer, red, green, blue, alpha, alpha < 0.5f),
                    LightTexture.FULL_BRIGHT,
                    OverlayTexture.NO_OVERLAY,
                    model);

            RenderSystem.disableDepthTest();
            buffer.endBatch();
            RenderSystem.enableDepthTest();
            if (flag)
                Lighting.setupFor3DItems();
        }
        poseStack.popMatrix();
        RenderSystem.applyModelViewMatrix();
    }

    @Contract(pure = true)
    private static @NotNull MultiBufferSource wrapBuffer(MultiBufferSource buffer, float red, float green, float blue, float alpha, boolean forceTranslucent)
    {
        return renderType -> new TintedVertexConsumer(buffer.getBuffer(forceTranslucent ? TRANSLUCENT : renderType), red, green, blue, alpha);
    }

    public static final class TintedVertexConsumer extends VertexConsumerWrapper
    {
        private final float red;
        private final float green;
        private final float blue;
        private final float alpha;

        public TintedVertexConsumer(VertexConsumer wrapped, float red, float green, float blue, float alpha)
        {
            super(wrapped);
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }

        @Override
        public @NotNull VertexConsumer setColor(int red, int green, int blue, int alpha)
        {
            return parent.setColor(
                    (red * this.red) / 255f,
                    (green * this.green) / 255f,
                    (blue * this.blue) / 255f,
                    (alpha * this.alpha) / 255f
            );
        }
    }

    public static @NotNull List<Vec3> getSpiralAroundVector(@NotNull Vec3 startPos, @NotNull Vec3 finishPos, float radius, int steps, int turns)
    {
        Vec3 vector = startPos.vectorTo(finishPos);

        List<Vec3> list = new ArrayList<>();

        double length = vector.length();
        Vec3 unit = vector.normalize();

        Vec3 perp = unit.cross(new Vec3(0,0, 1)).normalize();

        for (int q = 0; q < steps; q++)
        {
            double t = (double) q / (steps - 1);
            double angle = turns * 2  * Math.PI * t;

            Vec3 normal = perp.scale(Math.cos(angle)).
                    add(unit.cross(perp).
                            scale(Math.sin(angle)));

            Vec3 point = startPos.add(unit.scale(t * length));

            list.add(point.add(normal.scale(radius)));
        }

        return list;
    }

    public static @NotNull List<Vec3> getCircleAroundPoint (@NotNull Vec3 point, float radius, int segments)
    {
        List<Vec3> circle = new ArrayList<>();

        Vec3 axis = point.normalize();
        Vec3 orthoVector;

        if (Math.abs(axis.x) < Math.abs(axis.y) && Math.abs(axis.x) < Math.abs(axis.z))
        {
            orthoVector = new Vec3(1, 0, 0);
        } else if (Math.abs(axis.y) < Math.abs(axis.x) && Math.abs(axis.y) < Math.abs(axis.z))
        {
            orthoVector = new Vec3(0, 1, 0);
        } else {
            orthoVector = new Vec3(0, 0, 1);
        }

        Vec3 tangent = axis.cross(orthoVector).normalize();
        Vec3 bitangent = axis.cross(tangent).normalize();

        for (int q = 0; q < segments; q++)
        {
            double angle = 2 * Math.PI * q / segments;
            double dynamicRadius = radius * (Math.cos(System.currentTimeMillis() / 1000d % 360) * 0.45d + 1.55d) ;
            double x = dynamicRadius * Math.cos(angle);
            double y = dynamicRadius * Math.sin(angle);

            circle.add(point.add(
                    x * tangent.x() + y * bitangent.x(),
                    x * tangent.y() + y * bitangent.y(),
                    x * tangent.z() + y * bitangent.z()));
        }


        return circle;
    }

    public static @NotNull List<List<Vec3>> closeFirstAndLastCircles(@NotNull final List<Vec3> center, @NotNull List<List<Vec3>> circles)
    {
        Vec3 firstPoint = center.getFirst();
        Vec3 lastPoint = center.getLast();

        List<Vec3> circleFirst = circles.getFirst();
        List<Vec3> circleLast = circles.getLast();

        for (int q = 0; q < circleFirst.size(); q++)
        {
            circleFirst.set(q, firstPoint);
            circleLast.set(q, lastPoint);
        }

        return circles;
    }

    public static @NotNull List<List<Vec3>> getCirclesAroundPoints(@NotNull List<Vec3> points, float radius, int segments, boolean isEssence)
    {
        List<List<Vec3>> circles = new ArrayList<>();

        for (int q = 0; q < points.size(); q++)
        {
            Vec3 center = points.get(q);
            if (isEssence)
            {
                if (q == 1)
                    circles.add(getCircleAroundPoint(center, radius * 0.4f, segments));
                else if (q == 2)
                    circles.add(getCircleAroundPoint(center, radius * 0.7f, segments));
                else
                    circles.add(getCircleAroundPoint(center, radius, segments));
            }
            else
                circles.add(getCircleAroundPoint(center, radius, segments));
        }

        if (isEssence)
            return closeFirstAndLastCircles(points, circles);

        return circles;
    }


    /*0 - red
    * 1 - green
    * 2 - blue
    * 3 - alpha*/
    public static int @NotNull [] splitRGBA(int color)
    {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8)  & 0xFF;
        int b =  color        & 0xFF;
        int a = (color >> 24) & 0xFF;

        return new int[] { r, g, b, a };
    }

    public static void blit(GuiGraphics guiGraphics, ResourceLocation location, float posX, float posY, float posZ, float sizeX, float sizeY, int uStart, int uSize, int vStart, int vSize, int textureSizeX, int textureSizeY)
    {
        blit(guiGraphics, location, posX, posY, posZ, sizeX, sizeY, uStart, uSize, vStart, vSize, textureSizeX, textureSizeY, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void blit(@NotNull GuiGraphics guiGraphics, ResourceLocation location, float posX, float posY, float posZ, float sizeX, float sizeY, int uStart, int uSize, int vStart, int vSize, int textureSizeX, int textureSizeY, float red, float green, float blue, float alpha)
    {
        RenderSystem.setShaderTexture(0, location);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        float posXFinish = posX + sizeX;
        float posYFinish = posY + sizeY;

        float uScaledStart = uStart / (float)textureSizeX;
        float uScaledFinish = (uStart + uSize) / (float)textureSizeX;
        float vScaledStart = vStart / (float)textureSizeY;
        float vScaledFinish = (vStart + vSize) / (float)textureSizeY;

        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.addVertex(matrix4f, posX,       posY,       posZ).setColor(red, green, blue, alpha).setUv(uScaledStart,  vScaledStart);
        bufferbuilder.addVertex(matrix4f, posX,       posYFinish, posZ).setColor(red, green, blue, alpha).setUv(uScaledStart,  vScaledFinish);
        bufferbuilder.addVertex(matrix4f, posXFinish, posYFinish, posZ).setColor(red, green, blue, alpha).setUv(uScaledFinish, vScaledFinish);
        bufferbuilder.addVertex(matrix4f, posXFinish, posY,       posZ).setColor(red, green, blue, alpha).setUv(uScaledFinish, vScaledStart);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
        RenderSystem.disableBlend();
    }
}
