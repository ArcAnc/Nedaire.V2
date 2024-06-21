/**
 * @author ArcAnc
 * Created at: 18.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.fluid;

import net.minecraft.Util;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class NFluid extends BaseFlowingFluid
{

    public static NFluid makeFluid(Function<Properties, ? extends NFluid> make, Properties properties, Consumer<Properties> props)
    {
        return make.apply(Util.make(properties, props));
    }

    protected NFluid(Properties properties)
    {
        super(properties);
    }

    public static class NFluidSource extends NFluid
    {

        public NFluidSource(Properties properties)
        {
            super(properties);
        }

        @Override
        public int getAmount(@Nonnull FluidState state)
        {
            return 8;
        }

        @Override
        public boolean isSource(@Nonnull FluidState state)
        {
            return true;
        }
    }

    public static class NFluidFlowing extends NFluid
    {

        public NFluidFlowing(Properties properties)
        {
            super(properties);
            registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
        }

        @Override
        public int getAmount(FluidState state)
        {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(@Nonnull FluidState state)
        {
            return false;
        }

        @Override
        protected void createFluidStateDefinition(@Nonnull StateDefinition.Builder<Fluid, FluidState> builder)
        {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

    }

    public interface FluidPropsGetter
    {
        BaseFlowingFluid.Properties get(Supplier<? extends FluidType> fluidType, Supplier<? extends Fluid> still, Supplier<? extends Fluid> flowing);
    }

}

