/**
 * @author ArcAnc
 * Created at: 02.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.util.helpers;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VoxelShapeHelper
{
    /*Thanks to @XFactHD updated voxel shape now rotating efficiently rotating*/
    public static VoxelShape rotateShape(@NotNull Direction from, @NotNull Direction to, @NotNull VoxelShape shape)
    {
        if (from == to)
            return shape;
        if (from.getAxis().isHorizontal() && to.getAxis().isHorizontal())
            return rotateShapeHorizontal(from, to, shape);
        else if (from.getAxis().isVertical() && to.getAxis().isVertical())
        {
            List<AABB> shapeBoxes = shape.toAabbs();
            VoxelShape result = Shapes.empty();

            for (AABB box : shapeBoxes)
            {
                box = new AABB(1 - box.maxX, 1 - box.maxY, 1 - box.maxZ, 1 - box.minX, 1 - box.minY, 1 - box.minZ);
                result = orUnoptimized(result, Shapes.create(box));
            }

            return result.optimize();
        }
        else if (from.getAxis().isVertical() && to.getAxis().isHorizontal())
        {
            List<AABB> shapeBoxes = shape.toAabbs();
            VoxelShape result = Shapes.empty();

            for (AABB box : shapeBoxes)
            {
                box = new AABB(box.minX, box.minZ, box.minY, box.maxX, box.maxZ, box.maxY);
                result = orUnoptimized(result, Shapes.create(box));
            }

            if (from == Direction.DOWN)
                return rotateShapeHorizontal(Direction.NORTH, to, result);
            else
                return rotateShapeHorizontal(Direction.SOUTH, to, result);
        }
        else if (from.getAxis().isHorizontal() && to.getAxis().isVertical())
        {
            VoxelShape result = rotateShapeHorizontal(from, Direction.NORTH, shape);

            List<AABB> shapeBoxes = result.toAabbs();
            result = Shapes.empty();
            if (to == Direction.DOWN)
                for (AABB box : shapeBoxes)
                {
                    box = new AABB(box.minX, box.minZ, box.minY, box.maxX, box.maxZ, box.maxY);
                    result = orUnoptimized(result, Shapes.create(box));
                }
            else
                for (AABB box : shapeBoxes)
                {
                    box = new AABB(box.minX, 1 - box.maxZ, box.minY, box.maxX, 1 - box.minZ, box.maxY);
                    result = orUnoptimized(result, Shapes.create(box));
                }
            return result.optimize();
        }
        return shape;
    }

    public static VoxelShape rotateShapeHorizontal(@NotNull Direction from, @NotNull Direction to, @NotNull VoxelShape shape)
    {
        if (from.getAxis().isVertical() || to.getAxis().isVertical())
            return shape;
        if (from == to)
            return shape;

        List<AABB> shapeBoxes = shape.toAabbs();
        int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;
        VoxelShape result = Shapes.empty();

        for (AABB box : shapeBoxes)
        {
            for (int i = 0; i < times; i++)
            {
                box = new AABB(1 - box.maxZ, box.minY, box.minX, 1 - box.minZ, box.maxY, box.maxX);
            }
            result = orUnoptimized(result, Shapes.create(box));
        }
        return result.optimize();
    }

    public static @NotNull VoxelShape orUnoptimized(@NotNull VoxelShape first, @NotNull VoxelShape second)
    {
        return Shapes.joinUnoptimized(first, second, BooleanOp.OR);
    }
}
