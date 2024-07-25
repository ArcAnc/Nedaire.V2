/**
 * @author ArcAnc
 * Created at: 23.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.util.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RenderHelper
{
    public static @NotNull Minecraft mc()
    {
        return Minecraft.getInstance();
    }

    public static LocalPlayer clientPlayer()
    {
        return mc().player;
    }

    public static @NotNull TextureAtlas textureMap()
    {
        return mc().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
    }

    public static @NotNull TextureAtlasSprite getTexture(ResourceLocation location)
    {
        return textureMap().getSprite(location);
    }


    public static @NotNull List<Vec3> getSpiralAroundVector(@NotNull Vec3 startPos, @NotNull Vec3 finishPos, float radius, int steps, int turns)
    {
        Vec3 vector = startPos.vectorTo(finishPos);

        List<Vec3> list = new ArrayList<>();

        double length = vector.length();
        Vec3 unit = vector.normalize();

        Vec3 perp = unit.cross(new Vec3(0,0, 1)).normalize();

        for (int q = 0; q < steps; q++)
        {
            double t = (double) q / (steps - 1);
            double angle = turns * 2  * Math.PI * t;

            Vec3 normal = perp.scale(Math.cos(angle)).
                    add(unit.cross(perp).
                            scale(Math.sin(angle)));

            Vec3 point = startPos.add(unit.scale(t * length));

            list.add(point.add(normal.scale(radius)));
        }

        return list;
    }

    public static @NotNull List<Vec3> getCircleAroundPoint (@NotNull Vec3 point, float radius, int segments)
    {
        List<Vec3> circle = new ArrayList<>();

        Vec3 axis = point.normalize();
        Vec3 orthoVector;

        if (Math.abs(axis.x) < Math.abs(axis.y) && Math.abs(axis.x) < Math.abs(axis.z))
        {
            orthoVector = new Vec3(1, 0, 0);
        } else if (Math.abs(axis.y) < Math.abs(axis.x) && Math.abs(axis.y) < Math.abs(axis.z))
        {
            orthoVector = new Vec3(0, 1, 0);
        } else {
            orthoVector = new Vec3(0, 0, 1);
        }

        Vec3 tangent = axis.cross(orthoVector).normalize();
        Vec3 bitangent = axis.cross(tangent).normalize();

        for (int q = 0; q < segments; q++)
        {
            double angle = 2 * Math.PI * q / segments;
            double dynamicRadius = radius * (Math.cos(System.currentTimeMillis() / 1000d % 360) * 0.45d + 1.55d) ;
            double x = dynamicRadius * Math.cos(angle);
            double y = dynamicRadius * Math.sin(angle);

            circle.add(point.add(
                    x * tangent.x() + y * bitangent.x(),
                    x * tangent.y() + y * bitangent.y(),
                    x * tangent.z() + y * bitangent.z()));
        }


        return circle;
    }

    public static @NotNull List<List<Vec3>> closeFirstAndLastCircles(@NotNull final List<Vec3> center, @NotNull List<List<Vec3>> circles)
    {
        Vec3 firstPoint = center.getFirst();
        Vec3 lastPoint = center.getLast();

        List<Vec3> circleFirst = circles.getFirst();
        List<Vec3> circleLast = circles.getLast();

        for (int q = 0; q < circleFirst.size(); q++)
        {
            circleFirst.set(q, firstPoint);
            circleLast.set(q, lastPoint);
        }

        return circles;
    }

    public static @NotNull List<List<Vec3>> getCirclesAroundPoints(@NotNull List<Vec3> points, float radius, int segments, boolean isEssence)
    {
        List<List<Vec3>> circles = new ArrayList<>();

        for (int q = 0; q < points.size(); q++)
        {
            Vec3 center = points.get(q);
            if (isEssence)
            {
                if (q == 1)
                    circles.add(getCircleAroundPoint(center, radius * 0.4f, segments));
                else if (q == 2)
                    circles.add(getCircleAroundPoint(center, radius * 0.7f, segments));
                else
                    circles.add(getCircleAroundPoint(center, radius, segments));
            }
            else
                circles.add(getCircleAroundPoint(center, radius, segments));
        }

        if (isEssence)
            return closeFirstAndLastCircles(points, circles);

        return circles;
    }


    /*0 - red
    * 1 - green
    * 2 - blue
    * 3 - alpha*/
    public static int @NotNull [] splitRGBA(int color)
    {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8)  & 0xFF;
        int b =  color        & 0xFF;
        int a = (color >> 24) & 0xFF;

        return new int[] { r, g, b, a };
    }
}
