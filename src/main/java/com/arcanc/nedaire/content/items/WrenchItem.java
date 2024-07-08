/**
 * @author ArcAnc
 * Created at: 07.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.items;

import com.arcanc.nedaire.content.block.BlockInterfaces;
import com.arcanc.nedaire.util.helpers.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class WrenchItem extends NBaseItem
{
    public WrenchItem(Properties pProperties)
    {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResult onItemUseFirst(@NotNull ItemStack stack, @NotNull UseOnContext context)
    {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return BlockHelper.castTileEntity(level, pos, BlockInterfaces.INWrencheable.class).
                map(tile -> tile.onUsed(stack, context)).
                orElse(super.onItemUseFirst(stack, context));
    }
}
