/**
 * @author ArcAnc
 * Created at: 21.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.util.inventory.items;

public interface IInventoryCallback
{
    default void onInventoryChanged(int slot)
    {

    }

    default void clearSlot(int slot)
    {

    }
}
