/**
 * @author ArcAnc
 * Created at: 16.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.util.helpers;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;

public class DirectionHelper
{
    public static Rotation getRotationBetweenFacings(Direction orig, Direction to)
    {
        if(to == orig)
            return Rotation.NONE;
        if(orig.getAxis()== Direction.Axis.Y || to.getAxis() == Direction.Axis.Y)
            return null;
        orig = orig.getClockWise();
        if(orig == to)
            return Rotation.CLOCKWISE_90;
        orig = orig.getClockWise();
        if(orig == to)
            return Rotation.CLOCKWISE_180;
        orig = orig.getClockWise();
        if(orig == to)
            return Rotation.COUNTERCLOCKWISE_90;
        return null;//This shouldn't ever happen
    }
}
