/**
 * @author ArcAnc
 * Created at: 08.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.util;

import com.arcanc.nedaire.Nedaire;
import com.arcanc.nedaire.content.block.BlockInterfaces;
import com.arcanc.nedaire.content.items.UpgradeItem;
import com.arcanc.nedaire.util.helpers.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public enum Upgrade
{
    SPARK("spark", 0, 1),
    STREAM("stream", 1, 5),
    STORM("storm", 2, 20),
    NOVA("nova", 3, 100),
    CREATIVE("creative", 4, 1000);

    private final String name;
    private final int lvl;
    private final int modifier;

    Upgrade (String name, int lvl, int modifier)
    {
        this.name = name;
        this.lvl = lvl;
        this.modifier = modifier;
    }

    public String getName()
    {
        return name;
    }

    public int getLvl()
    {
        return lvl;
    }

    public int getModifier()
    {
        return modifier;
    }

    public static boolean canApplyUpgrade(Level level, BlockPos pos, @NotNull ItemStack stack)
    {
        if (!(stack.getItem() instanceof UpgradeItem item))
            return false;
        return BlockHelper.castTileEntity(level, pos, BlockInterfaces.IUpgradeable.class).
                map(iUpgradeable -> iUpgradeable.applyUpgrade(item.getLvl())).
                orElse(false);
    }

    public static Optional<Upgrade> getUpgradeFromStack (@NotNull ItemStack stack)
    {
        if (!(stack.getItem() instanceof UpgradeItem item))
            return Optional.empty();
        else
            return Optional.of(item.getLvl());
    }

    public boolean isLowerThan(@NotNull Upgrade upg)
    {
        return this.getLvl() < upg.getLvl();
    }
}
