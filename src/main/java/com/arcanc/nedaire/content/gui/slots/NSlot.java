/**
 * @author ArcAnc
 * Created at: 26.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.gui.slots;

import com.arcanc.nedaire.util.NDatabase;
import com.arcanc.nedaire.util.inventory.items.ItemStackHolder;
import com.arcanc.nedaire.util.inventory.items.ManagedItemHandler;
import com.arcanc.nedaire.util.inventory.items.SimpleItemHandler;
import com.arcanc.nedaire.util.inventory.items.SlotType;
import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class NSlot extends SlotItemHandler
{
    public static final ResourceLocation BACKGROUND_STANDARD = NDatabase.modRL(NDatabase.GUIInfo.Slots.Textures.STANDARD);
    public static final ResourceLocation BACKGROUND_INPUT =  NDatabase.modRL(NDatabase.GUIInfo.Slots.Textures.INPUT);
    public static final ResourceLocation BACKGROUND_OUTPUT = NDatabase.modRL(NDatabase.GUIInfo.Slots.Textures.OUTPUT);
    public static final ResourceLocation BACKGROUND_BOTH =  NDatabase.modRL(NDatabase.GUIInfo.Slots.Textures.BOTH);

    protected boolean active = true;
    protected int panelIndex;
    protected Predicate<ItemStack> mayPlace;

    public NSlot(@NotNull IItemHandler inv, int panelIndex, int id, int x, int y)
    {
        super(inv, id, x, y);
        this.panelIndex = panelIndex;
        mayPlace = $ -> true;
    }

    public NSlot(@NotNull IItemHandler inv, int panelIndex, int id, int x, int y, @NotNull Predicate<ItemStack> mayPlace)
    {
        this(inv, panelIndex, id, x, y);
        Preconditions.checkNotNull(mayPlace);
        this.mayPlace = mayPlace;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack)
    {
        return super.mayPlace(stack);
    }

    @Override
    public @NotNull NSlot setBackground(@NotNull ResourceLocation atlas, @NotNull ResourceLocation sprite)
    {
        super.setBackground(atlas, sprite);
        return this;
    }

    public @NotNull NSlot setColoredBackground()
    {
        if (getItemHandler() instanceof ManagedItemHandler handler)
        {
            ItemStackHolder holder = handler.getHandler(SlotType.ALL).getSlot(getSlotIndex());
            if (handler.getHandler(SlotType.INPUT).getHolders().contains(holder))
            {
                setBackground(InventoryMenu.BLOCK_ATLAS, BACKGROUND_INPUT);
                return this;
            }
            else if (handler.getHandler(SlotType.OUTPUT).getHolders().contains(holder))
            {
                setBackground(InventoryMenu.BLOCK_ATLAS, BACKGROUND_OUTPUT);
                return this;
            }
            else
            {
                setBackground(InventoryMenu.BLOCK_ATLAS, BACKGROUND_BOTH);
                return this;
            }
        }
        setBackground(InventoryMenu.BLOCK_ATLAS, BACKGROUND_STANDARD);
        return this;
    }


    @Override
    public void set(@NotNull ItemStack stack)
    {
        if (getItemHandler() instanceof SimpleItemHandler handler)
        {
            handler.setStackInSlot(getSlotIndex(), stack);
            handler.onInventoryChange(getSlotIndex());
        }
    }

    @Override
    public void initialize(@NotNull ItemStack stack)
    {
        if (getItemHandler() instanceof SimpleItemHandler handler)
        {
            handler.setStackInSlot(getSlotIndex(), stack);
            handler.onInventoryChange(getSlotIndex());
        }
    }

    /**
     * @param active New state of slot
     * @return this, to allow chaining.
     */
    public @NotNull NSlot setActive(boolean active)
    {
        this.active = active;
        return this;
    }

    @Override
    public boolean isActive()
    {
        return super.isActive();
    }

    public static class ItemHandlerGhost extends NSlot
    {

        public ItemHandlerGhost(@NotNull IItemHandler itemHandler, int panelIndex, int index, int xPosition, int yPosition, @NotNull Predicate<ItemStack> mayPlace)
        {
            super(itemHandler, panelIndex, index, xPosition, yPosition, mayPlace);
        }

        public ItemHandlerGhost(@NotNull IItemHandler itemHandler, int panelIndex, int index, int xPosition, int yPosition)
        {
            this(itemHandler, panelIndex, index, xPosition, yPosition, $ -> true);
        }

        @Override
        public boolean mayPickup(@NotNull Player playerIn)
        {
            return false;
        }
    }
}
