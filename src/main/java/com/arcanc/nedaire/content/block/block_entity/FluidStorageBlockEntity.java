/**
 * @author ArcAnc
 * Created at: 28.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.block.block_entity;

import com.arcanc.nedaire.Nedaire;
import com.arcanc.nedaire.content.block.BlockInterfaces;
import com.arcanc.nedaire.registration.NRegistration;
import com.arcanc.nedaire.util.NDatabase;
import com.arcanc.nedaire.util.Upgrade;
import com.arcanc.nedaire.util.helpers.BlockHelper;
import com.arcanc.nedaire.util.inventory.fluids.SimpleFluidHandler;
import com.arcanc.nedaire.util.inventory.fluids.TankType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class FluidStorageBlockEntity extends NBaseBlockEntity implements BlockInterfaces.IUpgradeable
{
    private final int HANDLER_BASE_CAPACITY = 5000;
    protected SimpleFluidHandler handler;
    protected Upgrade upg = Upgrade.SPARK;

    public FluidStorageBlockEntity(BlockPos pos, BlockState state)
    {
        super(NRegistration.NBlockEntities.BE_FLUID_STORAGE.get(), pos, state);

        this.handler = new SimpleFluidHandler().
                addSlot().
                setCallback(holder -> setChanged()).
                setCapacity(HANDLER_BASE_CAPACITY).
                setTankType(TankType.ALL).
                finishSlot();
    }

    @Override
    public void readCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        handler.deserializeNBT(registries, tag.getCompound(NDatabase.CapabilitiesInfo.FluidInfo.CAPABILITY_NAME));
        this.upg = Upgrade.values()[tag.getInt(NDatabase.ItemsInfo.Names.UPGRADE)];
    }

    @Override
    public void writeCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacker)
    {
        tag.put(NDatabase.CapabilitiesInfo.FluidInfo.CAPABILITY_NAME, handler.serializeNBT(registries));
        tag.putInt(NDatabase.ItemsInfo.Names.UPGRADE, upg.getLvl());
    }

    public SimpleFluidHandler getHandler (Direction direction)
    {
        if (direction == Direction.UP || direction == Direction.DOWN || direction == null)
            return handler;
        return null;
    }

    @Override
    public Upgrade getUpgrade()
    {
        return this.upg;
    }

    @Override
    public boolean applyUpgrade(Upgrade upg)
    {
        if (getUpgrade().isLowerThan(upg))
        {
            Nedaire.getLogger().warn("Upgrading!");
            this.upg = upg;
            this.handler.setTanksCapacity(HANDLER_BASE_CAPACITY * upg.getModifier());
            level.setBlock(getBlockPos(), getBlockState().setValue(BlockHelper.BlockProperties.UPGRADE_LEVEL, upg.getLvl()), Block.UPDATE_ALL);
            this.markDirty();
            return true;
        }
        return false;
    }
}
