/**
 * @author ArcAnc
 * Created at: 27.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.gui.container_menu;

import com.arcanc.nedaire.content.block.block_entity.FluidTransmitterBlockEntity;
import com.arcanc.nedaire.util.NDatabase;
import com.arcanc.nedaire.util.filter.FilterMethod;
import com.arcanc.nedaire.util.helpers.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FluidTransmitterContainer extends NContainerMenu
{

    public final FluidTransmitterBlockEntity blockEntity;

    public static @NotNull FluidTransmitterContainer makeServer(MenuType<?> type, int id, Inventory player, FluidTransmitterBlockEntity blockEntity)
    {
        return new FluidTransmitterContainer(blockCtx(type, id, blockEntity), player, blockEntity);
    }

    public static @NotNull FluidTransmitterContainer makeClient(MenuType<?> type, int id, @NotNull Inventory player, BlockPos pos)
    {
        Player p = player.player;
        Level l = p.level();
        FluidTransmitterBlockEntity be = BlockHelper.castTileEntity(l, pos, FluidTransmitterBlockEntity.class).get();
        return new FluidTransmitterContainer(clientCtx(type, id), player, be);
    }

    private FluidTransmitterContainer(@NotNull MenuContext ctx, @NotNull Inventory player, @NotNull FluidTransmitterBlockEntity blockEntity)
    {
        super(ctx);
        this.blockEntity = blockEntity;

        addFluidFilterSlots(blockEntity.getHandler(), 40, 15);

        this.ownSlotCount = blockEntity.getHandler().getSlots();

        layoutPlayerInventorySlots(player, 13, 90);
    }

    @Override
    public void receiveMessageFromScreen(CompoundTag nbt)
    {
        super.receiveMessageFromScreen(nbt);

        for (ServerPlayer player : usingPlayers)
        {
            ServerLevel level = player.serverLevel();
            BlockPos pos = new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
            if (nbt.contains(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.Filter.FILTER_TAG))
            {
                BlockHelper.castTileEntity(level, pos, FluidTransmitterBlockEntity.class).ifPresent(tile ->
                {
                    tile.setFilterMethod(FilterMethod.readFromNbt(nbt.getCompound(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.Filter.FILTER_TAG)));
                    tile.setChanged();
                });
            }
            if (nbt.contains(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.FluidTransmitter.TRANSFER_AMOUNT))
            {
                BlockHelper.castTileEntity(level, pos, FluidTransmitterBlockEntity.class).ifPresent(tile ->
                {
                    tile.setTransferAmount(nbt.getInt(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.FluidTransmitter.TRANSFER_AMOUNT));
                    tile.setChanged();
                });
            }
        }
    }
}
