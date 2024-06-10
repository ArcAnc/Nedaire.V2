/**
 * @author ArcAnc
 * Created at: 10.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Ancient's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire;


import com.arcanc.nedaire.registration.NRegistration;
import com.arcanc.nedaire.util.NDatabase;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(NDatabase.MOD_ID)
public class Nedaire
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public Nedaire(IEventBus modEventBus, ModContainer modContainer)
    {
        modEventBus.addListener(this::commonSetup);

        NRegistration.NBlocks.BLOCKS.register(modEventBus);
        NRegistration.NItems.ITEMS.register(modEventBus);
        NRegistration.NCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        LOGGER.info("{} Started Initialization", NDatabase.MOD_ID);

        LOGGER.info("{} Finished Initialization", NDatabase.MOD_ID);
    }

    public static Logger getLogger ()
    {
        return LOGGER;
    }
}
