/**
 * @author ArcAnc
 * Created at: 01.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.block;

import com.arcanc.nedaire.content.block.block_entity.FluidTransmitterBlockEntity;
import com.arcanc.nedaire.registration.NRegistration;
import com.arcanc.nedaire.util.helpers.BlockHelper;
import com.arcanc.nedaire.util.helpers.VoxelShapeHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FluidTransmitterBlock extends NBaseEntityBlock<FluidTransmitterBlockEntity>
{

    private static final Map<Direction, VoxelShape> BY_DIRECTION = new EnumMap<>(Direction.class);
    private static final VoxelShape SHAPE = Shapes.or(
            box(5,0,5, 11, 2, 11),
            box(7, 2, 7, 9, 5, 9),
            box(6,2,6, 7, 5, 7),
            box(6, 2, 9, 7, 5, 10),
            box(9, 2, 9, 10, 5, 10),
            box(9, 2, 6, 10 , 5, 7),
            box(6, 5, 7, 7, 6, 9),
            box(7, 5, 9, 9, 6, 10),
            box(7, 5, 6, 9, 6, 7),
            box(9, 5, 6,10, 6, 7),
            box(9, 5, 9, 10, 6, 10),
            box(6, 5, 9, 7, 6, 10),
            box(6, 5, 6, 7, 6, 7),
            box(9, 5, 7, 10, 6, 9),
            box(7, 6, 7, 9, 7, 9)
    );
    public static final MapCodec<FluidTransmitterBlock> CODEC = simpleCodec(FluidTransmitterBlock :: new);

    public FluidTransmitterBlock(Properties blockProps)
    {
        super(NRegistration.NBlockEntities.BE_FLUID_TRANSMITTER, blockProps);

        BY_DIRECTION.put(Direction.DOWN, SHAPE);
        BY_DIRECTION.put(Direction.UP, VoxelShapeHelper.rotateShape(Direction.DOWN, Direction.UP, SHAPE));
        BY_DIRECTION.put(Direction.NORTH, VoxelShapeHelper.rotateShape(Direction.DOWN, Direction.NORTH, SHAPE));
        BY_DIRECTION.put(Direction.SOUTH, VoxelShapeHelper.rotateShape(Direction.DOWN, Direction.SOUTH, SHAPE));
        BY_DIRECTION.put(Direction.EAST, VoxelShapeHelper.rotateShape(Direction.DOWN, Direction.EAST, SHAPE));
        BY_DIRECTION.put(Direction.WEST, VoxelShapeHelper.rotateShape(Direction.DOWN, Direction.WEST, SHAPE));

        Set<Direction> ext = EnumSet.of(Direction.DOWN);

    }

    @Override
    protected BlockState getInitDefaultState()
    {
        BlockState state = super.getInitDefaultState();
        if (state.hasProperty(BlockHelper.BlockProperties.FACING))
            state = state.setValue(BlockHelper.BlockProperties.FACING, Direction.NORTH);
        if (state.hasProperty(BlockHelper.BlockProperties.ENABLED))
            state = state.setValue(BlockHelper.BlockProperties.ENABLED, false);
        return state;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack)
    {
        super.setPlacedBy(level, pos, state, placer, stack);
        Direction dir = state.getValue(BlockHelper.BlockProperties.FACING);
        BlockPos targetPos = pos.offset(dir.getNormal());
        BlockState targetState = level.getBlockState(targetPos);
        if (!targetState.isFaceSturdy(level, targetPos, dir.getOpposite()))
           dropBlock(level, pos);

    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context)
    {
        Direction dir = context.getClickedFace();
        return super.getStateForPlacement(context).setValue(BlockHelper.BlockProperties.FACING, dir.getOpposite()).setValue(BlockHelper.BlockProperties.ENABLED, false);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected @NotNull BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirror)
    {
        return state.rotate(mirror.getRotation(state.getValue(BlockHelper.BlockProperties.FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    protected @NotNull BlockState rotate(@NotNull BlockState state, @NotNull Rotation rotation)
    {
        return BlockHelper.nextDirection(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder)
    {
        builder.add(BlockHelper.BlockProperties.FACING, BlockHelper.BlockProperties.ENABLED, BlockHelper.BlockProperties.WATERLOGGED);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context)
    {
        return BY_DIRECTION.get(state.getValue(BlockHelper.BlockProperties.FACING));
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context)
    {
        return BY_DIRECTION.get(state.getValue(BlockHelper.BlockProperties.FACING));
    }

    @Override
    public @NotNull VoxelShape getInteractionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos)
    {
        return BY_DIRECTION.get(state.getValue(BlockHelper.BlockProperties.FACING));
    }

    @Override
    protected @NotNull MapCodec<FluidTransmitterBlock> codec()
    {
        return CODEC;
    }
}
