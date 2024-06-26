/**
 * @author ArcAnc
 * Created at: 26.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.util.inventory.fluids;

public interface IFluidCallback
{
    default void onFluidChanged(int tank)
    {

    }

    default void clearTank(int tank)
    {

    }
}
