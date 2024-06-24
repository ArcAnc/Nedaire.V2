/**
 * @author ArcAnc
 * Created at: 10.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Ancient's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire;


import com.arcanc.nedaire.content.block.ber.NodeRenderer;
import com.arcanc.nedaire.content.capabilities.NCapabilities;
import com.arcanc.nedaire.content.gui.container_menu.NContainerMenu;
import com.arcanc.nedaire.content.gui.nerwork.messages.NetworkEngine;
import com.arcanc.nedaire.data.NBlockStateProvider;
import com.arcanc.nedaire.data.NItemModelProvider;
import com.arcanc.nedaire.data.NSpriteSourceProvider;
import com.arcanc.nedaire.registration.NRegistration;
import com.arcanc.nedaire.util.NDatabase;
import com.mojang.logging.LogUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;

@Mod(NDatabase.MOD_ID)
public class Nedaire
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public Nedaire(@NotNull IEventBus modEventBus, ModContainer modContainer)
    {
        modEventBus.addListener(this :: commonSetup);

        NRegistration.init(modEventBus);

        modEventBus.addListener(NCapabilities :: registerCapabilities);

        setupEvents(modEventBus);
    }

    private void setupEvents(final IEventBus modEventBus)
    {
        registerNetwork(modEventBus);
        registerContainerMenuEvents();

        if (FMLLoader.getDist().isClient())
        {
            modEventBus.addListener(this :: registerBlockEntityRenderers);
        }

        modEventBus.addListener(this :: gatherData);
    }

    private void registerNetwork(final @NotNull IEventBus modEventBus)
    {
        LOGGER.warn("Register BE Renderers");
        modEventBus.addListener(NetworkEngine :: setupMessages);
    }

    private void registerContainerMenuEvents()
    {
        NeoForge.EVENT_BUS.addListener(NContainerMenu :: onContainerOpened);
        NeoForge.EVENT_BUS.addListener(NContainerMenu :: onContainerClosed);
    }

    private void registerBlockEntityRenderers(final EntityRenderersEvent.@NotNull RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(NRegistration.NBlockEntities.BE_NODE.get(), NodeRenderer::new);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        LOGGER.info("{} Started Server Initialization", NDatabase.MOD_ID);

        LOGGER.info("{} Finished Server Initialization", NDatabase.MOD_ID);
    }

    public void gatherData(final @NotNull GatherDataEvent event)
    {
        ExistingFileHelper ext = event.getExistingFileHelper();
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();


        //gen.addProvider(event.includeClient(), new NEnUsLangProvider(packOutput));
        gen.addProvider(event.includeClient(), new NItemModelProvider(packOutput, ext));
        gen.addProvider(event.includeClient(), new NBlockStateProvider(packOutput, ext));
        gen.addProvider(event.includeClient(), new NSpriteSourceProvider(packOutput, lookupProvider, ext));
    }

    public static Logger getLogger ()
    {
        return LOGGER;
    }
}
