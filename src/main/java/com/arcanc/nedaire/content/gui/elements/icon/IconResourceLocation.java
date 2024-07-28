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
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class IconResourceLocation implements Icon
{
    private final ResourceLocation loc;
    private int blitOffset = 0, texX = 0, texY = 0, texW = 1, texH = 1;
    private int textureSizeX = 256, textureSizeY = 256;


    public IconResourceLocation(ResourceLocation texture)
    {
        this(texture, 0, 0, 0, 1, 1, 256, 256);
    }

    public IconResourceLocation(ResourceLocation texture, int blitOffset, int texX, int texY, int texW, int texH, int textureSizeX, int textureSizeY)
    {
        this.loc = texture;
        this.blitOffset = blitOffset;
        this.texX = texX;
        this.texY = texY;
        this.texW = texW;
        this.texH = texH;
        this.textureSizeX = textureSizeX;
        this.textureSizeY = textureSizeY;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int x, int y, int width, int height)
    {

        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1f);

        RenderHelper.blit(guiGraphics, loc, x, y, blitOffset, width, height, texX, texW, texY, texH, textureSizeX, textureSizeY);

        poseStack.popPose();
    }
}
