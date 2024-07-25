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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jetbrains.annotations.NotNull;

public class FluidStorageBlock extends NBaseEntityBlock<FluidStorageBlockEntity> implements IProbeInfoAccessor
{
    private static final VoxelShape SHAPE = Shapes.or(
            box(3, 2, 3, 13, 14, 13),
            box(11, 0, 1, 15, 4, 5),
            box(11, 12, 1, 15, 16, 5),
            box(11, 12, 11, 15, 16, 15),
            box(11, 0, 11, 15, 4, 15),
            box(1, 0, 11, 5, 4, 15),
            box(1, 12, 11, 5, 16, 15),
            box(1, 0, 1, 5, 4, 5),
            box(1, 12, 1, 5, 16, 5),
            box(2, 0, 5, 5, 2, 11),
            box(11, 0, 5, 14, 2, 11),
            box(11, 14, 5, 14, 16, 11),
            box(2, 14, 5, 5, 16, 11),
            box(12, 4, 12, 14, 12, 14),
            box(12, 4, 2, 14, 12, 4),
            box(2, 4, 2, 4, 12, 4),
            box(2, 4, 12, 4, 12, 14),
            box(5, 14, 11, 11, 16, 14),
            box(5, 0, 11, 11, 2, 14),
            box(5, 14, 2, 11, 16, 5),
            box(5, 0, 2, 11, 2, 5),
            box(1, 6.5f, 13, 3, 9.5f, 15),
            box(13, 6.5f, 13, 15, 9.5f, 15),
            box(13, 6.5f, 1, 15, 9.5f, 3),
            box(1, 6.5f, 1, 3, 9.5f, 3)
            );

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
        if(FluidUtil.interactWithFluidHandler(player, hand, level, pos, null))
        {
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
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
