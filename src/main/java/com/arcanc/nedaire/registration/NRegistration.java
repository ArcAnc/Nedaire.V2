/**
 * @author ArcAnc
 * Created at: 10.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Ancient's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.registration;

import com.arcanc.nedaire.util.NDatabase;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NRegistration
{
    public static class NBlocks
    {
        public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(NDatabase.MOD_ID);
    }

    public static class NItems
    {
        public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NDatabase.MOD_ID);
    }

    public static final class NCreativeTabs
    {
        public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NDatabase.MOD_ID);
    }
}
