/**
 * @author ArcAnc
 * Created at: 10.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Ancient's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire;


import com.arcanc.nedaire.content.event.ClientEvents;
import com.arcanc.nedaire.content.event.CommonEvents;
import com.arcanc.nedaire.registration.NRegistration;
import com.arcanc.nedaire.util.NDatabase;
import com.arcanc.nedaire.util.handlers.FluidTransportHandler;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod(NDatabase.MOD_ID)
public class Nedaire
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public Nedaire(@NotNull IEventBus modEventBus, ModContainer modContainer)
    {
        NRegistration.init(modEventBus);

        setupEvents(modEventBus);
    }

    private void setupEvents(final @NotNull IEventBus modEventBus)
    {
        CommonEvents.registerCommonEvents(modEventBus);
        if (FMLLoader.getDist().isClient())
        {
            ClientEvents.registerClientEvents(modEventBus);
        }

    }

    public static Logger getLogger ()
    {
        return LOGGER;
    }
}
