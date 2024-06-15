/**
 * @author ArcAnc
 * Created at: 14.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.items;

public class ItemInterfaces
{
    public interface ICustomModelProperties
    {
        void registerModelProperties();
    }

    public interface IMustAddToCreativeTab
    {
        default boolean addSelfToCreativeTab()
        {
            return true;
        }
    }
}
