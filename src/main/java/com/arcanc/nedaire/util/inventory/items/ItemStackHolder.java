/**
 * @author ArcAnc
 * Created at: 20.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.util.inventory.items;

import com.arcanc.nedaire.util.NDatabase;
import com.google.common.base.Preconditions;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class ItemStackHolder
{
    protected ItemStack stack;
    protected SlotType slotType;
    protected Predicate<ItemStack> validator;
    protected int capacity;
    protected HolderCallback callback;
    public ItemStackHolder(ItemStack stack, int capacity, SlotType slotType, Predicate<ItemStack> validator, HolderCallback callback)
    {
        Preconditions.checkArgument(capacity > 0);
        this.stack = Preconditions.checkNotNull(stack);
        this.capacity = capacity;
        this.slotType = Preconditions.checkNotNull(slotType);
        this.validator = Preconditions.checkNotNull(validator);
        this.callback = Preconditions.checkNotNull(callback);
    }

    public ItemStack getStack()
    {
        return stack;
    }

    public void setStack(ItemStack stack)
    {
        this.stack = stack;
        update();
    }

    public void setAmount(int amount)
    {
        this.stack.setCount(amount);
        update();
    }

    public SlotType getSlotType()
    {
        return slotType;
    }

    public int getCapacity()
    {
        return capacity;
    }

    public Predicate<ItemStack> getValidator()
    {
        return validator;
    }

    public HolderCallback getCallback() {
        return callback;
    }

    public @NotNull ItemStack insertItem(@NotNull ItemStack stack, boolean simulate)
    {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!isItemValid(stack))
            return stack;

        if (isEmpty())
        {
            int maxStack = getCapacity();
            int count = Math.min(stack.getCount(), maxStack);
            if (!simulate)
                setStack(stack.copyWithCount(count));
            return count >= stack.getCount() ? ItemStack.EMPTY : stack.copyWithCount(stack.getCount() - count);
        }
        else if (ItemStack.isSameItemSameComponents(this.stack, stack))
        {
            int totalCount = stack.getCount() + stack.getCount();
            int limit = getCapacity();
            if (totalCount <= limit)
            {
                if (!simulate)
                    setAmount(totalCount);
                return ItemStack.EMPTY;
            }
            if (!simulate)
                setAmount(limit);
            return stack.copyWithCount(totalCount - limit);
        }
        return stack;
    }

    public @NotNull ItemStack extractItem(int amount, boolean simulate)
    {
        if (amount <= 0 || stack.isEmpty())
            return ItemStack.EMPTY;
        int retCount = Math.min(stack.getCount(), amount);
        ItemStack ret = stack.copyWithCount(retCount);
        if (!simulate)
        {
            stack.shrink(retCount);
            if (stack.isEmpty())
                this.stack = ItemStack.EMPTY;
            update();
        }
        return ret;
    }

    public boolean isItemValid(ItemStack stack)
    {
        return this.validator.test(stack);
    }

    public boolean isEmpty()
    {
        return this.stack.isEmpty() || this.stack.getCount() == 0;
    }

    public boolean isFull()
    {
        return this.stack.getCount() >= this.capacity;
    }

    public void clear()
    {
        this.stack = ItemStack.EMPTY;
        update();
    }

    public CompoundTag serializeNBT(HolderLookup.Provider provider)
    {
        CompoundTag tag = new CompoundTag();

        tag.put(NDatabase.CapabilitiesInfo.InventoryInfo.ItemStackHolderInfo.ITEM, this.stack.saveOptional(provider));
        tag.putInt(NDatabase.CapabilitiesInfo.InventoryInfo.ItemStackHolderInfo.CAPACITY, this.capacity);
        tag.putInt(NDatabase.CapabilitiesInfo.InventoryInfo.ItemStackHolderInfo.SLOT_TYPE, this.slotType.ordinal());

        return tag;
    }

    public void deserializeNBT(HolderLookup.Provider provider, @NotNull CompoundTag tag)
    {
        this.setStack(ItemStack.parseOptional(provider, tag.getCompound(NDatabase.CapabilitiesInfo.InventoryInfo.ItemStackHolderInfo.ITEM)));
        this.capacity = tag.getInt(NDatabase.CapabilitiesInfo.InventoryInfo.ItemStackHolderInfo.CAPACITY);
        this.slotType = SlotType.values()[tag.getInt(NDatabase.CapabilitiesInfo.InventoryInfo.ItemStackHolderInfo.SLOT_TYPE)];
    }

    public void update()
    {
        this.callback.update(this);
    }

    public static @NotNull Builder newBuilder()
    {
        return new Builder();
    }

    public static <T extends SimpleItemHandler> @NotNull ExtBuilder<T> newBuilder(T handler)
    {
        return new ExtBuilder<>(handler);
    }

    public static class Builder
    {
        private ItemStack stack = ItemStack.EMPTY;
        private SlotType slotType = SlotType.ALL;
        private Predicate<ItemStack> validator = itemStack -> true;
        private int capacity = 64;
        private HolderCallback callback = holder -> {};

        private Builder ()
        {}

        public Builder setCapacity(int capacity)
        {
            this.capacity = capacity;
            return this;
        }

        public Builder setSlotType(SlotType slotType)
        {
            this.slotType = slotType;
            return this;
        }

        public Builder setStack(ItemStack stack)
        {
            this.stack = stack;
            return this;
        }

        public Builder setValidator(Predicate<ItemStack> validator)
        {
            this.validator = validator;
            return this;
        }

        public Builder setCallback(HolderCallback callback)
        {
            this.callback = callback;
            return this;
        }

        public @NotNull ItemStackHolder build()
        {
            return new ItemStackHolder(this.stack, this.capacity, this.slotType, this.validator, this.callback);
        }
    }

    public static class ExtBuilder<T extends SimpleItemHandler> extends Builder
    {
        private final T handler;
        public ExtBuilder(T handler)
        {
            this.handler = handler;
        }

        @Override
        public ExtBuilder<T> setCallback(HolderCallback callback)
        {
            super.setCallback(callback);
            return this;
        }

        @Override
        public ExtBuilder<T> setCapacity(int capacity)
        {
            super.setCapacity(capacity);
            return this;
        }

        @Override
        public ExtBuilder<T> setSlotType(SlotType slotType)
        {
            super.setSlotType(slotType);
            return this;
        }

        @Override
        public ExtBuilder<T> setStack(ItemStack stack)
        {
            super.setStack(stack);
            return this;
        }

        @Override
        public ExtBuilder<T> setValidator(Predicate<ItemStack> validator)
        {
            super.setValidator(validator);
            return this;
        }

        public @NotNull T finishSlot()
        {
            handler.finishSlot(build());
            return handler;
        }
    }

    @FunctionalInterface
    public interface HolderCallback
    {
        void update(ItemStackHolder holder);
    }
}
