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
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class NDatabase
{
    public static final String MOD_ID = "nedaire";
    public static final String MOD_VERSION = "0.0.0";

    public static final class DataComponentsInfo
    {
        public static final String BLOCK_POS = "block_pos";
    }

    public static final class BlocksInfo
    {
        public static final class Names
        {
            public static final String NODE = "node";
            public static final class NodeTypesInfo
            {
                public static final String STANDARD = "standard";
                public static final String PURE = "pure";
                public static final String HUNGRY = "hungry";
                public static final String UNSTABLE = "unstable";

                public static final class Modifier
                {
                    public static final String NORMAL = "normal";
                    public static final String BRIGHT = "bright";
                    public static final String PALE = "pale";
                    public static final String FADING = "fading";
                }
            }

            public static final String FLUID_TRANSMITTER = "fluid_transmitter";
            public static final String FLUID_STORAGE = "fluid_storage";
        }

        public static final class BlockEntities
        {
            public static final class TagAddress
            {
                public static final class Machines
                {
                    public static final class RedstoneSensitive
                    {
                        public static final String REDSTONE_MOD = "redstone_mod";
                    }

                    public static final class Filter
                    {
                        public static final String FILTER_TAG = "filter_tag";
                        public static final String LIST_TYPE = "list_type";
                        public static final String ROUTE = "route";
                        public static final String NBT = "nbt";
                        public static final String TAG_CHECK = "tag_check";
                        public static final String MOD_OWNER = "mod_owner";
                        public static final String TARGET = "target";
                    }

                    public static final class FluidTransmitter
                    {
                        public static final String POS_LIST = "pos_list";
                        public static final String PREV_TARGET_INDEX = "prev_target_index";
                        public static final String TRANSFER_AMOUNT = "transfer_amount";
                    }
                }
            }
        }
    }

    public static final class ItemsInfo
    {
        public static final class Names
        {
            public static final String WRENCH = "wrench";
            public static final String UPGRADE = "upgrade";
        }
    }

    public static final class FluidsInfo
    {
        public static class Names
        {
            public static final String ENERGON_RED = "energon_red";
            public static final String ENERGON_BLUE = "energon_blue";
            public static final String ENERGON_YELLOW = "energon_yellow";
            public static final String ENERGON_GREEN = "energon_green";
            public static final String ENERGON_DARK = "energon_dark";
        }

        public static ResourceLocation getStillLoc(String name)
        {
            return modRL("block/fluids/" + name +"/still");
        }

        public static ResourceLocation getFlowLoc(String name)
        {
            return modRL("block/fluids/" + name +"/flow");
        }

        public static ResourceLocation getOverlayLoc(String name)
        {
            return modRL("block/fluids/" + name + "/overlay");
        }

        public static ResourceLocation getBlockLocation(String name)
        {
            return modRL("block/fluids/" + name + "/block");
        }

        public static ResourceLocation getBucketLocation(String name)
        {
            return modRL("fluids/" + name + "/bucket");
        }
    }

    public static final class EntitiesInfo
    {
        public static final class Transfer
        {
            public static final String STACK = "stack";
        }
    }

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
            public static final class EnergonTypeInfo
            {
                public static final String TAG_LOCATION = "energon_type";
                public static final String RED = "red";
                public static final String DARK = "dark";
                public static final String GREEN = "green";
                public static final String YELLOW = "yellow";
                public static final String BLUE = "blue";
            }
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

        public static final class FluidInfo
        {
            public static final String CAPABILITY_NAME = "fluid_handler";

            public static final String FLUIDS = "fluids";
            public static final String SLOT = "slot";

            public static final class FluidStackHolderInfo
            {
                public static final String FLUID = "fluid";
                public static final String CAPACITY = "capacity";
                public static final String TANK_TYPE = "tank_type";
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
    public static @NotNull ResourceLocation modRL(@NotNull String s)
    {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, s);
    }

    public static @NotNull String modRLStr(@NotNull String s)
    {
        return modRL(s).toString();
    }
}
