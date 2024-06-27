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
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.List;

public class SimpleFluidHandler implements IFluidHandler, INBTSerializable<CompoundTag>
{
    protected List<FluidStackHolder> tanks;
    @Nullable
    protected IFluidCallback callback;

    public SimpleFluidHandler()
    {
        this(new ArrayList<>(), null);
    }
    public SimpleFluidHandler(@Nullable IFluidCallback callback)
    {
        this(new ArrayList<>(), callback);
    }
    public SimpleFluidHandler(@NotNull List<FluidStackHolder> holderList)
    {
        this(holderList, null);
    }
    public SimpleFluidHandler(@NotNull List<FluidStackHolder> holderList, @Nullable IFluidCallback callback)
    {
        this.tanks = new ArrayList<>(holderList);
        this.callback = callback;
    }

    public FluidStackHolder.ExtBuilder<SimpleFluidHandler> addSlot()
    {
        return FluidStackHolder.newBuilder(this);
    }

    public void finishSlot(@NotNull FluidStackHolder slot)
    {
        this.tanks.add(slot);
    }
    @Override
    public int getTanks()
    {
        return tanks.size();
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int slot)
    {
        validateSlotIndex(slot);
        return this.tanks.get(slot).getFluid();
    }

    @Override
    public int fill(@NotNull FluidStack stack, @NotNull FluidAction simulate)
    {
        int ret;
        for (FluidStackHolder tank : tanks)
        {
            ret = tank.fill(stack, simulate);
            if (ret > 0)
                return ret;
        }
        return 0;
    }


    @Override
    public @NotNull FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action)
    {
        FluidStack ret;
        for (FluidStackHolder tank : tanks)
        {
            ret = tank.drain(resource, action);
            if (!ret.isEmpty())
                return ret;
        }
        return FluidStack.EMPTY;
    }

    @Override
    public @NotNull FluidStack drain(int amount, @NotNull FluidAction simulate)
    {
        FluidStack ret;
        for (FluidStackHolder tank : tanks)
        {
            ret = tank.drain(amount, simulate);
            if (!ret.isEmpty())
                return ret;
        }
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int slot)
    {
        validateSlotIndex(slot);
        return this.tanks.get(slot).getCapacity();
    }

    @Override
    public boolean isFluidValid(int slot, @NotNull FluidStack stack)
    {
        validateSlotIndex(slot);
        return this.tanks.get(slot).isFluidValid(stack);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider)
    {
        ListTag list = new ListTag();
        for (int q = 0; q < getTanks(); q++)
        {
            FluidStackHolder holder = this.tanks.get(q);

            if (!holder.isEmpty())
            {
                CompoundTag itemTag = holder.serializeNBT(provider);
                itemTag.putInt(NDatabase.CapabilitiesInfo.FluidInfo.SLOT, q);
                list.add(q, itemTag);
            }
        }
        CompoundTag fullTag = new CompoundTag();
        fullTag.put(NDatabase.CapabilitiesInfo.FluidInfo.FLUIDS, list);
        return fullTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt)
    {
        ListTag list = nbt.getList(NDatabase.CapabilitiesInfo.FluidInfo.FLUIDS, Tag.TAG_COMPOUND);
        for (int q = 0; q < list.size(); q++)
        {
            CompoundTag itemTag = list.getCompound(q);
            int slot = itemTag.getInt(NDatabase.CapabilitiesInfo.FluidInfo.SLOT);

            if (slot >= 0 && slot < this.tanks.size())
            {
                this.tanks.get(slot).deserializeNBT(provider, itemTag);
                onInventoryChange(slot);
            }
        }
    }

    public void onInventoryChange(int slot)
    {
        if (callback == null)
            return;
        validateSlotIndex(slot);
        callback.onFluidChanged(slot);
    }

    public void clear(int slot)
    {
        if (callback == null)
            return;
        validateSlotIndex(slot);
        callback.clearTank(slot);
        callback.onFluidChanged(slot);
    }

    public void clear()
    {
        this.tanks.clear();
    }

    public boolean isEmpty ()
    {
        if(!tanks.isEmpty())
            for (FluidStackHolder tank : this.tanks)
                if (!tank.isEmpty())
                    return false;
        return true;
    }

    public IFluidHandler getHandler(TankType type)
    {
        return this;
    }

    protected void validateSlotIndex(int slot)
    {
        if (slot < 0 || slot >= tanks.size())
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + tanks.size() + ")");
    }
}
