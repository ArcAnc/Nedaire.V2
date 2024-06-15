/**
 * @author ArcAnc
 * Created at: 11.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.magic;

import net.minecraft.resources.ResourceLocation;

public class SubType
{
    private final ResourceLocation name;

    public SubType (ResourceLocation location)
    {
        this.name = location;
    }

    public ResourceLocation getName() {
        return name;
    }
}
