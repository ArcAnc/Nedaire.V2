/**
 * @author ArcAnc
 * Created at: 28.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.block.ber;

import com.arcanc.nedaire.Nedaire;
import com.arcanc.nedaire.content.block.block_entity.FluidStorageBlockEntity;
import com.arcanc.nedaire.util.helpers.RenderHelper;
import com.arcanc.nedaire.util.inventory.fluids.SimpleFluidHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class FluidStorageRenderer implements BlockEntityRenderer<FluidStorageBlockEntity>
{
    private static final float MIN_X =  3.01F/16F;
    private static final float MAX_X = 12.99F/16F;
    private static final float MIN_Y =  0.01F/16F;
    private static final float MAX_Y = 15.99F/16F;
    private static final float MIN_Z =  3.01F/16F;
    private static final float MAX_Z = 12.99F/16F;

    private static final float MIN_UV_T =  3.01F;
    private static final float MAX_UV_T = 12.99F;
    private static final float MIN_U_S  =  3.01F;
    private static final float MAX_U_S  = 12.99F;
    private static final float MIN_V_S  =  0.01F;
    private static final float MAX_V_S  = 15.99F;

    public FluidStorageRenderer(BlockEntityRendererProvider.Context ctx)
    {

    }

    @Override
    public void render(@NotNull FluidStorageBlockEntity blockEntity,
                       float partialTick,
                       @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource,
                       int packedLight,
                       int packedOverlay)
    {
        SimpleFluidHandler handler = blockEntity.getHandler(null);
        FluidStack stack = handler.getFluidInTank(0);
        if (!stack.isEmpty())
            renderContent(stack, (float) stack.getAmount() / handler.getTankCapacity(0), poseStack, bufferSource, packedLight, packedOverlay);
    }

    private void renderContent(@NotNull FluidStack stack, float height, @NotNull PoseStack pose, @NotNull MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        IClientFluidTypeExtensions renderProps = IClientFluidTypeExtensions.of(stack.getFluid());

        ResourceLocation stillTex = renderProps.getStillTexture();
        TextureAtlasSprite still = RenderHelper.mc().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTex);

        ResourceLocation flowTex = renderProps.getFlowingTexture();
        TextureAtlasSprite flow = RenderHelper.mc().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(flowTex);


        boolean gas = stack.getFluid().getFluidType().isLighterThanAir();
        int[] color = RenderHelper.splitRGBA(renderProps.getTintColor());


        pose.pushPose();
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder builder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        RenderSystem.enableDepthTest();

        PoseStack.Pose matrix = pose.last();

        drawTop(builder, matrix, height, still, color, gas);
        drawSides(builder, matrix, height, flow, color, gas);
        BufferUploader.drawWithShader(builder.buildOrThrow());
        RenderSystem.disableDepthTest();
        pose.popPose();
    }

    private void drawTop(@NotNull VertexConsumer builder, PoseStack.Pose pose, float height, @NotNull TextureAtlasSprite tex, int @NotNull [] color, boolean gas)
    {
        float minX = gas ? MAX_X : MIN_X;
        float maxX = gas ? MIN_X : MAX_X;
        float y = (MIN_Y + (gas ? (1F - height) * (MAX_Y - MIN_Y) : height * (MAX_Y - MIN_Y)))* 0.85f;

        float minU = tex.getU(MIN_UV_T / 16f);
        float maxU = tex.getU(MAX_UV_T / 16f);
        float minV = tex.getV(MIN_UV_T / 16f);
        float maxV = tex.getV(MAX_UV_T / 16f);

        builder.addVertex(pose, maxX, y, MIN_Z).setColor(color[0], color[1], color[2], color[3]).setUv(minU, minV);
        builder.addVertex(pose, minX, y, MIN_Z).setColor(color[0], color[1], color[2], color[3]).setUv(maxU, minV);
        builder.addVertex(pose, minX, y, MAX_Z).setColor(color[0], color[1], color[2], color[3]).setUv(maxU, maxV);
        builder.addVertex(pose, maxX, y, MAX_Z).setColor(color[0], color[1], color[2], color[3]).setUv(minU, maxV);
    }

    private void drawSides(@NotNull VertexConsumer builder, PoseStack.Pose pose, float height, @NotNull TextureAtlasSprite tex, int @NotNull [] color, boolean gas)
    {
        float minY = gas ? MAX_Y - (height * (MAX_Y - MIN_Y)) : MIN_Y;
        float maxY = gas ? MAX_Y : MIN_Y + height * (MAX_Y - MIN_Y);

        float minU = tex.getU(MIN_U_S / 16f);
        float maxU = tex.getU(MAX_U_S / 16f);
        float minV = tex.getV(MIN_V_S / 16f);
        float maxV = tex.getV(height);

        //North
        builder.addVertex(pose, MIN_X, maxY, MIN_Z).setColor(color[0], color[1], color[2], color[3]).setUv(minU, minV);
        builder.addVertex(pose, MAX_X, maxY, MIN_Z).setColor(color[0], color[1], color[2], color[3]).setUv(maxU, minV);
        builder.addVertex(pose, MAX_X, minY, MIN_Z).setColor(color[0], color[1], color[2], color[3]).setUv(maxU, maxV);
        builder.addVertex(pose, MIN_X, minY, MIN_Z).setColor(color[0], color[1], color[2], color[3]).setUv(minU, maxV);

        //South
        builder.addVertex(pose, MAX_X, maxY, MAX_Z).setColor(color[0], color[1], color[2], color[3]).setUv(minU, minV);
        builder.addVertex(pose, MIN_X, maxY, MAX_Z).setColor(color[0], color[1], color[2], color[3]).setUv(maxU, minV);
        builder.addVertex(pose, MIN_X, minY, MAX_Z).setColor(color[0], color[1], color[2], color[3]).setUv(maxU, maxV);
        builder.addVertex(pose, MAX_X, minY, MAX_Z).setColor(color[0], color[1], color[2], color[3]).setUv(minU, maxV);

        //East
        builder.addVertex(pose, MAX_X, maxY, MIN_Z).setColor(color[0], color[1], color[2], color[3]).setUv(minU, minV);
        builder.addVertex(pose, MAX_X, maxY, MAX_Z).setColor(color[0], color[1], color[2], color[3]).setUv(maxU, minV);
        builder.addVertex(pose, MAX_X, minY, MAX_Z).setColor(color[0], color[1], color[2], color[3]).setUv(maxU, maxV);
        builder.addVertex(pose, MAX_X, minY, MIN_Z).setColor(color[0], color[1], color[2], color[3]).setUv(minU, maxV);

        //West
        builder.addVertex(pose, MIN_X, maxY, MAX_Z).setColor(color[0], color[1], color[2], color[3]).setUv(minU, minV);
        builder.addVertex(pose, MIN_X, maxY, MIN_Z).setColor(color[0], color[1], color[2], color[3]).setUv(maxU, minV);
        builder.addVertex(pose, MIN_X, minY, MIN_Z).setColor(color[0], color[1], color[2], color[3]).setUv(maxU, maxV);
        builder.addVertex(pose, MIN_X, minY, MAX_Z).setColor(color[0], color[1], color[2], color[3]).setUv(minU, maxV);
    }
}
