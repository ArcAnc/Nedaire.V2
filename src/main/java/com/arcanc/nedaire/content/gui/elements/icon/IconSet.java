/**
 * @author ArcAnc
 * Created at: 27.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.gui.elements.icon;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record IconSet(Icon focused, Icon enabled, Icon disabled)
{
    public static @NotNull IconSet of(@NotNull ResourceLocation loc, int blitOffset, int texX, int texY, int texW, int texH)
    {
        return IconSet.of(loc, blitOffset, texX, texY, texW, texH, 256, 256);
    }

    public static @NotNull IconSet of(@NotNull ResourceLocation loc, int blitOffset, int texX, int texY, int texW, int texH, int textureSizeX, int textureSizeY)
    {
        return new IconSet(
                Icon.of(loc, blitOffset, texX, texY, texW, texH, textureSizeX, textureSizeY),
                Icon.of(loc, blitOffset, texX, texY + texH, texW, texH, textureSizeX, textureSizeY),
                Icon.of(loc, blitOffset, texX, texY + texH * 2, texW, texH, textureSizeX, textureSizeY));
    }

    public Icon get(int index)
    {
        return index == 0 ? focused : index == 1 ? enabled : disabled;
    }
}
