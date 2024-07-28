/**
 * @author ArcAnc
 * Created at: 14.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.block;

import com.arcanc.nedaire.util.helpers.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.ticks.ScheduledTick;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NBlockBase extends Block implements SimpleWaterloggedBlock
{
    public NBlockBase(Properties props)
    {
        super(props);

        this.registerDefaultState(getInitDefaultState());
    }

    protected BlockState getInitDefaultState ()
    {
        BlockState state = this.stateDefinition.any();
        if (state.hasProperty(BlockHelper.BlockProperties.WATERLOGGED))
        {
            state = state.setValue(BlockHelper.BlockProperties.WATERLOGGED, Boolean.FALSE);
        }
        return state;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean triggerEvent(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, int eventId, int eventParam)
    {
        if (world.isClientSide() && eventId == 255)
        {
            world.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
            return true;
        }
        return super.triggerEvent(state, world, pos, eventId, eventParam);
    }

    @Override
    public @NotNull String getDescriptionId()
    {
        return BlockHelper.getRegistryName(this).withPrefix("block.").toLanguageKey().replace(':', '.').replace('/', '.');
    }

    /**
     * WATERLOGGING
     */

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context)
    {
        BlockState state = this.defaultBlockState();
        if (state.hasProperty(BlockHelper.BlockProperties.WATERLOGGED))
        {
            return state.setValue(BlockHelper.BlockProperties.WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
        }
        return state;
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction dir, @NotNull BlockState facingState, @NotNull LevelAccessor world, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos)
    {
        if (state.hasProperty(BlockHelper.BlockProperties.WATERLOGGED) && state.getValue(BlockHelper.BlockProperties.WATERLOGGED))
        {
            world.getFluidTicks().schedule(new ScheduledTick<>(Fluids.WATER, currentPos, Fluids.WATER.getTickDelay(world), 0));
        }
        return super.updateShape(state, dir, facingState, world, currentPos, facingPos);
    }

    @Override
    public @NotNull FluidState getFluidState(@NotNull BlockState state)
    {
        if (state.hasProperty(BlockHelper.BlockProperties.WATERLOGGED) && state.getValue(BlockHelper.BlockProperties.WATERLOGGED))
        {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState(state);
    }

    @Override
    public boolean canPlaceLiquid(@Nullable Player player, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Fluid liquid)
    {
        return state.hasProperty(BlockHelper.BlockProperties.WATERLOGGED) && SimpleWaterloggedBlock.super.canPlaceLiquid(player, world, pos, state, liquid);
    }

    @Override
    public boolean placeLiquid(@NotNull LevelAccessor world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull FluidState liquid)
    {
        return state.hasProperty(BlockHelper.BlockProperties.WATERLOGGED) && SimpleWaterloggedBlock.super.placeLiquid(world, pos, state, liquid);
    }

    @Override
    public @NotNull ItemStack pickupBlock(@Nullable Player player, @NotNull LevelAccessor world, @NotNull BlockPos pos, @NotNull BlockState state)
    {
        if (state.hasProperty(BlockHelper.BlockProperties.WATERLOGGED))
        {
            return SimpleWaterloggedBlock.super.pickupBlock(player, world, pos, state);
        }
        return ItemStack.EMPTY;
    }

    protected void dropBlock(@NotNull Level level, BlockPos pos)
    {
        level.destroyBlock(pos, true);
        level.sendBlockUpdated(pos, defaultBlockState(), level.getBlockState(pos), UPDATE_ALL);
    }
}
