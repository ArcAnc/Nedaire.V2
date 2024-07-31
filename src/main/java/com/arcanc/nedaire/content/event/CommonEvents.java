/**
 * @author ArcAnc
 * Created at: 09.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.event;

import com.arcanc.nedaire.Nedaire;
import com.arcanc.nedaire.content.block.block_entity.FluidStorageBlockEntity;
import com.arcanc.nedaire.content.block.block_entity.NodeBlockEntity;
import com.arcanc.nedaire.content.gui.container_menu.NContainerMenu;
import com.arcanc.nedaire.content.items.NBucketItem;
import com.arcanc.nedaire.content.nerwork.messages.NetworkEngine;
import com.arcanc.nedaire.data.NBlockStateProvider;
import com.arcanc.nedaire.data.NItemModelProvider;
import com.arcanc.nedaire.data.NSpriteSourceProvider;
import com.arcanc.nedaire.data.language.NEnUsLangProvider;
import com.arcanc.nedaire.registration.NRegistration;
import com.arcanc.nedaire.util.NDatabase;
import com.arcanc.nedaire.util.handlers.FluidTransportHandler;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class CommonEvents
{
    public static void registerCommonEvents(@NotNull final IEventBus modEventBus)
    {
        modEventBus.addListener(NetworkEngine :: setupMessages);
        registerContainerMenuEvents();
        modEventBus.addListener(CommonEvents :: commonSetup);

        modEventBus.addListener(CommonEvents :: registerCapabilitiesEvent);

        modEventBus.addListener(CommonEvents :: gatherData);

        //fluidTransport
        NeoForge.EVENT_BUS.addListener(FluidTransportHandler :: levelTickEvent);
        NeoForge.EVENT_BUS.addListener(FluidTransportHandler :: loadLevel);
        NeoForge.EVENT_BUS.addListener(FluidTransportHandler :: unloadLevel);
        NeoForge.EVENT_BUS.addListener(FluidTransportHandler :: saveLevel);
        NeoForge.EVENT_BUS.addListener(FluidTransportHandler :: playerLoad);
    }

    private static void registerContainerMenuEvents()
    {
        NeoForge.EVENT_BUS.addListener(NContainerMenu:: onContainerOpened);
        NeoForge.EVENT_BUS.addListener(NContainerMenu :: onContainerClosed);
    }

    private static void commonSetup(final FMLCommonSetupEvent event)
    {
        Nedaire.getLogger().info("{} Started Server Initialization", NDatabase.MOD_ID);

        Nedaire.getLogger().info("{} Finished Server Initialization", NDatabase.MOD_ID);
    }

    private static void registerCapabilitiesEvent(final @NotNull RegisterCapabilitiesEvent event)
    {
        NRegistration.NItems.ITEMS.getEntries().stream().filter(item -> item.get() instanceof NBucketItem).
                map(DeferredHolder:: get).forEach(item -> event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), item));

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, NRegistration.NBlockEntities.BE_NODE.get(), NodeBlockEntity::getHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, NRegistration.NBlockEntities.BE_FLUID_STORAGE.get(), FluidStorageBlockEntity::getHandler);
    }

    private static void gatherData(final @NotNull GatherDataEvent event)
    {
        ExistingFileHelper ext = event.getExistingFileHelper();
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();


        gen.addProvider(event.includeClient(), new NEnUsLangProvider(packOutput));
        gen.addProvider(event.includeClient(), new NItemModelProvider(packOutput, ext));
        gen.addProvider(event.includeClient(), new NBlockStateProvider(packOutput, ext));
        gen.addProvider(event.includeClient(), new NSpriteSourceProvider(packOutput, lookupProvider, ext));
    }
}
