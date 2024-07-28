/**
 * @author ArcAnc
 * Created at: 14.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.items;

import com.arcanc.nedaire.util.helpers.ItemHelper;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class NBaseItem extends Item implements ItemInterfaces.IMustAddToCreativeTab
{
    public NBaseItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull String getDescriptionId()
    {
        return ItemHelper.getRegistryName(this).withPrefix("item.").toLanguageKey().replace(':', '.').replace('/', '.');
    }

    @Override
    public boolean addSelfToCreativeTab()
    {
        return ItemInterfaces.IMustAddToCreativeTab.super.addSelfToCreativeTab();
    }
}
