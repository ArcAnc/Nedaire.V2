/**
 * @author ArcAnc
 * Created at: 28.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.gui.elements.icon;

import com.arcanc.nedaire.util.filter.FilterType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record EnumIconSet(List<Icon> iconList)
{
    public static <T extends Enum<T>> @NotNull EnumIconSet of(@NotNull FilterType<T> filterType, @NotNull ResourceLocation loc, int blitOffset, int texX, int texY, int texW, int texH)
    {
        return EnumIconSet.of(filterType, loc, blitOffset, texX, texY, texW, texH, 256, 256);
    }

    public static <T extends Enum<T>>@NotNull EnumIconSet of(@NotNull FilterType<T> filterType, @NotNull ResourceLocation loc, int blitOffset, int texX, int texY, int texW, int texH, int textureSizeX, int textureSizeY)
    {
        List<Icon> icons = new ArrayList<>();
        for (int q = 0; q < filterType.size() * 2; q++)
        {
            T type = filterType.possibleValues()[q % filterType.size()];
            icons.add(q, Icon.of(loc, blitOffset, texX + texW * (q / filterType.size()), texY + type.ordinal() * texH, texW, texH, textureSizeX, textureSizeY));
        }
        return new EnumIconSet(icons);
    }

    public Icon get(int index)
    {
        return iconList.get(index);
    }
}
