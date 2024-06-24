/**
 * @author ArcAnc
 * Created at: 24.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.block.block_entity;

import com.arcanc.nedaire.registration.NRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class NodeBlockEntity extends NBaseBlockEntity
{
    public NodeBlockEntity(BlockPos pPos, BlockState pBlockState)
    {
        super(NRegistration.NBlockEntities.BE_NODE.get(), pPos, pBlockState);
    }

    @Override
    public void readCustomTag(@NotNull CompoundTag tag, boolean descrPacket) {

    }

    @Override
    public void writeCustomTag(@NotNull CompoundTag pTag, boolean descrPacker) {

    }
}
