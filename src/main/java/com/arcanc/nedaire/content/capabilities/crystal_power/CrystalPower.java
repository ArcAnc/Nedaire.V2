/**
 * @author ArcAnc
 * Created at: 13.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.capabilities.crystal_power;

import com.arcanc.nedaire.util.NDatabase;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class CrystalPower implements ICrystalPower, INBTSerializable<CompoundTag>
{
    private float power;
    private float powerMax;

    private float maxReceive;
    private float maxExtract;

    public CrystalPower(int capacity) {
        this(capacity, capacity, capacity, 0);
    }

    public CrystalPower(int capacity, int maxTransfer) {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public CrystalPower(int capacity, int maxReceive, int maxExtract) {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public CrystalPower(int capacity, int maxReceive, int maxExtract, int energy) {
        this.powerMax = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.power = Math.max(0, Math.min(capacity, energy));
    }

    @Override
    public float getCP() {
        return power;
    }

    @Override
    public float getCPMax() {
        return powerMax;
    }

    @Override
    public void setCP(float amount)
    {
        this.power = Math.max(0, Math.min(amount, powerMax));
    }

    @Override
    public float addCP(float add, boolean simulate)
    {
        if (!canInsert() || add <= 0)
            return 0;

        float energyReceived = Mth.clamp(this.powerMax - this.power, 0, Math.min(this.maxReceive, add));
        if (!simulate)
            this.power += energyReceived;
        return energyReceived;
    }

    @Override
    public float extractCP(float extract, boolean simulate)
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
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider)
    {
        CompoundTag tag = new CompoundTag();

        tag.putFloat(NDatabase.CapabilitiesInfo.CrystalPowerInfo.POWER, this.power);
        tag.putFloat(NDatabase.CapabilitiesInfo.CrystalPowerInfo.MAX_POWER, this.powerMax);
        tag.putFloat(NDatabase.CapabilitiesInfo.CrystalPowerInfo.MAX_EXTRACT, this.maxExtract);
        tag.putFloat(NDatabase.CapabilitiesInfo.CrystalPowerInfo.MAX_RECEIVE, this.maxReceive);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt)
    {
        this.power = nbt.getFloat(NDatabase.CapabilitiesInfo.CrystalPowerInfo.POWER);
        this.powerMax = nbt.getFloat(NDatabase.CapabilitiesInfo.CrystalPowerInfo.MAX_POWER);
        this.maxExtract = nbt.getFloat(NDatabase.CapabilitiesInfo.CrystalPowerInfo.MAX_EXTRACT);
        this.maxReceive = nbt.getFloat(NDatabase.CapabilitiesInfo.CrystalPowerInfo.MAX_RECEIVE);
    }
}
