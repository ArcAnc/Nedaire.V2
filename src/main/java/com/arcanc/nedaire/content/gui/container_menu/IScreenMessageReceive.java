/**
 * @author ArcAnc
 * Created at: 23.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.gui.container_menu;

import net.minecraft.nbt.CompoundTag;

/*Code taken from Immersive Engineering. Thanks BluSunrize*/
public interface IScreenMessageReceive
{
    default void receiveMessageFromScreen(CompoundTag nbt)
    {
    }
}
