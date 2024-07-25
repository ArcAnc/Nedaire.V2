/**
 * @author ArcAnc
 * Created at: 16.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.block.ber;

import com.arcanc.nedaire.content.block.block_entity.FluidTransmitterBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class FluidTransmitterRenderer implements BlockEntityRenderer<FluidTransmitterBlockEntity>
{

    public FluidTransmitterRenderer(BlockEntityRendererProvider.Context ctx)
    {

    }

    @Override
    public void render(@NotNull FluidTransmitterBlockEntity blockEntity,
                       float partialTick,
                       @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource,
                       int packedLight,
                       int packedOverlay)
    {
        BlockPos bePos = blockEntity.getBlockPos();
        for (BlockPos pos : blockEntity.getAttachedPoses())
        {
            Vec3 targetPos = pos.getCenter().subtract(bePos.getCenter().subtract(0, 0.25f, 0));

            poseStack.pushPose();
            PoseStack.Pose matrix = poseStack.last();

            VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.lines());

            vertexconsumer.addVertex(matrix,0.5f, 0.25f, 0.5f).setColor(255, 0, 0, 255).setNormal(matrix, 1, 0, 0);
            vertexconsumer.addVertex(matrix, (float)targetPos.x() + 0.5f, (float)targetPos.y() + 0.25f, (float)targetPos.z() + 0.5f).setColor(255, 0, 0, 255).setNormal(matrix, 1, 0, 0);
            poseStack.popPose();
        }


    }

    @Override
    public @NotNull AABB getRenderBoundingBox(@NotNull FluidTransmitterBlockEntity blockEntity)
    {
        return AABB.INFINITE;
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull FluidTransmitterBlockEntity blockEntity)
    {
        return true;
    }
}
