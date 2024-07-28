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
import com.arcanc.nedaire.content.gui.slots.NSlot;
import com.arcanc.nedaire.content.gui.sync.GenericContainerData;
import com.arcanc.nedaire.content.gui.sync.GenericDataSerializers;
import com.arcanc.nedaire.content.nerwork.messages.NetworkEngine;
import com.arcanc.nedaire.content.nerwork.messages.packets.S2CContainerDataPacket;
import com.arcanc.nedaire.util.helpers.FluidHelper;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class NContainerMenu extends AbstractContainerMenu implements IScreenMessageReceive
{

    private final List<GenericContainerData<?>> genericData = new ArrayList<>();
    protected final List<ServerPlayer> usingPlayers = new ArrayList<>();
    private final Runnable setChanged;
    private final Predicate<Player> isValid;
    public int ownSlotCount;
    protected NContainerMenu(@NotNull MenuContext ctx)
    {
        super(ctx.type, ctx.id);
        this.setChanged = ctx.setChanged;
        this.isValid = ctx.isValid;
    }


    /*
     * Slot index = 10
     */
    /*protected void addItemFilterSlots(LazyOptional<IItemFilter> filter, int xPos, int yPos)
    {
        filter.ifPresent(fil ->
        {
            for (int q = 0; q < fil.getContent().getSlots(); q++)
            {
                this.addSlot(new NSlot.ItemHandlerGhost(fil.getContent(), 10, q, xPos + (q % 3) * 18, yPos + (q / 3) * 18).setBackground(InventoryMenu.BLOCK_ATLAS, NSlot.BACKGROUND_STANDARD).setActive(false));
            }
        });
    }*/

    /**
     * Slot index = 11
     */
    protected void addFluidFilterSlots(@NotNull IItemHandler filter, int xPos, int yPos)
    {
        for (int q = 0; q < filter.getSlots(); q++)
        {
            this.addSlot(new NSlot.ItemHandlerGhost(filter, 11, q, xPos + (q % 4) * 18, yPos + (q / 4) * 18, FluidHelper::isFluidHandler).
                    setBackground(InventoryMenu.BLOCK_ATLAS, NSlot.BACKGROUND_STANDARD));
        }
    }

    private int addSlotRange(Inventory handler, int index, int x, int y, int amount, int dx)
    {
        for (int i = 0; i < amount ; i++)
        {
            addSlot(new Slot(handler, index, x, y).setBackground(InventoryMenu.BLOCK_ATLAS, NSlot.BACKGROUND_STANDARD));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(Inventory handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy)
    {
        for (int j = 0 ; j < verAmount ; j++)
        {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    protected void layoutPlayerInventorySlots(Inventory playerInv, int leftCol, int topRow)
    {
        // Player inventory
        addSlotBox(playerInv, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInv, 0, leftCol, topRow, 9, 18);
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
                NetworkEngine.sendToPlayer(player, new S2CContainerDataPacket(toSync));
    }

    public void receiveSync (@NotNull List<Pair<Integer, GenericDataSerializers.DataPair<?>>> synced)
    {
        for(Pair<Integer, GenericDataSerializers.DataPair<?>> syncElement : synced)
            genericData.get(syncElement.getFirst()).processSync(syncElement.getSecond().data());
    }

    @Override
    public void clicked(int id, int dragType, @NotNull ClickType clickType, @NotNull Player player)
    {
        Slot slot = id < 0 ? null : this.slots.get(id);
        if (!(slot instanceof NSlot.ItemHandlerGhost))
        {
            super.clicked(id, dragType, clickType, player);
            return;
        }
        ItemStack stackSlot = slot.getItem();

        if (dragType == 2)
            slot.set(ItemStack.EMPTY);
        else if(dragType == 0 || dragType == 1)
        {
            ItemStack stackHeld = getCarried();

            int amount = Math.min(slot.getMaxStackSize(), stackHeld.getCount());
            if (dragType == 1)
                amount = 1;
            if (stackSlot.isEmpty())
            {
                if(!stackHeld.isEmpty() && slot.mayPlace(stackHeld))
                    slot.set(stackHeld.copyWithCount(amount));
            }
            else if (stackHeld.isEmpty())
                slot.set(ItemStack.EMPTY);
            else if(slot.mayPlace(stackHeld))
            {
                if (ItemStack.matches(stackSlot, stackHeld))
                    stackSlot.grow(amount);
                else
                    slot.set(stackHeld.copyWithCount(amount));
            }
            if (stackSlot.getCount() > slot.getMaxStackSize())
                stackSlot.setCount(slot.getMaxStackSize());
        }
        else if(dragType == 5)
        {
            ItemStack stackHeld = getCarried();
            int amount = Math.min(slot.getMaxStackSize(), stackHeld.getCount());
            if(!slot.hasItem())
                slot.set(stackHeld.copyWithCount(amount));
        }
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
        return inAllowedRange && move.moveItemStackTo(pStack, allowedStart, pEndIndex, false);
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
            serverPlayer.connection.send(new S2CContainerDataPacket(list));
        }
    }

    public static void onContainerClosed(final PlayerContainerEvent.@NotNull Close ev)
    {
        if(ev.getContainer() instanceof NContainerMenu nContainer &&
                ev.getEntity() instanceof ServerPlayer serverPlayer)
            nContainer.usingPlayers.remove(serverPlayer);
    }

    @Contract("_, _, _ -> new")
    public static @NotNull MenuContext blockCtx(MenuType<?> pMenuType, int pContainerId, BlockEntity be)
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
                return !(p.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) > 64.0D);
        });
    }

    @Contract("_, _, _, _, _ -> new")
    public static @NotNull MenuContext itemCtx(
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

    @Contract("_, _ -> new")
    public static @NotNull MenuContext clientCtx(MenuType<?> pMenuType, int pContainerId)
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
