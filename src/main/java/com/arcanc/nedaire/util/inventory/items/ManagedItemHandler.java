/**
 * @author ArcAnc
 * Created at: 22.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.util.inventory.items;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ManagedItemHandler extends SimpleItemHandler
{
    protected List<ItemStackHolder> inputSlots;
    protected List<ItemStackHolder> outputSlots;
    protected List<ItemStackHolder> modifierSlots;


    protected IItemHandler inputHandler;
    protected IItemHandler outputHandler;
    protected IItemHandler modifierHandler;
    protected IItemHandler allHandler;

    public ManagedItemHandler (@Nullable IInventoryCallback callback)
    {
        super(callback);
        this.inputSlots = new ArrayList<>();
        this.outputSlots = new ArrayList<>();
        this.modifierSlots = new ArrayList<>();
    }

    public ManagedItemHandler addSlots(SlotType type, int amount)
    {
        for (int q = 0; q < amount; q++)
            addTypedSlot(ItemStackHolder.newBuilder().
                    setSlotType(type).
                    build());
        return this;
    }
    public ManagedItemHandler addSlots(SlotType type, int amount, Predicate<ItemStack> validator)
    {
        for (int q = 0; q < amount; q++)
            addTypedSlot(ItemStackHolder.newBuilder().
                    setSlotType(type).
                    setValidator(validator).
                    build());
        return this;
    }

    public ManagedItemHandler addTypedSlot(ItemStackHolder holder)
    {
        this.slots.add(holder);
        switch (holder.getSlotType())
        {
            case INPUT -> inputSlots.add(holder);
            case OUTPUT -> outputSlots.add(holder);
            case MODIFIERS -> modifierSlots.add(holder);
        }
        return this;
    }

    public void initHandlers()
    {
        inputHandler = new SimpleItemHandler(inputSlots, callback);
        outputHandler = new SimpleItemHandler(outputSlots, callback);
        modifierHandler = new SimpleItemHandler(modifierSlots, callback);
        allHandler = new SimpleItemHandler(slots, callback);
    }

    @Override
    public IItemHandler getHandler(@NotNull SlotType type)
    {
        return switch (type)
        {
            case ALL -> allHandler;
            case INPUT -> inputHandler;
            case OUTPUT -> outputHandler;
            case MODIFIERS -> modifierHandler;
        };
    }
}
