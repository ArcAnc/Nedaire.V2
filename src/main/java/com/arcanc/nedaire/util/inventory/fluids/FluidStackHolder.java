/**
 * @author ArcAnc
 * Created at: 26.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.util.inventory.fluids;

import com.arcanc.nedaire.util.NDatabase;
import com.google.common.base.Preconditions;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class FluidStackHolder implements IFluidTank
{
    protected FluidStack fluid;
    protected TankType tankType;
    protected Predicate<FluidStack> validator;
    protected int capacity;
    protected HolderCallback callback;
    public FluidStackHolder(FluidStack stack, int capacity, TankType tankType, Predicate<FluidStack> validator, HolderCallback callback)
    {
        Preconditions.checkArgument(capacity > 0);
        this.fluid = Preconditions.checkNotNull(stack);
        this.capacity = capacity;
        this.tankType = Preconditions.checkNotNull(tankType);
        this.validator = Preconditions.checkNotNull(validator);
        this.callback = Preconditions.checkNotNull(callback);
    }

    public @NotNull @Override FluidStack getFluid()
    {
        return fluid;
    }

    @Override
    public int getFluidAmount()
    {
        return fluid.getAmount();
    }

    public void setFluid(FluidStack fluid)
    {
        this.fluid = fluid;
        update();
    }

    public void setAmount(int amount)
    {
        this.fluid.setAmount(amount);
        update();
    }

    public TankType getTankType()
    {
        return tankType;
    }

    @Override
    public int getCapacity()
    {
        return capacity;
    }

    public Predicate<FluidStack> getValidator()
    {
        return validator;
    }

    public HolderCallback getCallback() {
        return callback;
    }

    @Override
    public int fill(@NotNull FluidStack stack, IFluidHandler.@NotNull FluidAction simulate)
    {
        if (stack.isEmpty() || !isFluidValid(stack))
            return 0;
        if (simulate.simulate())
        {
            if (this.fluid.isEmpty())
                return Math.min(capacity, stack.getAmount());
            if (!FluidStack.isSameFluidSameComponents(fluid,stack))
                return 0;
            return Math.min(capacity - fluid.getAmount(), stack.getAmount());
        }
        if (fluid.isEmpty())
        {
            setFluid(new FluidStack(stack.getFluid(), Math.min(capacity, stack.getAmount())));
            return fluid.getAmount();
        }
        if (!FluidStack.isSameFluidSameComponents(fluid, stack))
            return 0;
        int filled = capacity - fluid.getAmount();

        if (stack.getAmount() < filled)
        {
            fluid.setAmount(fluid.getAmount() + stack.getAmount());
            filled = stack.getAmount();
        }
        else
            fluid.setAmount(capacity);
        return filled;
    }

    @Override
    public @NotNull FluidStack drain(@NotNull FluidStack stack, IFluidHandler.@NotNull FluidAction simulate)
    {

        if (stack.isEmpty() || !FluidStack.isSameFluidSameComponents(stack, fluid))
            return FluidStack.EMPTY;
        return drain(stack.getAmount(), simulate);
    }

    @Override
    public @NotNull FluidStack drain(int amount, IFluidHandler.@NotNull FluidAction simulate)
    {
        if (amount <= 0 || fluid.isEmpty())
            return FluidStack.EMPTY;

        int drained = amount;

        if (fluid.getAmount() < drained)
            drained = fluid.getAmount();
        FluidStack stack = new FluidStack(fluid.getFluid(), drained);
        if (simulate.execute())
        {
            fluid.shrink(drained);
            if (fluid.isEmpty())
                this.fluid = FluidStack.EMPTY;
            update();
        }
        return stack;
    }

    @Override
    public boolean isFluidValid(@NotNull FluidStack stack)
    {
        return this.validator.test(stack);
    }

    public boolean isEmpty()
    {
        return this.fluid.isEmpty() || this.fluid.getAmount() == 0;
    }

    public boolean isFull()
    {
        return this.fluid.getAmount() >= this.capacity;
    }

    public void clear()
    {
        this.fluid = FluidStack.EMPTY;
        update();
    }

    public CompoundTag serializeNBT(HolderLookup.Provider provider)
    {
        CompoundTag tag = new CompoundTag();

        tag.put(NDatabase.CapabilitiesInfo.FluidInfo.FluidStackHolderInfo.FLUID, this.fluid.saveOptional(provider));
        tag.putInt(NDatabase.CapabilitiesInfo.FluidInfo.FluidStackHolderInfo.CAPACITY, this.capacity);
        tag.putInt(NDatabase.CapabilitiesInfo.FluidInfo.FluidStackHolderInfo.TANK_TYPE, this.tankType.ordinal());

        return tag;
    }

    public void deserializeNBT(HolderLookup.Provider provider, @NotNull CompoundTag tag)
    {
        this.setFluid(FluidStack.parseOptional(provider, tag.getCompound(NDatabase.CapabilitiesInfo.FluidInfo.FluidStackHolderInfo.FLUID)));
        this.capacity = tag.getInt(NDatabase.CapabilitiesInfo.FluidInfo.FluidStackHolderInfo.CAPACITY);
        this.tankType = TankType.values()[tag.getInt(NDatabase.CapabilitiesInfo.FluidInfo.FluidStackHolderInfo.TANK_TYPE)];
    }

    public void update()
    {
        this.callback.update(this);
    }

    public static @NotNull Builder newBuilder()
    {
        return new Builder();
    }

    public static <T extends SimpleFluidHandler> @NotNull ExtBuilder<T> newBuilder(T handler)
    {
        return new ExtBuilder<>(handler);
    }

    public static class Builder
    {
        private FluidStack stack = FluidStack.EMPTY;
        private TankType tankType = TankType.ALL;
        private Predicate<FluidStack> validator = FluidStack -> true;
        private int capacity = 64;
        private HolderCallback callback = holder -> {};

        private Builder ()
        {}

        public Builder setCapacity(int capacity)
        {
            this.capacity = capacity;
            return this;
        }

        public Builder setTankType(TankType tankType)
        {
            this.tankType = tankType;
            return this;
        }

        public Builder setStack(FluidStack stack)
        {
            this.stack = stack;
            return this;
        }

        public Builder setValidator(Predicate<FluidStack> validator)
        {
            this.validator = validator;
            return this;
        }

        public Builder setCallback(HolderCallback callback)
        {
            this.callback = callback;
            return this;
        }

        public @NotNull FluidStackHolder build()
        {
            return new FluidStackHolder(this.stack, this.capacity, this.tankType, this.validator, this.callback);
        }
    }

    public static class ExtBuilder<T extends SimpleFluidHandler> extends FluidStackHolder.Builder
    {
        private final T handler;
        public ExtBuilder(T handler)
        {
            this.handler = handler;
        }

        @Override
        public FluidStackHolder.ExtBuilder<T> setCallback(FluidStackHolder.HolderCallback callback)
        {
            super.setCallback(callback);
            return this;
        }

        @Override
        public FluidStackHolder.ExtBuilder<T> setCapacity(int capacity)
        {
            super.setCapacity(capacity);
            return this;
        }

        @Override
        public ExtBuilder<T> setTankType(TankType tankType)
        {
            super.setTankType(tankType);
            return this;
        }

        @Override
        public ExtBuilder<T> setStack(FluidStack stack)
        {
            super.setStack(stack);
            return this;
        }

        @Override
        public ExtBuilder<T> setValidator(Predicate<FluidStack> validator)
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
        void update(FluidStackHolder holder);
    }
}
