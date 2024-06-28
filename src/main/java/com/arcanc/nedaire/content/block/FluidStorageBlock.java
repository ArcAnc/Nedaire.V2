/**
 * @author ArcAnc
 * Created at: 28.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.block;

import com.arcanc.nedaire.content.block.block_entity.FluidStorageBlockEntity;
import com.arcanc.nedaire.registration.NRegistration;
import com.arcanc.nedaire.util.inventory.fluids.SimpleFluidHandler;
import com.mojang.serialization.MapCodec;
import mcjty.theoneprobe.api.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jetbrains.annotations.NotNull;

public class FluidStorageBlock extends NBaseEntityBlock<FluidStorageBlockEntity> implements IProbeInfoAccessor
{
    private static final VoxelShape SHAPE = box(3, 0, 3, 13, 16, 13);

    public static final MapCodec<FluidStorageBlock> CODEC = simpleCodec(FluidStorageBlock :: new);

    public FluidStorageBlock(Properties props)
    {
        super (NRegistration.NBlockEntities.BE_FLUID_STORAGE, props);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack,
                                                       @NotNull BlockState state,
                                                       @NotNull Level level,
                                                       @NotNull BlockPos pos,
                                                       @NotNull Player player,
                                                       @NotNull InteractionHand hand,
                                                       @NotNull BlockHitResult hitResult)
    {
        if (!level.isClientSide())
        {
            if(FluidUtil.interactWithFluidHandler(player, hand, level, pos, hitResult.getDirection()))
            {
                return ItemInteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public @NotNull VoxelShape getInteractionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos)
    {
        return SHAPE;
    }

    @Override
    protected @NotNull MapCodec<? extends Block> codec()
    {
        return CODEC;
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, @NotNull Level level, BlockState blockState, @NotNull IProbeHitData iProbeHitData)
    {
        BlockEntity tile = level.getBlockEntity(iProbeHitData.getPos());
        if (tile instanceof FluidStorageBlockEntity be)
        {
            SimpleFluidHandler handler = be.getHandler(null);

            if (!handler.isEmpty())
            {
                TankReference reference = TankReference.createHandler(handler);

                iProbeInfo.tank(reference, iProbeInfo.
                        defaultProgressStyle().
                        alignment(ElementAlignment.ALIGN_CENTER).
                        suffix(" | %s EE", reference.getCapacity()));
            }
        }
    }
}
