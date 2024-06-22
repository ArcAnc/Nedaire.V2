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

import java.util.function.Function;

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
        public static final class EnergonInfo
        {
            public static final String CAPABILITY_NAME = "energon";
            public static final String POWER = "power";
            public static final String MAX_POWER = "max_power";
            public static final String MAX_RECEIVE = "max_receive";
            public static final String MAX_EXTRACT = "max_extract";
        }

        public static final class InventoryInfo
        {
            public static final String CAPABILITY_NAME = "item_handler";

            public static final String ITEMS = "items";
            public static final String SLOT = "slot";

            public static final class ItemStackHolderInfo
            {
                public static final String ITEM = "item";
                public static final String CAPACITY = "capacity";
                public static final String SLOT_TYPE = "slot_type";
            }
        }
    }

    public static final class CreativeTabsInfo
    {
        public static final String NAME = "Nedaire";
    }

    public static final class MultiblocksInfo
    {
        public static final String TAG_LOCATION = modRLStr("multiblock");

        public static class Serializing
        {
            public static final String TYPE = "type";

            public static final String POSITION = "position";
            public static final String STATE = "state";
            public static final String TAG = "tag";
            public static final String SHAPE = "shape";
            public static final String IS_TRIGGER = "isTrigger";

            public static final String INFO_ARRAY = "info_array";
        }

        private static final Function<String, String> nameBuilder = (builder) -> TAG_LOCATION + '.' + builder;

        public static final String IS_FORMED = nameBuilder.apply("is_formed");

        public static final String ROTATION = nameBuilder.apply("rotation");

        public static final String IS_MASTER = nameBuilder.apply("is_master");
        public static final String MASTER_POS = nameBuilder.apply("master_pos");
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
