/**
 * @author ArcAnc
 * Created at: 11.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.block.block_entity;

import com.arcanc.nedaire.util.NDatabase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class RedstoneSensitiveBlockEntity extends NBaseBlockEntity
{
    private int currentRedstoneMod = 2;

    public RedstoneSensitiveBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState)
    {
        super(pType, pPos, pBlockState);
    }

    public boolean isPowered()
    {
        if (getLevel() == null)
            return false;
        if (currentRedstoneMod == 0 && getLevel().hasNeighborSignal(getBlockPos()))
            return true;
        else if (currentRedstoneMod == 1 && !getLevel().hasNeighborSignal(getBlockPos()))
            return true;
        else return currentRedstoneMod == 2;
    }

    /**
     * 0 - required redstone
     * 1 - required disabled redstone
     * 2 - ignore all redstone
     */
    public void setCurrentRedstoneMod(int currentRedstoneMod)
    {
        this.currentRedstoneMod = currentRedstoneMod;
    }

    /**
     * 0 - required redstone
     * 1 - required disabled redstone
     * 2 - ignore all redstone
     */
    public int getCurrentRedstoneMod()
    {
        return currentRedstoneMod;
    }

    @Override
    public void readCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        if (tag.contains(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.RedstoneSensitive.REDSTONE_MOD))
        {
            this.currentRedstoneMod = tag.getInt(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.RedstoneSensitive.REDSTONE_MOD);
        }
    }

    @Override
    public void writeCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        tag.putInt(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.RedstoneSensitive.REDSTONE_MOD, this.currentRedstoneMod);
    }
}
