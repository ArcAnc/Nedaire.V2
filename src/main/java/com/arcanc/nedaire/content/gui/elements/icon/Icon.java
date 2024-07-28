/**
 * @author ArcAnc
 * Created at: 27.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.gui.elements.icon;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public interface Icon
{
    void render(GuiGraphics guiGraphics, int x, int y, int width, int height);

    static @NotNull IconResourceLocation of(ResourceLocation loc, int blitOffset, int texX, int texY, int texW, int texH, int textureSizeX, int textureSizeY)
    {
        return new IconResourceLocation(loc, blitOffset, texX, texY, texW, texH, textureSizeX, textureSizeY);
    }

    static @NotNull IconItemStack of (ItemStack stack, boolean overlay)
    {
        return new IconItemStack(stack, overlay);
    }

    static @NotNull IconItemStack of (ItemLike stack)
    {
        return of(stack, false);
    }

    static @NotNull IconItemStack of (ItemLike stack, boolean overlay)
    {
        return of(new ItemStack(stack), overlay);
    }
}
