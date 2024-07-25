/**
 * @author ArcAnc
 * Created at: 23.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.gui.container_menu;

import com.arcanc.nedaire.content.block.block_entity.NBaseBlockEntity;
import com.arcanc.nedaire.content.nerwork.messages.packets.S2CPacketContainerData;
import com.arcanc.nedaire.content.gui.sync.GenericContainerData;
import com.arcanc.nedaire.content.gui.sync.GenericDataSerializers;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class NContainerMenu extends AbstractContainerMenu implements IScreenMessageReceive
{

    private final List<GenericContainerData<?>> genericData = new ArrayList<>();
    private final List<ServerPlayer> usingPlayers = new ArrayList<>();
    private final Runnable setChanged;
    private final Predicate<Player> isValid;
    public int ownSlotCount;
    protected NContainerMenu(@NotNull MenuContext ctx)
    {
        super(ctx.type, ctx.id);
        this.setChanged = ctx.setChanged;
        this.isValid = ctx.isValid;
    }

    public void addGenericData(GenericContainerData<?> newData)
    {
        genericData.add(newData);
    }

    @Override
    public void broadcastChanges()
    {
        super.broadcastChanges();
        List<Pair<Integer, GenericDataSerializers.DataPair<?>>> toSync = new ArrayList<>();
        for(int i = 0; i < genericData.size(); i++)
        {
            GenericContainerData<?> data = genericData.get(i);
            if(data.needsUpdate())
                toSync.add(Pair.of(i, data.dataPair()));
        }
        if(!toSync.isEmpty())
            for(ServerPlayer player : usingPlayers)
                player.connection.send(new S2CPacketContainerData(toSync));
    }

    public void receiveSync (@NotNull List<Pair<Integer, GenericDataSerializers.DataPair<?>>> synced)
    {
        for(Pair<Integer, GenericDataSerializers.DataPair<?>> syncElement : synced)
            genericData.get(syncElement.getFirst()).processSync(syncElement.getSecond().data());
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slot)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slotObject = this.slots.get(slot);
        if(slotObject.hasItem())
        {
            ItemStack itemstack1 = slotObject.getItem();
            itemstack = itemstack1.copy();
            if(slot < ownSlotCount)
            {
                if(!this.moveItemStackTo(itemstack1, ownSlotCount, this.slots.size(), true))
                    return ItemStack.EMPTY;
            }
            else if(!this.moveItemStackToWithMayPlace(itemstack1, 0, ownSlotCount))
                return ItemStack.EMPTY;

            if(itemstack1.isEmpty())
                slotObject.set(ItemStack.EMPTY);
            else
                slotObject.setChanged();
        }

        return itemstack;
    }

    protected boolean moveItemStackToWithMayPlace(ItemStack pStack, int pStartIndex, int pEndIndex)
    {
        return moveItemStackToWithMayPlace(slots, this :: moveItemStackTo, pStack, pStartIndex, pEndIndex);
    }

    public static boolean moveItemStackToWithMayPlace(
            List<Slot> slots, MoveItemsFunc move, ItemStack pStack, int pStartIndex, int pEndIndex
    )
    {
        boolean inAllowedRange = true;
        int allowedStart = pStartIndex;
        for(int i = pStartIndex; i < pEndIndex; i++)
        {
            boolean mayPlace = slots.get(i).mayPlace(pStack);
            if(inAllowedRange&&!mayPlace)
            {
                if(move.moveItemStackTo(pStack, allowedStart, i, false))
                    return true;
                inAllowedRange = false;
            }
            else if(!inAllowedRange&&mayPlace)
            {
                allowedStart = i;
                inAllowedRange = true;
            }
        }
        return inAllowedRange&&move.moveItemStackTo(pStack, allowedStart, pEndIndex, false);
    }

    @Override
    public void removed(@NotNull Player player)
    {
        super.removed(player);
        setChanged.run();
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer)
    {
        return isValid.test(pPlayer);
    }

    public static void onContainerOpened(final PlayerContainerEvent.@NotNull Open ev)
    {
        if(ev.getContainer() instanceof NContainerMenu ieContainer&&ev.getEntity() instanceof ServerPlayer serverPlayer)
        {
            ieContainer.usingPlayers.add(serverPlayer);
            List<Pair<Integer, GenericDataSerializers.DataPair<?>>> list = new ArrayList<>();
            for(int i = 0; i < ieContainer.genericData.size(); i++)
                list.add(Pair.of(i, ieContainer.genericData.get(i).dataPair()));
            serverPlayer.connection.send(new S2CPacketContainerData(list));
        }
    }

    public static void onContainerClosed(final PlayerContainerEvent.@NotNull Close ev)
    {
        if(ev.getContainer() instanceof NContainerMenu ieContainer&&ev.getEntity() instanceof ServerPlayer serverPlayer)
            ieContainer.usingPlayers.remove(serverPlayer);
    }

    public static MenuContext blockCtx(MenuType<?> pMenuType, int pContainerId, BlockEntity be)
    {
        return new MenuContext(pMenuType, pContainerId, () -> {
            be.setChanged();
            if(be instanceof NBaseBlockEntity nBE)
                nBE.markContainingBlockForUpdate(null);
        }, p -> {
            BlockPos pos = be.getBlockPos();
            Level level = be.getLevel();
            if(level==null||level.getBlockEntity(pos)!=be)
                return false;
            else
                return !(p.distanceToSqr(pos.getX()+0.5D, pos.getY()+0.5D, pos.getZ()+0.5D) > 64.0D);
        });
    }

    public static MenuContext itemCtx(
            MenuType<?> pMenuType, int pContainerId, Inventory playerInv, EquipmentSlot slot, ItemStack stack
    )
    {
        return new MenuContext(pMenuType, pContainerId, () -> {
        }, p -> {
            if(p!=playerInv.player)
                return false;
            return ItemStack.isSameItem(p.getItemBySlot(slot), stack);
        });
    }

    public static MenuContext clientCtx(MenuType<?> pMenuType, int pContainerId)
    {
        return new MenuContext(pMenuType, pContainerId, () -> {
        }, $ -> true);
    }

    protected record MenuContext(
            MenuType<?> type, int id, Runnable setChanged, Predicate<Player> isValid
    )
    {
    }

    public interface MoveItemsFunc
    {
        boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection);
    }
}
