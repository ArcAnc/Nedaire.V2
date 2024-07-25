/**
 * @author ArcAnc
 * Created at: 08.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.items;

import com.arcanc.nedaire.util.Upgrade;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class UpgradeItem extends NBaseItem
{
    private final Upgrade lvl;
    public UpgradeItem(Upgrade lvl, Properties pProperties)
    {
        super(pProperties);
        this.lvl = lvl;
    }

    public Upgrade getLvl()
    {
        return lvl;
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context)
    {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();

        if (Upgrade.canApplyUpgrade(level, pos, stack))
            return InteractionResult.sidedSuccess(level.isClientSide());

        return super.useOn(context);
    }
}
