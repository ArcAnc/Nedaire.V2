/**
 * @author ArcAnc
 * Created at: 23.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.block.block_entity.ticking;


/*This code was taken from Immersive Engineering. Thanks BluSunrize, it's perfect*/
public interface NTickableBase
{
    default boolean canTickAny()
    {
        return true;
    }
}
