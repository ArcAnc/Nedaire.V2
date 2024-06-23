/**
 * @author ArcAnc
 * Created at: 23.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.gui.sync;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenericContainerData<T>
{
    private final GenericDataSerializers.DataSerializer<T> serializer;
    private final Supplier<T> get;
    private final Consumer<T> set;
    private T current;

    public GenericContainerData(GenericDataSerializers.DataSerializer<T> serializer, Supplier<T> get, Consumer<T> set)
    {
        this.serializer = serializer;
        this.get = get;
        this.set = set;
    }

    public GenericContainerData(GenericDataSerializers.DataSerializer<T> serializer, @NotNull GetterAndSetter<T> io)
    {
        this.serializer = serializer;
        this.get = io.getter();
        this.set = io.setter();
    }

    public static @NotNull GenericContainerData<Integer> int32(Supplier<Integer> get, Consumer<Integer> set)
    {
        return new GenericContainerData<>(GenericDataSerializers.INT32, get, set);
    }

    public static GenericContainerData<FluidStack> fluid(@NotNull FluidTank tank)
    {
        return new GenericContainerData<>(GenericDataSerializers.FLUID_STACK, tank::getFluid, tank::setFluid);
    }

    public static @NotNull GenericContainerData<Boolean> bool(Supplier<Boolean> get, Consumer<Boolean> set)
    {
        return new GenericContainerData<>(GenericDataSerializers.BOOLEAN, get, set);
    }

    public static @NotNull GenericContainerData<Float> float32(Supplier<Float> get, Consumer<Float> set)
    {
        return new GenericContainerData<>(GenericDataSerializers.FLOAT, get, set);
    }

    public boolean needsUpdate()
    {
        T newValue = get.get();
        if(newValue==null&&current==null)
            return false;
        if(current!=null&&newValue!=null&&serializer.equals().test(current, newValue))
            return false;
        current = serializer.copy().apply(newValue);
        return true;
    }

    @SuppressWarnings("unchecked")
    public void processSync(Object receivedData)
    {
        current = (T)receivedData;
        set.accept(serializer.copy().apply(current));
    }

    public GenericDataSerializers.DataPair<T> dataPair()
    {
        return new GenericDataSerializers.DataPair<>(serializer, current);
    }
}
