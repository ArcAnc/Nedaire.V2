/**
 * @author ArcAnc
 * Created at: 23.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.block;

import com.arcanc.nedaire.content.block.block_entity.ticking.NClientTickableBE;
import com.arcanc.nedaire.content.block.block_entity.ticking.NServerTickableBE;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/*Part of code was taken from Immersive Engineering. Thanks BluSunrize*/
public class NBaseEntityBlock<T extends BlockEntity> extends NBlockBase implements EntityBlock
{

    private final BiFunction<BlockPos, BlockState, T> makeEntity;
    private BEClassInspectedData classData;

    public NBaseEntityBlock(BiFunction<BlockPos, BlockState, T> makeEntity, Properties blockProps)
    {
        super(blockProps);
        this.makeEntity = makeEntity;
    }

    public NBaseEntityBlock(Supplier<BlockEntityType<T>> tileType, Properties blockProps)
    {
        this((bp, state) -> tileType.get().create(bp, state), blockProps);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState)
    {
        return makeEntity.apply(pPos, pState);
    }

    @Nullable
    @Override
    public <T2 extends BlockEntity>
    BlockEntityTicker<T2> getTicker(@NotNull Level world, @NotNull BlockState state, @NotNull BlockEntityType<T2> type)
    {
        return getClassData().makeBaseTicker(world.isClientSide);
    }

    @Override
    public boolean triggerEvent(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, int eventID, int eventParam)
    {
        super.triggerEvent(state, worldIn, pos, eventID, eventParam);
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        return tileentity !=null && tileentity.triggerEvent(eventID, eventParam);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack pStack,
                                                       @NotNull BlockState pState,
                                                       @NotNull Level pLevel,
                                                       @NotNull BlockPos pPos,
                                                       @NotNull Player pPlayer,
                                                       @NotNull InteractionHand pHand,
                                                       @NotNull BlockHitResult pHitResult)
    {
        /*if (tile instanceof BlockInterfaces.INWrencheble interaction)
        {
            ItemInteractionResult res = interaction.onUsed(pStack, pState, pLevel, pPos, pPlayer, pHand, pHitResult);
            if (res.consumesAction() || res == ItemInteractionResult.FAIL)
                return res;
        }*/
        return super.useItemOn(pStack, pState, pLevel, pPos, pPlayer, pHand, pHitResult);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state,
                                                        @NotNull Level level,
                                                        @NotNull BlockPos pos,
                                                        @NotNull Player player,
                                                        @NotNull BlockHitResult hitResult)
    {
        BlockEntity tile = level.getBlockEntity(pos);
        if(tile instanceof MenuProvider menuProvider && !player.isShiftKeyDown())
        {
            if(player instanceof ServerPlayer serverPlayer)
            {
                if(menuProvider instanceof BlockInterfaces.INInteractionObject<?> interaction)
                {
                    interaction = interaction.getGuiMaster();
                    if(interaction != null && interaction.canUseGui(player))
                    {
                        serverPlayer.openMenu(interaction);
                    }
                }
                else
                    serverPlayer.openMenu(menuProvider);
            }
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    private BEClassInspectedData getClassData()
    {
        if(this.classData==null)
        {
            T tempBE = makeEntity.apply(BlockPos.ZERO, getInitDefaultState());
            this.classData = new BEClassInspectedData(
                    tempBE instanceof NServerTickableBE,
                    tempBE instanceof NClientTickableBE
            );
        }
        return this.classData;
    }

    private record BEClassInspectedData(
            boolean serverTicking,
            boolean clientTicking
    )
    {
        @Nullable
        public <T extends BlockEntity> BlockEntityTicker<T> makeBaseTicker(boolean isClient)
        {
            if(serverTicking && !isClient)
                return NServerTickableBE.makeTicker();
            else if(clientTicking && isClient)
                return NClientTickableBE.makeTicker();
            else
                return null;
        }
    }
}
