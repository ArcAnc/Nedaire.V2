/**
 * @author ArcAnc
 * Created at: 13.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.capabilities;

import com.arcanc.nedaire.content.capabilities.crystal_power.ICrystalPower;
import com.arcanc.nedaire.util.NDatabase;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class NCapabilities
{
    public static final class CrystalPower
    {
        public static final BlockCapability<ICrystalPower, @Nullable Direction> BLOCK = BlockCapability.createSided(NDatabase.modRL(NDatabase.CapabilitiesInfo.CrystalPowerInfo.CAPABILITY_NAME), ICrystalPower.class);
        public static final EntityCapability<ICrystalPower, @Nullable Direction> ENTITY = EntityCapability.createSided(NDatabase.modRL(NDatabase.CapabilitiesInfo.CrystalPowerInfo.CAPABILITY_NAME), ICrystalPower.class);
        public static final ItemCapability<ICrystalPower, Void> ITEM = ItemCapability.createVoid(NDatabase.modRL(NDatabase.CapabilitiesInfo.CrystalPowerInfo.CAPABILITY_NAME), ICrystalPower.class);

        private CrystalPower() {}
    }

    public static void registerCapabilities(final RegisterCapabilitiesEvent event)
    {
        event.registerBlock(CrystalPower.BLOCK,
                (level, pos, state, blockEntity, context) -> level.getCapability(
                        CrystalPower.BLOCK,
                        pos,
                        state,
                        blockEntity,
                        context),
                /*FIXME: attach to normal blocks*/
                Blocks.ACACIA_LOG);
    }
}
