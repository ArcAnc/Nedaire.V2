/**
 * @author ArcAnc
 * Created at: 26.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.util.inventory.fluids;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ManagedFluidHandler extends SimpleFluidHandler
{
    protected List<FluidStackHolder> inputSlots;
    protected List<FluidStackHolder> outputSlots;


    protected IFluidHandler inputHandler;
    protected IFluidHandler outputHandler;
    protected IFluidHandler allHandler;

    public ManagedFluidHandler (@Nullable IFluidCallback callback)
    {
        super(callback);
        this.inputSlots = new ArrayList<>();
        this.outputSlots = new ArrayList<>();
    }

    public ManagedFluidHandler addSlots(TankType type, int amount)
    {
        for (int q = 0; q < amount; q++)
            addTypedSlot(FluidStackHolder.newBuilder().
                    setTankType(type).
                    build());
        return this;
    }
    public ManagedFluidHandler addSlots(TankType type, int amount, Predicate<FluidStack> validator)
    {
        for (int q = 0; q < amount; q++)
            addTypedSlot(FluidStackHolder.newBuilder().
                    setTankType(type).
                    setValidator(validator).
                    build());
        return this;
    }

    public ManagedFluidHandler addTypedSlot(FluidStackHolder holder)
    {
        this.tanks.add(holder);
        switch (holder.getTankType())
        {
            case INPUT -> inputSlots.add(holder);
            case OUTPUT -> outputSlots.add(holder);
        }
        return this;
    }

    public void initHandlers()
    {
        inputHandler = new SimpleFluidHandler(inputSlots, callback);
        outputHandler = new SimpleFluidHandler(outputSlots, callback);
        allHandler = new SimpleFluidHandler(tanks, callback);
    }

    @Override
    public IFluidHandler getHandler(@NotNull TankType type)
    {
        return switch (type)
        {
            case ALL -> allHandler;
            case INPUT -> inputHandler;
            case OUTPUT -> outputHandler;
        };
    }
}
