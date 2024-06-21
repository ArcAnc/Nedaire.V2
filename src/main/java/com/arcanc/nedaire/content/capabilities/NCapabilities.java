/**
 * @author ArcAnc
 * Created at: 13.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.capabilities;

import com.arcanc.nedaire.content.capabilities.energon.IEnergon;
import com.arcanc.nedaire.util.NDatabase;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NCapabilities
{
    public static final class Energon
    {
        public static final BlockCapability<IEnergon, @Nullable Direction> BLOCK = BlockCapability.createSided(NDatabase.modRL(NDatabase.CapabilitiesInfo.EnergonInfo.CAPABILITY_NAME), IEnergon.class);
        public static final EntityCapability<IEnergon, @Nullable Direction> ENTITY = EntityCapability.createSided(NDatabase.modRL(NDatabase.CapabilitiesInfo.EnergonInfo.CAPABILITY_NAME), IEnergon.class);
        public static final ItemCapability<IEnergon, Void> ITEM = ItemCapability.createVoid(NDatabase.modRL(NDatabase.CapabilitiesInfo.EnergonInfo.CAPABILITY_NAME), IEnergon.class);

        private Energon() {}
    }

    public static void registerCapabilities(final @NotNull RegisterCapabilitiesEvent event)
    {
        event.registerBlock(Energon.BLOCK,
                (level, pos, state, blockEntity, context) -> level.getCapability(
                        Energon.BLOCK,
                        pos,
                        state,
                        blockEntity,
                        context),
                /*FIXME: attach to normal blocks*/
                Blocks.ACACIA_LOG);
    }
}
