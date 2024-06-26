/**
 * @author ArcAnc
 * Created at: 26.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.items;

import com.arcanc.nedaire.util.helpers.ItemHelper;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

public class NBucketItem extends BucketItem implements ItemInterfaces.IMustAddToCreativeTab
{
    public NBucketItem(Fluid pContent, Properties pProperties)
    {
        super(pContent, pProperties);
    }

    @Override
    public @NotNull String getDescriptionId()
    {
        return ItemHelper.getRegistryName(this).toString().replace(':', '.').replace('/', '.');
    }
}
