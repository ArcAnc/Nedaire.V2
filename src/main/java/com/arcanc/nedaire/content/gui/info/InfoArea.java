/**
 * @author ArcAnc
 * Created at: 26.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.gui.info;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

import java.util.List;

public abstract class InfoArea
{
    protected final Rect2i area;

    protected InfoArea(Rect2i area)
    {
        this.area = area;
    }

    public final void fillTooltip(int mouseX, int mouseY, List<Component> tooltip)
    {
        if(area.contains(mouseX, mouseY))
            fillTooltipOverArea(mouseX, mouseY, tooltip);
    }

    protected abstract void fillTooltipOverArea(int mouseX, int mouseY, List<Component> tooltip);

    public abstract void draw(GuiGraphics graphics);
}
