/**
 * @author ArcAnc
 * Created at: 13.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.capabilities.energon;

import com.arcanc.nedaire.util.NDatabase;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

public class Energon implements IEnergon, INBTSerializable<CompoundTag>
{
    private float power;
    private float powerMax;

    private float maxReceive;
    private float maxExtract;

    public Energon(int capacity) {
        this(capacity, capacity, capacity, 0);
    }

    public Energon(int capacity, int maxTransfer) {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public Energon(int capacity, int maxReceive, int maxExtract) {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public Energon(int capacity, int maxReceive, int maxExtract, int energy) {
        this.powerMax = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.power = Math.max(0, Math.min(capacity, energy));
    }

    @Override
    public float getEnergy() {
        return power;
    }

    @Override
    public float getEnergyMax() {
        return powerMax;
    }

    @Override
    public void setEnergy(float amount)
    {
        this.power = Math.max(0, Math.min(amount, powerMax));
    }

    @Override
    public float addEnergy(float add, boolean simulate)
    {
        if (!canInsert() || add <= 0)
            return 0;

        float energyReceived = Mth.clamp(this.powerMax - this.power, 0, Math.min(this.maxReceive, add));
        if (!simulate)
            this.power += energyReceived;
        return energyReceived;
    }

    @Override
    public float extractEnergy(float extract, boolean simulate)
    {
        if (!canExtract() || extract <= 0)
            return 0;

        float energyExtracted = Math.min(this.power, Math.min(this.maxExtract, extract));
        if (!simulate)
            this.power -= energyExtracted;
        return energyExtracted;
    }

    @Override
    public boolean canExtract() {
        return this.maxExtract > 0;
    }

    @Override
    public boolean canInsert() {
        return this.maxReceive > 0;
    }

    @Override
    public @NotNull CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider)
    {
        CompoundTag tag = new CompoundTag();

        tag.putFloat(NDatabase.CapabilitiesInfo.EnergonInfo.POWER, this.power);
        tag.putFloat(NDatabase.CapabilitiesInfo.EnergonInfo.MAX_POWER, this.powerMax);
        tag.putFloat(NDatabase.CapabilitiesInfo.EnergonInfo.MAX_EXTRACT, this.maxExtract);
        tag.putFloat(NDatabase.CapabilitiesInfo.EnergonInfo.MAX_RECEIVE, this.maxReceive);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt)
    {
        this.power = nbt.getFloat(NDatabase.CapabilitiesInfo.EnergonInfo.POWER);
        this.powerMax = nbt.getFloat(NDatabase.CapabilitiesInfo.EnergonInfo.MAX_POWER);
        this.maxExtract = nbt.getFloat(NDatabase.CapabilitiesInfo.EnergonInfo.MAX_EXTRACT);
        this.maxReceive = nbt.getFloat(NDatabase.CapabilitiesInfo.EnergonInfo.MAX_RECEIVE);
    }
}
