/**
 * @author ArcAnc
 * Created at: 10.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Ancient's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.util;

import net.minecraft.resources.ResourceLocation;

public class NDatabase
{
    public static final String MOD_ID = "nedaire";
    public static final String MOD_VERSION = "0.0.0";

    public static final class MagicInfo
    {
        public static final class SchoolsInfo
        {
            public static final String DESTRUCTION = "destruction";
            public static final String PROTECTION = "protection";
            public static final String TRANSMUTATION = "transmutation";
            public static final String NECROMANCY = "necromancy";
        }

        public static final class SubTypesInfo
        {
            public static final String AIR = "air";
            public static final String LIGHT = "light";
            public static final String PLANT = "plant";
            public static final String DEATH = "death";
        }
    }

    public static final class CapabilitiesInfo
    {
        public static final class CrystalPowerInfo
        {
            public static final String CAPABILITY_NAME = "crystal_power";
            public static final String POWER = "power";
            public static final String MAX_POWER = "max_power";
            public static final String MAX_RECEIVE = "max_receive";
            public static final String MAX_EXTRACT = "max_extract";
        }
    }

    public static final class CreativeTabsInfo
    {
        public static final String NAME = "Nedaire";
    }

    public static ResourceLocation modRL(String s)
    {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, s);
    }

    public static String modRLStr(String s)
    {
        return modRL(s).toString();
    }
}
