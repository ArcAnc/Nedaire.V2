/**
 * @author ArcAnc
 * Created at: 28.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.util.filter;

public interface FilterType<T extends Enum<T>>
{
    T getValue();

    int size();

    T[] possibleValues();
}
