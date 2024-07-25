/**
 * @author ArcAnc
 * Created at: 09.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.event;

import com.arcanc.nedaire.content.block.ber.FluidStorageRenderer;
import com.arcanc.nedaire.content.block.ber.FluidTransmitterRenderer;
import com.arcanc.nedaire.content.block.ber.NodeRenderer;
import com.arcanc.nedaire.content.block.model_loaders.FluidStorageBakedModel;
import com.arcanc.nedaire.content.fluid.NEnergonFluidType;
import com.arcanc.nedaire.content.fluid.NFluidType;
import com.arcanc.nedaire.content.items.NBucketItem;
import com.arcanc.nedaire.registration.NRegistration;
import com.arcanc.nedaire.util.handlers.FluidTransportHandler;
import com.arcanc.nedaire.util.model.SimpleModel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public class ClientEvents
{
    public static void registerClientEvents(final @NotNull IEventBus modEventBus)
    {
        modEventBus.addListener(ClientEvents :: registerBlockEntityRenderers);
        modEventBus.addListener(ClientEvents :: setupClient);
        modEventBus.addListener(ClientEvents :: setupItemColor);
        modEventBus.addListener(ClientEvents :: setupModels);
        modEventBus.addListener(ClientEvents ::registerFluidTypesExtensions);

        NeoForge.EVENT_BUS.addListener(FluidTransportHandler :: essenceRenderer);
    }

    private static void registerFluidTypesExtensions(final RegisterClientExtensionsEvent event)
    {
        NRegistration.NFluids.FLUID_TYPES.getEntries().
                     stream().
                     map(DeferredHolder::get).
                     filter(fluidType -> fluidType instanceof NFluidType).
                     map(fluidType -> (NFluidType)fluidType).
                     forEach(fluidType ->
                             event.registerFluidType(fluidType.registerClientExtensions(), fluidType));
    }

    private static void registerBlockEntityRenderers(final EntityRenderersEvent.@NotNull RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(NRegistration.NBlockEntities.BE_NODE.get(), NodeRenderer:: new);
        event.registerBlockEntityRenderer(NRegistration.NBlockEntities.BE_FLUID_STORAGE.get(), FluidStorageRenderer:: new);
        event.registerBlockEntityRenderer(NRegistration.NBlockEntities.BE_FLUID_TRANSMITTER.get(), FluidTransmitterRenderer :: new);
    }

    private static void setupClient(final @NotNull FMLClientSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            NRegistration.NFluids.FLUIDS.getEntries().stream().filter(fluid -> fluid.get().getFluidType() instanceof NEnergonFluidType).
                    map(DeferredHolder:: get).
                    forEach(fluid ->
                    {
                        ItemBlockRenderTypes.setRenderLayer(fluid, RenderType.translucent());
                        ItemBlockRenderTypes.setRenderLayer(fluid, RenderType.translucent());
                    });
        });
    }

    private static void setupItemColor(final @NotNull RegisterColorHandlersEvent.Item event)
    {
        event.register((stack, tintIndex) ->
                {
                    if (stack.getItem() instanceof NBucketItem item)
                    {
                        if (item.content.getFluidType() instanceof NEnergonFluidType type && tintIndex == 1)
                            return type.getEnergonType().color();
                    }
                    return -1;
                },
                NRegistration.NFluids.ENERGON_DARK.bucket().get(),
                NRegistration.NFluids.ENERGON_BLUE.bucket().get(),
                NRegistration.NFluids.ENERGON_RED.bucket().get(),
                NRegistration.NFluids.ENERGON_GREEN.bucket().get(),
                NRegistration.NFluids.ENERGON_YELLOW.bucket().get());
    }

    private static void setupModels (final ModelEvent.@NotNull RegisterGeometryLoaders event)
    {
        event.register(SimpleModel.GeometryLoader.ID, new SimpleModel.GeometryLoader(FluidStorageBakedModel::new));
    }
}
