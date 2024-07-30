/**
 * @author ArcAnc
 * Created at: 27.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.gui.screen;

import com.arcanc.nedaire.content.block.block_entity.FluidTransmitterBlockEntity;
import com.arcanc.nedaire.content.gui.container_menu.FluidTransmitterContainer;
import com.arcanc.nedaire.content.gui.elements.buttons.FilterEnumButton;
import com.arcanc.nedaire.content.gui.elements.buttons.IconButton;
import com.arcanc.nedaire.content.gui.elements.icon.EnumIconSet;
import com.arcanc.nedaire.content.gui.elements.icon.IconSet;
import com.arcanc.nedaire.util.NDatabase;
import com.arcanc.nedaire.util.filter.FilterMethod;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class FluidTransmitterScreen extends NContainerScreen<FluidTransmitterContainer>
{
    private static final ResourceLocation FILTER_GUI = NDatabase.GUIInfo.getTexturePath(NDatabase.GUIInfo.Filter.FILTER);

    private IconButton buttonPlus;
    private IconButton buttonMinus;

    public FluidTransmitterScreen(FluidTransmitterContainer slots, Inventory player, Component title)
    {
        super(slots, player, title);

        this.imageHeight = 175;
        this.imageWidth =  185;
    }

    @Override
    protected void init()
    {
        super.init();

        buttonMinus = createIconButton(116, 20, FILTER_GUI, 32, 128, this::decreaseTransferAmount,
                NDatabase.GUIInfo.Descriptions.Filter.BUTTON_MINUS);
        buttonPlus = createIconButton(165, 20, FILTER_GUI, 0, 128, this::increaseTransferAmount,
                NDatabase.GUIInfo.Descriptions.Filter.BUTTON_PLUS);

        buttonMinus.active = getMenu().blockEntity.getTransferAmount() >= FluidTransmitterBlockEntity.MIN_TRANSFER;
        buttonPlus.active = getMenu().blockEntity.getTransferAmount() <= FluidTransmitterBlockEntity.MAX_TRANSFER;

        this.addRenderableWidget(new FilterEnumButton<>(
                getGuiLeft() + 116,
                getGuiTop() + 38,
                16,
                16,
                menu.blockEntity.getFilterMethod().list(),
                EnumIconSet.of(
                        menu.blockEntity.getFilterMethod().list(),
                        FILTER_GUI,
                        1,
                        192,
                        0,
                        32,
                        32),
                button ->
                {
                    FilterMethod method =  menu.blockEntity.getFilterMethod();
                    FilterMethod.ListType type = method.list();

                    method = FilterMethod.withListType(method, FilterMethod.ListType.values()[(type.ordinal() + 1) % FilterMethod.ListType.values().length]);

                    FilterMethod finalMethod = method;
                    button.setData(method.list());
                    button.setTooltip(Tooltip.create(Component.translatable(NDatabase.GUIInfo.Descriptions.getDescription(NDatabase.GUIInfo.Descriptions.Filter.BUTTON_LIST_TYPE[button.getData().getValue().ordinal()]))));

                    sendTransmitterUpdate(tag -> tag.put(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.Filter.FILTER_TAG, finalMethod.writeToNbt()));
                },
                NDatabase.GUIInfo.Descriptions.Filter.BUTTON_LIST_TYPE));

        this.addRenderableWidget(new FilterEnumButton<>(
                getGuiLeft() + 134,
                getGuiTop() + 38,
                16,
                16,
                menu.blockEntity.getFilterMethod().route(),
                EnumIconSet.of(
                        menu.blockEntity.getFilterMethod().route(),
                        FILTER_GUI,
                        1,
                        64,
                        0,
                        32,
                        32),
                button ->
                {
                    FilterMethod method =  menu.blockEntity.getFilterMethod();
                    FilterMethod.Route route = method.route();

                    method = FilterMethod.withRoute(method, FilterMethod.Route.values()[(route.ordinal() + 1) % FilterMethod.Route.values().length]);

                    FilterMethod finalMethod = method;
                    button.setData(method.route());
                    button.setTooltip(Tooltip.create(Component.translatable(NDatabase.GUIInfo.Descriptions.getDescription(NDatabase.GUIInfo.Descriptions.Filter.BUTTON_ROUTE[button.getData().getValue().ordinal()]))));

                    sendTransmitterUpdate(tag -> tag.put(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.Filter.FILTER_TAG, finalMethod.writeToNbt()));
                },
                NDatabase.GUIInfo.Descriptions.Filter.BUTTON_ROUTE));

        this.addRenderableWidget(new FilterEnumButton<>(
                getGuiLeft() + 152,
                getGuiTop() + 38,
                16,
                16,
                menu.blockEntity.getFilterMethod().nbt(),
                EnumIconSet.of(
                        menu.blockEntity.getFilterMethod().nbt(),
                        FILTER_GUI,
                        1,
                        128,
                        0,
                        32,
                        32),
                button ->
                {
                    FilterMethod method =  menu.blockEntity.getFilterMethod();
                    FilterMethod.NBT nbt = method.nbt();

                    method = FilterMethod.withNBT(method, FilterMethod.NBT.values()[(nbt.ordinal() + 1) % FilterMethod.NBT.values().length]);

                    FilterMethod finalMethod = method;
                    button.setData(method.nbt());
                    button.setTooltip(Tooltip.create(Component.translatable(NDatabase.GUIInfo.Descriptions.getDescription(NDatabase.GUIInfo.Descriptions.Filter.BUTTON_NBT[button.getData().getValue().ordinal()]))));

                    sendTransmitterUpdate(tag -> tag.put(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.Filter.FILTER_TAG, finalMethod.writeToNbt()));
                },
                NDatabase.GUIInfo.Descriptions.Filter.BUTTON_NBT));

        this.addRenderableWidget(new FilterEnumButton<>(
                getGuiLeft() + 116,
                getGuiTop() + 56,
                16,
                16,
                menu.blockEntity.getFilterMethod().tag(),
                EnumIconSet.of(
                        menu.blockEntity.getFilterMethod().tag(),
                        FILTER_GUI,
                        1,
                        128,
                        64,
                        32,
                        32),
                button ->
                {
                    FilterMethod method =  menu.blockEntity.getFilterMethod();
                    FilterMethod.TagCheck nbtTag = method.tag();

                    method = FilterMethod.withTagCheck(method, FilterMethod.TagCheck.values()[(nbtTag.ordinal() + 1) % FilterMethod.TagCheck.values().length]);

                    FilterMethod finalMethod = method;
                    button.setData(method.tag());
                    button.setTooltip(Tooltip.create(Component.translatable(NDatabase.GUIInfo.Descriptions.getDescription(NDatabase.GUIInfo.Descriptions.Filter.BUTTON_TAG[button.getData().getValue().ordinal()]))));

                    sendTransmitterUpdate(tag -> tag.put(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.Filter.FILTER_TAG, finalMethod.writeToNbt()));
                },
                NDatabase.GUIInfo.Descriptions.Filter.BUTTON_TAG));

        this.addRenderableWidget(new FilterEnumButton<>(
                getGuiLeft() + 134,
                getGuiTop() + 56,
                16,
                16,
                menu.blockEntity.getFilterMethod().owner(),
                EnumIconSet.of(
                        menu.blockEntity.getFilterMethod().owner(),
                        FILTER_GUI,
                        1,
                        192,
                        64,
                        32,
                        32),
                button ->
                {
                    FilterMethod method =  menu.blockEntity.getFilterMethod();
                    FilterMethod.ModOwner owner = method.owner();

                    method = FilterMethod.withModOwner(method, FilterMethod.ModOwner.values()[(owner.ordinal() + 1) % FilterMethod.ModOwner.values().length]);

                    FilterMethod finalMethod = method;
                    button.setData(method.owner());
                    button.setTooltip(Tooltip.create(Component.translatable(NDatabase.GUIInfo.Descriptions.getDescription(NDatabase.GUIInfo.Descriptions.Filter.BUTTON_MOD_OWNER[button.getData().getValue().ordinal()]))));

                    sendTransmitterUpdate(tag -> tag.put(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.Filter.FILTER_TAG, finalMethod.writeToNbt()));
                },
                NDatabase.GUIInfo.Descriptions.Filter.BUTTON_MOD_OWNER));

        this.addRenderableWidget(new FilterEnumButton<>(
                getGuiLeft() + 152,
                getGuiTop() + 56,
                16,
                16,
                menu.blockEntity.getFilterMethod().target(),
                EnumIconSet.of(
                        menu.blockEntity.getFilterMethod().target(),
                        FILTER_GUI,
                        1,
                        0,
                        0,
                        32,
                        32),
                button ->
                {
                    FilterMethod method =  menu.blockEntity.getFilterMethod();
                    FilterMethod.Target target = method.target();

                    method = FilterMethod.withTarget(method, FilterMethod.Target.values()[(target.ordinal() + 1) % FilterMethod.Target.values().length]);

                    FilterMethod finalMethod = method;
                    button.setData(method.target());
                    button.setTooltip(Tooltip.create(Component.translatable(NDatabase.GUIInfo.Descriptions.getDescription(NDatabase.GUIInfo.Descriptions.Filter.BUTTON_TARGET[button.getData().getValue().ordinal()]))));

                    sendTransmitterUpdate(tag -> tag.put(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.Filter.FILTER_TAG, finalMethod.writeToNbt()));
                },
                NDatabase.GUIInfo.Descriptions.Filter.BUTTON_TARGET));

    }

    private @NotNull IconButton createIconButton(int x, int y, ResourceLocation guiTexture, int u, int v,
                                                 Consumer<IconButton> onPress, String descriptionKey)
    {
        IconButton button = new IconButton(getGuiLeft() + x, getGuiTop() + y, 14, 14,
                IconSet.of(guiTexture, 1, u, v, 32, 32),
                btn -> onPress.accept((IconButton) btn),
                Tooltip.create(Component.translatable(NDatabase.GUIInfo.Descriptions.getDescription(descriptionKey))),
                messageSupplier -> Component.translatable(NDatabase.GUIInfo.Descriptions.getDescription(descriptionKey)));

        this.addRenderableWidget(button);
        return button;
    }

    private void decreaseTransferAmount(IconButton button)
    {
        adjustTransferAmount(button, -1);
    }

    private void increaseTransferAmount(IconButton button)
    {
        adjustTransferAmount(button, 1);
    }

    private void adjustTransferAmount(IconButton button, int direction)
    {
        FluidTransmitterBlockEntity blockEntity = getMenu().blockEntity;
        int value = blockEntity.getTransferAmount();

        if (hasShiftDown() && hasControlDown())
            value += direction * 1000;
        else if (hasShiftDown())
            value += direction * 100;
        else if (hasControlDown())
            value += direction * 10;
        else
            value += direction;

        value = Mth.clamp(value, FluidTransmitterBlockEntity.MIN_TRANSFER, FluidTransmitterBlockEntity.MAX_TRANSFER);

        button.active = value != (direction == -1 ? FluidTransmitterBlockEntity.MIN_TRANSFER : FluidTransmitterBlockEntity.MAX_TRANSFER);
        (direction == -1 ? buttonPlus : buttonMinus).active = true;

        int finalValue = value;
        sendTransmitterUpdate(tag -> tag.putInt(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.FluidTransmitter.TRANSFER_AMOUNT, finalValue));
    }

    @Override
    protected void renderAdditionalInfo(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
    {
        Font font = minecraft.font;
        Component text = Component.literal(String.valueOf(menu.blockEntity.getTransferAmount()));
        guiGraphics.drawString(font, text, getGuiLeft() + 148 - font.width(text) / 2, getGuiTop() + 23, -1, false);
    }

    private void sendTransmitterUpdate(@NotNull Consumer<CompoundTag> addInfo)
    {
        CompoundTag message = new CompoundTag();
        BlockEntity blockEntity = getMenu().blockEntity;

        message.putInt("x", blockEntity.getBlockPos().getX());
        message.putInt("y", blockEntity.getBlockPos().getY());
        message.putInt("z", blockEntity.getBlockPos().getZ());

        addInfo.accept(message);

        sendUpdateToServer(message);
    }
}
