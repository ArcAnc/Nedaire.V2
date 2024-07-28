/**
 * @author ArcAnc
 * Created at: 27.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.gui.elements.icon;

import com.arcanc.nedaire.util.helpers.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/*FIXME: может криво отрисовывать размер иконки или сдвигать с необходимой позиции. Проверить на работоспособность и пофиксить если что*/
public class IconItemStack implements Icon
{
    private final boolean overlay;
    private final ItemStack stack;

    public IconItemStack(ItemStack stack, boolean requiredOverlay)
    {
        this.stack = stack;
        this.overlay = requiredOverlay;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int x, int y, int width, int height)
    {
        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();

        float sizeX = width/16f;
        float sizeY = height/16f;

        poseStack.scale(sizeX, sizeY, 1f);

        RenderHelper.renderItemStack(guiGraphics, this.stack, x, y, overlay);

        poseStack.scale(-sizeX, -sizeY, 1f);

        poseStack.popPose();
    }

}
