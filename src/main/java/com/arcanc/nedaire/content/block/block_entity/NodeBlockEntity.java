/**
 * @author ArcAnc
 * Created at: 24.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.block.block_entity;

import com.arcanc.nedaire.Nedaire;
import com.arcanc.nedaire.registration.NRegistration;
import com.arcanc.nedaire.util.NDatabase;
import com.arcanc.nedaire.util.inventory.fluids.SimpleFluidHandler;
import com.arcanc.nedaire.util.inventory.fluids.TankType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class NodeBlockEntity extends NBaseBlockEntity
{

    private static final float CHANCE_FOR_RED = 0.5f;

    private static final float CHANCE_FOR_GREEN = 0.05f;
    private final SimpleFluidHandler handler = new SimpleFluidHandler();
    public NodeBlockEntity(BlockPos pPos, BlockState pBlockState)
    {
        super(NRegistration.NBlockEntities.BE_NODE.get(), pPos, pBlockState);
    }

    @Override
    public void onLoad()
    {
        generateRandomAmountOfEnergy();
    }

    private void generateRandomAmountOfEnergy()
    {
        if (getLevel().isClientSide())
            return;
        Nedaire.getLogger().warn("On load starts");
        Nedaire.getLogger().warn("Handler slots amount: {}", handler.getTanks());
        if (handler.getTanks() == 0)
        {
            RandomSource random = getLevel().random;
            int amount = random.nextIntBetweenInclusive(150, 950);
            handler.addSlot().
                    setStack(new FluidStack(NRegistration.NFluids.ENERGON_BLUE.still().get(), amount)).
                    setCapacity(amount).
                    setTankType(TankType.OUTPUT).
                    setValidator(stack -> false).
                    finishSlot();
            if (random.nextFloat() < CHANCE_FOR_RED)
            {
                amount = random.nextIntBetweenInclusive(150, 350);
                handler.addSlot().
                        setStack(new FluidStack(NRegistration.NFluids.ENERGON_RED.still().get(), amount)).
                        setCapacity(amount).setTankType(TankType.OUTPUT).
                        setValidator(stack -> false).
                        finishSlot();
            }
            if (random.nextFloat() < CHANCE_FOR_GREEN)
            {
                amount = random.nextIntBetweenInclusive(50, 200);
                handler.addSlot().
                        setStack(new FluidStack(NRegistration.NFluids.ENERGON_GREEN.still().get(), amount)).
                        setCapacity(amount).setTankType(TankType.OUTPUT).
                        setValidator(stack -> false).
                        finishSlot();
            }
            markDirty();
        }
        Nedaire.getLogger().warn("Handler slots amount: {}", handler.getTanks());
        for (int q = 0; q < handler.getTanks(); q++)
        {
            Nedaire.getLogger().warn("At slot {} contaning liquid: {}", q, handler.getFluidInTank(q).getFluid().getFluidType().getDescription());
        }
    }

    @Override
    public void readCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        CompoundTag handlerTag = tag.getCompound(NDatabase.CapabilitiesInfo.FluidInfo.CAPABILITY_NAME);
        int slot = handlerTag.getList(NDatabase.CapabilitiesInfo.FluidInfo.FLUIDS, Tag.TAG_COMPOUND).size();

        handler.clear();

        for (int q = 0; q < slot; q++)
        {
            handler.addSlot().
                    setTankType(TankType.OUTPUT).
                    setValidator(stack -> false).
                    finishSlot();
        }

        handler.deserializeNBT(registries, handlerTag);
    }

    @Override
    public void writeCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacker)
    {
        tag.put(NDatabase.CapabilitiesInfo.FluidInfo.CAPABILITY_NAME, handler.serializeNBT(registries));
    }

    public SimpleFluidHandler getHandler (Direction direction)
    {
        return handler;
    }
}
