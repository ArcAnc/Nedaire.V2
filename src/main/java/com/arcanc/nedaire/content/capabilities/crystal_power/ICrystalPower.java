/**
 * @author ArcAnc
 * Created at: 13.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.capabilities.crystal_power;

public interface ICrystalPower
{
    float getCP();
    float getCPMax();
    void setCP(float amount);

    float addCP(float add, boolean simulate);

    float extractCP(float extract, boolean simulate);

    boolean canExtract();
    boolean canInsert();
}
