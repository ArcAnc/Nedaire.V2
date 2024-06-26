/**
 * @author ArcAnc
 * Created at: 21.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.util.inventory.items;

import com.arcanc.nedaire.util.NDatabase;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.List;

public class SimpleItemHandler implements IItemHandler, INBTSerializable<CompoundTag>
{
    protected List<ItemStackHolder> slots;
    @Nullable
    protected IInventoryCallback callback;

    public SimpleItemHandler()
    {
        this(new ArrayList<>(), null);
    }
    public SimpleItemHandler(@Nullable IInventoryCallback callback)
    {
        this(new ArrayList<>(), callback);
    }
    public SimpleItemHandler(@NotNull List<ItemStackHolder> holderList)
    {
        this(holderList, null);
    }
    public SimpleItemHandler(@NotNull List<ItemStackHolder> holderList, @Nullable IInventoryCallback callback)
    {
        this.slots = new ArrayList<>(holderList);
        this.callback = callback;
    }

    public ItemStackHolder.ExtBuilder<SimpleItemHandler> addSlot()
    {
        return ItemStackHolder.newBuilder(this);
    }

    public void finishSlot(@NotNull ItemStackHolder slot)
    {
        this.slots.add(slot);
    }
    @Override
    public int getSlots()
    {
        return slots.size();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot)
    {
        validateSlotIndex(slot);
        return this.slots.get(slot).getStack();
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
    {
        validateSlotIndex(slot);
        ItemStack ret = this.slots.get(slot).insertItem(stack, simulate);
        onInventoryChange(slot);
        return ret;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        validateSlotIndex(slot);
        ItemStack ret = this.slots.get(slot).extractItem(amount, simulate);
        onInventoryChange(slot);
        return ret;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        validateSlotIndex(slot);
        return this.slots.get(slot).getCapacity();
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack)
    {
        validateSlotIndex(slot);
        return this.slots.get(slot).isItemValid(stack);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider)
    {
        ListTag list = new ListTag();
        for (int q = 0; q < getSlots(); q++)
        {
            ItemStackHolder holder = this.slots.get(q);

            if (!holder.isEmpty())
            {
                CompoundTag itemTag = holder.serializeNBT(provider);
                itemTag.putInt(NDatabase.CapabilitiesInfo.InventoryInfo.SLOT, q);
                list.add(q, itemTag);
            }
        }
        CompoundTag fullTag = new CompoundTag();
        fullTag.put(NDatabase.CapabilitiesInfo.InventoryInfo.ITEMS, list);
        return fullTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt)
    {
        ListTag list = nbt.getList(NDatabase.CapabilitiesInfo.InventoryInfo.ITEMS, Tag.TAG_COMPOUND);
        for (int q = 0; q < list.size(); q++)
        {
            CompoundTag itemTag = list.getCompound(q);
            int slot = itemTag.getInt(NDatabase.CapabilitiesInfo.InventoryInfo.SLOT);

            if (slot >= 0 && slot < this.slots.size())
            {
                this.slots.get(slot).deserializeNBT(provider, itemTag);
                onInventoryChange(slot);
            }
        }
    }

    public void onInventoryChange(int slot)
    {
        if (callback == null)
            return;
        validateSlotIndex(slot);
        callback.onInventoryChanged(slot);
    }

    public void clear(int slot)
    {
        if (callback == null)
            return;
        validateSlotIndex(slot);
        callback.clearSlot(slot);
        callback.onInventoryChanged(slot);
    }

    public IItemHandler getHandler(SlotType type)
    {
        return this;
    }

    protected void validateSlotIndex(int slot)
    {
        if (slot < 0 || slot >= slots.size())
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + slots.size() + ")");
    }
}
