/**
 * @author ArcAnc
 * Created at: 28.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.block.block_entity;

import com.arcanc.nedaire.registration.NRegistration;
import com.arcanc.nedaire.util.NDatabase;
import com.arcanc.nedaire.util.inventory.fluids.SimpleFluidHandler;
import com.arcanc.nedaire.util.inventory.fluids.TankType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class FluidStorageBlockEntity extends NBaseBlockEntity
{
    protected SimpleFluidHandler handler;

    public FluidStorageBlockEntity(BlockPos pos, BlockState state)
    {
        super(NRegistration.NBlockEntities.BE_FLUID_STORAGE.get(), pos, state);

        this.handler = new SimpleFluidHandler().
                addSlot().
                setCallback(holder -> setChanged()).
                setCapacity(5000).
                setTankType(TankType.ALL).
                finishSlot();
    }

    @Override
    public void readCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        handler.deserializeNBT(registries, tag.getCompound(NDatabase.CapabilitiesInfo.FluidInfo.CAPABILITY_NAME));
    }

    @Override
    public void writeCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacker)
    {
        tag.put(NDatabase.CapabilitiesInfo.FluidInfo.CAPABILITY_NAME, handler.serializeNBT(registries));
    }

    public SimpleFluidHandler getHandler (Direction direction)
    {
        if (direction == Direction.UP || direction == Direction.DOWN || direction == null)
            return handler;
        return null;
    }
}
