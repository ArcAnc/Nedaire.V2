/**
 * @author ArcAnc
 * Created at: 01.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.block.block_entity;

import com.arcanc.nedaire.content.block.BlockInterfaces;
import com.arcanc.nedaire.content.block.block_entity.ticking.NServerTickableBE;
import com.arcanc.nedaire.registration.NRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FluidTransmitterBlockEntity extends NBaseBlockEntity implements BlockInterfaces.INWrencheable, NServerTickableBE
{

    private final List<BlockPos> attachedPoses = new ArrayList<>();

    /*FIXME: дописать передачу жижи из одной бочки в другую. Сделать отвертку/молоток/да что угодно, что будет настраивать этот передатчик. Так же нужно допилить его рендер*/
    public FluidTransmitterBlockEntity(BlockPos pPos, BlockState pBlockState)
    {
        super(NRegistration.NBlockEntities.BE_FLUID_TRANSMITTER.get(), pPos, pBlockState);
    }

    @Override
    public void tickServer()
    {

    }

    @Override
    public InteractionResult onUsed(@NotNull ItemStack stack, UseOnContext ctx)
    {
        BlockPos pos = BlockPos.ZERO;

        if (stack.has(NRegistration.NDataComponents.POSITION))
        {
            pos = stack.getOrDefault(NRegistration.NDataComponents.POSITION, BlockPos.ZERO);
            stack.remove(NRegistration.NDataComponents.POSITION);
        }

        if (pos != BlockPos.ZERO)
        {
            Level level = ctx.getLevel();
            IFluidHandler cap = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, ctx.getClickedFace().getOpposite());
            if (cap != null)
            {
                attachedPoses.add(pos);
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void readCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {

    }

    @Override
    public void writeCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacker)
    {

    }

    public enum ChooseTargetMethod
    {
        ROUND_ROBIN, RANDOM;
    }
}
