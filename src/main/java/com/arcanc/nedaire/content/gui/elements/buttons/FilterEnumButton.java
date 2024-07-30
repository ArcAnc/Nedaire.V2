/**
 * @author ArcAnc
 * Created at: 28.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.gui.elements.buttons;

import com.arcanc.nedaire.content.gui.elements.icon.EnumIconSet;
import com.arcanc.nedaire.util.NDatabase;
import com.arcanc.nedaire.util.filter.FilterType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class FilterEnumButton<T extends Enum<T>> extends Button
{
    private FilterType<T> data;
    private final EnumIconSet iconSet;
    private final OnPress<T> onPress;

    public FilterEnumButton(int x, int y, int width, int height, @NotNull FilterType<T> data, EnumIconSet iconSet, OnPress<T> onPress, String @NotNull [] tooltip)
    {
        super(x, y, width, height, Component.empty(), button -> {}, DEFAULT_NARRATION);
        this.iconSet = iconSet;
        this.data = data;
        this.onPress = onPress;
        setTooltip(Tooltip.create(Component.translatable(NDatabase.GUIInfo.Descriptions.getDescription(tooltip[data.getValue().ordinal()]))));
    }

    @Override
    public void onPress()
    {
        this.onPress.onPress(this);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        guiGraphics.pose().pushPose();
        {
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            int index = isHovered() ? data.getValue().ordinal() : data.possibleValues().length + data.getValue().ordinal();
            iconSet.get(index).render(guiGraphics, getX(), getY(), getWidth(), getHeight());
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
        guiGraphics.pose().popPose();
    }

    public FilterType<T> getData()
    {
        return data;
    }

    public void setData(FilterType<T> data)
    {
        this.data = data;
    }

    @OnlyIn(Dist.CLIENT)
    public interface OnPress<T extends Enum<T>> {
        void onPress(FilterEnumButton<T> button);
    }
}
