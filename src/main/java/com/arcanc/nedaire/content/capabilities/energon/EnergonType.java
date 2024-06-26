/**
 * @author ArcAnc
 * Created at: 26.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.capabilities.energon;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public record EnergonType(int color)
{
    public EnergonType(@NotNull Color color)
    {
        this(color.getRGB());
    }

    public EnergonType(int red, int green, int blue, int alpha)
    {
        this(new Color(red, green, blue, alpha));
    }
}
