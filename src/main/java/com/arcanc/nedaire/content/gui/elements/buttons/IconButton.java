/**
 * @author ArcAnc
 * Created at: 27.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.gui.elements.buttons;

import com.arcanc.nedaire.content.gui.elements.icon.IconSet;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class IconButton extends Button
{
    private final IconSet icon;

    public IconButton(int x, int y, int width, int height, IconSet icon, Button.OnPress onPress, Tooltip tooltip, Button.CreateNarration narration)
    {
        super(x, y, width, height, Component.empty(), onPress, narration);
        setTooltip(tooltip);
        this.icon = icon;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        guiGraphics.pose().pushPose();
        {
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            icon.get(isActive() ? isHovered() ? 0 : 1 : 2).render(guiGraphics, getX(), getY(), getWidth(), getHeight());
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
        guiGraphics.pose().popPose();
    }
}
