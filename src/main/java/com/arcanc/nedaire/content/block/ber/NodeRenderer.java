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
import com.arcanc.nedaire.util.NDatabase;
import com.arcanc.nedaire.util.helpers.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class NodeRenderer implements BlockEntityRenderer<NodeBlockEntity>
{
    private static final ResourceLocation TEXTURE = NDatabase.modRL("textures/block/node/normal.png");
    public NodeRenderer(BlockEntityRendererProvider.Context ctx)
    {

    }
    @Override
    public void render(@NotNull NodeBlockEntity pBlockEntity,
                       float pPartialTick,
                       @NotNull PoseStack pPoseStack,
                       @NotNull MultiBufferSource pBuffer,
                       int pPackedLight,
                       int pPackedOverlay)
    {
        Camera camera = RenderHelper.mc().gameRenderer.getMainCamera();
        pPoseStack.pushPose();
        pPoseStack.translate(0.5f, 0.5f, 0.5f);
        pPoseStack.mulPose(camera.rotation());
        pPoseStack.mulPose(Axis.YP.rotationDegrees(180f));

        VertexConsumer builder = pBuffer.getBuffer(RenderType.entityTranslucent(TEXTURE));

        renderRotatedQuad(pPoseStack.last(), builder, pPackedLight, pPackedOverlay);
        pPoseStack.popPose();
    }

    public void renderRotatedQuad(PoseStack.Pose pose, VertexConsumer builder, int packedLight, int packedOverlay)
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

        renderVertex(pose, builder, quadSize, -quadSize, u1, v1, packedLight, packedOverlay);
        renderVertex(pose, builder, quadSize, quadSize, u1, v0, packedLight, packedOverlay);
        renderVertex(pose, builder, -quadSize, quadSize, u0, v0, packedLight, packedOverlay);
        renderVertex(pose, builder, -quadSize, -quadSize, u0, v1, packedLight, packedOverlay);
    }

    public void renderVertex(PoseStack.Pose pose,
                             @NotNull VertexConsumer builder,
                             float offsetX,
                             float offsetY,
                             float u,
                             float v,
                             int packedLight,
                             int packedOverlay)
    {
        builder.addVertex(pose, offsetX, offsetY, 0.0f).
                setLight(packedLight).
                setOverlay(packedOverlay).
                setUv(u, v).
                setColor(1.0f, 1.0f, 1.0f, 0.65f).
                setNormal(pose, 0f, 1f, 0f);
    }
}
