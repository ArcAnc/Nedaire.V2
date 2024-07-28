/**
 * @author ArcAnc
 * Created at: 26.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.gui.screen;

import com.arcanc.nedaire.content.gui.container_menu.NContainerMenu;
import com.arcanc.nedaire.content.gui.info.InfoArea;
import com.arcanc.nedaire.content.gui.slots.NSlot;
import com.arcanc.nedaire.content.nerwork.messages.NetworkEngine;
import com.arcanc.nedaire.content.nerwork.messages.packets.C2SContainerUpdatePacket;
import com.arcanc.nedaire.util.NDatabase;
import com.arcanc.nedaire.util.helpers.RenderHelper;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class NContainerScreen<T extends NContainerMenu> extends AbstractContainerScreen<T>
{

    public static final ResourceLocation MIDDLE = NDatabase.GUIInfo.getTexturePath(NDatabase.GUIInfo.Background.Textures.MIDDLE);
    public static final ResourceLocation MIDDLE_TOP = NDatabase.GUIInfo.getTexturePath(NDatabase.GUIInfo.Background.Textures.MIDDLE_TOP);
    public static final ResourceLocation MIDDLE_BOT = NDatabase.GUIInfo.getTexturePath(NDatabase.GUIInfo.Background.Textures.MIDDLE_BOTTOM);
    public static final ResourceLocation MIDDLE_LEFT = NDatabase.GUIInfo.getTexturePath(NDatabase.GUIInfo.Background.Textures.MIDDLE_LEFT);
    public static final ResourceLocation MIDDLE_RIGHT = NDatabase.GUIInfo.getTexturePath(NDatabase.GUIInfo.Background.Textures.MIDDLE_RIGHT);
    public static final ResourceLocation LEFT_TOP = NDatabase.GUIInfo.getTexturePath(NDatabase.GUIInfo.Background.Textures.LEFT_TOP);
    public static final ResourceLocation LEFT_BOT = NDatabase.GUIInfo.getTexturePath(NDatabase.GUIInfo.Background.Textures.LEFT_BOTTOM);
    public static final ResourceLocation RIGHT_TOP = NDatabase.GUIInfo.getTexturePath(NDatabase.GUIInfo.Background.Textures.RIGHT_TOP);
    public static final ResourceLocation RIGHT_BOT = NDatabase.GUIInfo.getTexturePath(NDatabase.GUIInfo.Background.Textures.RIGHT_BOTTOM);

    public NContainerScreen(T slots, Inventory player, Component title)
    {
        super(slots, player, title);
        this.inventoryLabelY = this.imageHeight - 91;
    }

    @NotNull
    protected List<InfoArea> makeInfoAreas()
    {
        return ImmutableList.of();
    }

    @Override
    protected void init()
    {
        super.init();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        renderAdditionalInfo(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    protected abstract void renderAdditionalInfo(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks);

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTicks, int x, int y)
    {
        int x_pos = this.leftPos;
        int	y_pos = this.topPos;

        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        guiGraphics.blit(LEFT_TOP, x_pos, y_pos, 0, 0, 0, 8, 8, 8, 8);

        guiGraphics.blit(MIDDLE_TOP, x_pos + 8, y_pos, 0, 0, 0, this.imageWidth - 16, 8, 8, 8);

        guiGraphics.blit(RIGHT_TOP, x_pos + this.imageWidth - 8, y_pos, 0, 0, 0, 8, 8, 8, 8);

        guiGraphics.blit(MIDDLE_LEFT, x_pos, y_pos + 8, 0, 0, 0, 8, this.imageHeight - 16, 8, 8);

        guiGraphics.blit(LEFT_BOT, x_pos, y_pos + this.imageHeight - 8, 0, 0, 0, 8, 8, 8, 8);

        guiGraphics.blit(MIDDLE_BOT, x_pos + 8, y_pos + this.imageHeight - 8, 0, 0, 0, this.imageWidth - 16, 8, 8, 8);

        guiGraphics.blit(RIGHT_BOT, x_pos + this.imageWidth - 8, y_pos + this.imageHeight - 8, 0, 0, 0, 8, 8, 8, 8);

        guiGraphics.blit(MIDDLE_RIGHT, x_pos + this.imageWidth - 8, y_pos + 8, 0, 0, 0, 8, this.imageHeight - 16, 8, 8);

        guiGraphics.blit(MIDDLE, x_pos + 8, y_pos + 8, 0, 0, 0, this.imageWidth - 16, this.imageHeight - 16, 8, 8);

        poseStack.popPose();
    }

    @Override
    public void renderTransparentBackground(@NotNull GuiGraphics guiGraphics)
    {
        guiGraphics.fillGradient(0, 0, this.width, this.height, -100, -1072689136, -804253680);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY)
    {
    }

    @Override
    public void renderSlot(@NotNull GuiGraphics guiGraphics, @NotNull Slot slot)
    {
        int i = slot.x;
        int j = slot.y;
        ItemStack itemstack = slot.getItem();
        boolean flag = false;
        boolean flag1 = slot == this.clickedSlot && !this.draggingItem.isEmpty() && !this.isSplittingStack;
        ItemStack itemstack1 = this.menu.getCarried();
        String s = null;
        if (slot == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !itemstack.isEmpty())
            itemstack = itemstack.copyWithCount(itemstack.getCount() / 2);
        else if (this.isQuickCrafting && this.quickCraftSlots.contains(slot) && !itemstack1.isEmpty())
        {
            if (this.quickCraftSlots.size() == 1)
                return;

            if (AbstractContainerMenu.canItemQuickReplace(slot, itemstack1, true) && this.menu.canDragTo(slot))
            {
                flag = true;
                int k = Math.min(itemstack1.getMaxStackSize(), slot.getMaxStackSize(itemstack1));
                int l = slot.getItem().isEmpty() ? 0 : slot.getItem().getCount();
                int i1 = AbstractContainerMenu.getQuickCraftPlaceCount(this.quickCraftSlots, this.quickCraftingType, itemstack1) + l;
                if (i1 > k)
                {
                    i1 = k;
                    s = ChatFormatting.YELLOW.toString() + k;
                }
                itemstack = itemstack1.copyWithCount(i1);
            }
            else
            {
                this.quickCraftSlots.remove(slot);
                this.recalculateQuickCraftRemaining();
            }
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
        if (slot.isActive())
        {
            Pair<ResourceLocation, ResourceLocation> pair = slot.getNoItemIcon();
            if (pair != null)
            {
                TextureAtlasSprite textureatlassprite = this.minecraft.getTextureAtlas(pair.getFirst()).apply(pair.getSecond());
                RenderSystem.setShaderTexture(0, textureatlassprite.atlasLocation());
                guiGraphics.blit(i-1, j-1, 0, 18, 18, textureatlassprite);
            }
        }

        if (!flag1)
        {
            if (flag)
                guiGraphics.fill(i, j, i + 16, j + 16, -2130706433);

            RenderSystem.enableDepthTest();
            if (slot instanceof NSlot.ItemHandlerGhost)
                RenderHelper.renderFakeItemTransparent(itemstack, getGuiLeft() + i, getGuiTop() + j, 0.5f);
            else
            {
                guiGraphics.renderItem(itemstack, i, j, slot.x + slot.y * this.imageWidth);
                guiGraphics.renderItemDecorations(this.font, itemstack, i, j, s);
            }
            RenderSystem.disableDepthTest();
        }
        guiGraphics.pose().popPose();
    }


    protected void sendUpdateToServer(CompoundTag message)
    {
        NetworkEngine.sendToServer(new C2SContainerUpdatePacket(menu.containerId, message));
    }

}
