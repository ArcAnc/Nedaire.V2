/**
 * @author ArcAnc
 * Created at: 14.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.util.helpers;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ItemHelper
{
    public static ResourceLocation getRegistryName(Item item)
    {
        return BuiltInRegistries.ITEM.getKey(item);
    }
}
