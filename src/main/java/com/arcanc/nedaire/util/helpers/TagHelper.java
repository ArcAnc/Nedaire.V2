/**
 * @author ArcAnc
 * Created at: 30.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.util.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class TagHelper
{
    public static @NotNull Vec3 readVec3(@NotNull CompoundTag compound)
    {
        return new Vec3(compound.getDouble("x"), compound.getDouble("y"), compound.getDouble("z"));
    }

    public static @NotNull Vec3 readVec3(@NotNull CompoundTag compound, @NotNull String address)
    {
        Vec3 vec = Vec3.ZERO;
        if (compound.contains(address))
        {
            CompoundTag tag = compound.getCompound(address);
            vec = readVec3(tag);
        }
        return vec;
    }

    public static @NotNull CompoundTag writeVec3(@NotNull Vec3 vec)
    {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("x", vec.x());
        tag.putDouble("y", vec.y());
        tag.putDouble("z", vec.z());

        return tag;
    }

    public static @NotNull CompoundTag writeVec3(@NotNull Vec3 vec, @NotNull CompoundTag dest, @NotNull String address)
    {
        dest.put(address, writeVec3(vec));
        return dest;
    }

    public static @NotNull BlockPos readBlockPos(@NotNull CompoundTag compound, @NotNull String address)
    {
        BlockPos pos = new BlockPos(0,0,0);

        if (compound.contains(address))
        {
            CompoundTag tag = compound.getCompound(address);
            if (!tag.isEmpty())
            {
                pos = new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
            }
        }

        return pos;
    }

    public static @NotNull CompoundTag writeBlockPos (@NotNull BlockPos pos)
    {
        CompoundTag tag = new CompoundTag();

        tag.putInt("x", pos.getX());
        tag.putInt("y", pos.getY());
        tag.putInt("z", pos.getZ());

        return tag;
    }

    public static @NotNull CompoundTag writeBlockPos(@NotNull BlockPos pos, @NotNull CompoundTag dest, @NotNull String address)
    {
        dest.put(address, writeBlockPos(pos));
        return dest;
    }
}
