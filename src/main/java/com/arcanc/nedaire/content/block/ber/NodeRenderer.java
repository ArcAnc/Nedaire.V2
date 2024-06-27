/**
 * @author ArcAnc
 * Created at: 24.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.block.ber;

import com.arcanc.nedaire.content.block.block_entity.NodeBlockEntity;
import com.arcanc.nedaire.content.fluid.NEnergonFluidType;
import com.arcanc.nedaire.util.NDatabase;
import com.arcanc.nedaire.util.helpers.RenderHelper;
import com.arcanc.nedaire.util.inventory.fluids.SimpleFluidHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

public class NodeRenderer implements BlockEntityRenderer<NodeBlockEntity>
{
    private static final ResourceLocation TEXTURE = NDatabase.modRL("textures/block/node/normal.png");
    public NodeRenderer(BlockEntityRendererProvider.Context ctx)
    {

    }
    @Override
    public void render(@NotNull NodeBlockEntity blockEntity,
                       float pPartialTick,
                       @NotNull PoseStack pPoseStack,
                       @NotNull MultiBufferSource pBuffer,
                       int pPackedLight,
                       int pPackedOverlay)
    {
        SimpleFluidHandler handler = blockEntity.getHandler(null);
        int[] resColor = new int[]{255, 255, 255};

        if (!handler.isEmpty())
        {
            int[] color = new int[handler.getTanks()];

            for (int q = 0 ; q < handler.getTanks(); q++)
            {
                FluidType type = handler.getFluidInTank(q).getFluidType();
                if (type instanceof NEnergonFluidType energonType)
                    color[q] = energonType.getEnergonType().color();
            }
            int[][] divided_color = new int[color.length][3];
            for (int q = 0; q < color.length; q++)
            {
                int[] c = RenderHelper.splitRGBA(color[q]);
                divided_color[q][0] = c[0];
                divided_color[q][1] = c[1];
                divided_color[q][2] = c[2];
            }
            resColor = new int[]{0, 0, 0};
            for (int q = 0; q < 3; q++)
            {
                for (int[] ints : divided_color) {
                    resColor[q] += ints[q];
                }
            }

           for (int q = 0; q < color.length; q++)
               resColor[q] /= color.length;
        }

        Camera camera = RenderHelper.mc().gameRenderer.getMainCamera();
        pPoseStack.pushPose();
        pPoseStack.translate(0.5f, 0.5f, 0.5f);
        pPoseStack.mulPose(camera.rotation());
        pPoseStack.mulPose(Axis.YP.rotationDegrees(180f));

        VertexConsumer builder = pBuffer.getBuffer(RenderType.entityTranslucent(TEXTURE));

        renderRotatedQuad(pPoseStack.last(), builder, pPackedLight, pPackedOverlay, resColor);
        pPoseStack.popPose();
    }

    public void renderRotatedQuad(PoseStack.Pose pose, VertexConsumer builder, int packedLight, int packedOverlay, int[] color)
    {
        long time = System.currentTimeMillis();
        float quadSize = 0.6f;
        int tickAmount = 2048/64;
        int timePerTick = 5;

        time *= timePerTick;
        time *= 2;
        time /=1000;
        time %= tickAmount;

        float u0 = 0.0f;
        float u1 = 1.0f;
        float v0 = time / 32f;
        float v1 = (time + 1) / 32f;

        renderVertex(pose, builder, quadSize, -quadSize, u1, v1, packedLight, packedOverlay, color);
        renderVertex(pose, builder, quadSize, quadSize, u1, v0, packedLight, packedOverlay,color);
        renderVertex(pose, builder, -quadSize, quadSize, u0, v0, packedLight, packedOverlay, color);
        renderVertex(pose, builder, -quadSize, -quadSize, u0, v1, packedLight, packedOverlay, color);
    }

    public void renderVertex(PoseStack.Pose pose,
                             @NotNull VertexConsumer builder,
                             float offsetX,
                             float offsetY,
                             float u,
                             float v,
                             int packedLight,
                             int packedOverlay, int[] color)
    {
        builder.addVertex(pose, offsetX, offsetY, 0.0f).
                setLight(packedLight).
                setOverlay(packedOverlay).
                setUv(u, v).
                setColor(color[0], color[1], color[2], 166).
                setNormal(pose, 0f, 1f, 0f);
    }
}
