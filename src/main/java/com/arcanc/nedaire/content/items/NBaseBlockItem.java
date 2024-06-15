/**
 * @author ArcAnc
 * Created at: 15.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.items;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class NBaseBlockItem extends BlockItem implements ItemInterfaces.IMustAddToCreativeTab
{
    public NBaseBlockItem(Block pBlock, Properties pProperties)
    {
        super(pBlock, pProperties);
    }

    @Override
    public @NotNull String getDescriptionId()
    {
        return getBlock().getDescriptionId().replace(':', '.').replace('/', '.');
    }

    @Override
    public boolean addSelfToCreativeTab()
    {
        return ItemInterfaces.IMustAddToCreativeTab.super.addSelfToCreativeTab();
    }
}
