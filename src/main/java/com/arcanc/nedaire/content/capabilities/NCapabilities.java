/**
 * @author ArcAnc
 * Created at: 13.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.capabilities;

import com.arcanc.nedaire.util.NDatabase;
import com.arcanc.nedaire.util.inventory.items.ItemCapabilityAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class NCapabilities
{
    /*public static final class Energon
    {
        public static final BlockCapability<IEnergon, @Nullable Direction> BLOCK = BlockCapability.createSided(NDatabase.modRL(NDatabase.CapabilitiesInfo.EnergonInfo.CAPABILITY_NAME), IEnergon.class);
        public static final EntityCapability<IEnergon, @Nullable Direction> ENTITY = EntityCapability.createSided(NDatabase.modRL(NDatabase.CapabilitiesInfo.EnergonInfo.CAPABILITY_NAME), IEnergon.class);
        public static final ItemCapability<IEnergon, Void> ITEM = ItemCapability.createVoid(NDatabase.modRL(NDatabase.CapabilitiesInfo.EnergonInfo.CAPABILITY_NAME), IEnergon.class);

        private Energon() {}
    }
    */
    public static final class ItemHandler
    {
        public static final BlockCapability<IItemHandler, @NotNull ItemCapabilityAccess> BLOCK = BlockCapability.create(NDatabase.modRL(NDatabase.CapabilitiesInfo.InventoryInfo.CAPABILITY_NAME), IItemHandler.class, ItemCapabilityAccess.class);
    }

    public static void registerCapabilities(final @NotNull RegisterCapabilitiesEvent event)
    {

        /*event.registerBlockEntity(Energon.BLOCK,
                /*FIXME: replace with normal BE type
                BlockEntityType.SKULL,
                (blockEntity, context) -> blockEntity instanceof NBaseBlockEntity be ? be.getCapability(context) : null);*/

        /*event.registerBlock(ItemHandler.BLOCK,
                (level, pos, state, blockEntity, context) -> level.getCapability(ItemHandler.BLOCK,
                        pos,
                        state,
                        blockEntity,
                        context),
                /*FIXME: attach to normal blocks
                Blocks.ACACIA_LOG);
        event.registerBlock(Capabilities.ItemHandler.BLOCK,
                (level, pos, state, blockEntity, context) -> level.getCapability(
                        Capabilities.ItemHandler.BLOCK,
                        pos,
                        state,
                        blockEntity,
                        context),
                /*FIXME: attach to normal blocks
                Blocks.ACACIA_LOG);*/
    }
}
