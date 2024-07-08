/**
 * @author ArcAnc
 * Created at: 29.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.api;

import com.arcanc.nedaire.content.block.block_entity.NodeBlockEntity;
import com.arcanc.nedaire.util.NDatabase;

import java.util.function.Consumer;

public enum NodeSubType
{
    NORMAL(NDatabase.BlocksInfo.Names.NodeTypesInfo.Modifier.NORMAL, 1.0f, 1.0f, nodeBlockEntity -> {}),
    BRIGHT(NDatabase.BlocksInfo.Names.NodeTypesInfo.Modifier.BRIGHT, 2.0f, 2.0f, nodeBlockEntity -> {}),
    PALE (NDatabase.BlocksInfo.Names.NodeTypesInfo.Modifier.PALE, 0.5f, 0.5f, nodeBlockEntity -> {}),
    FADING(NDatabase.BlocksInfo.Names.NodeTypesInfo.Modifier.FADING, 0.0f, 0.15f, nodeBlockEntity -> {});

    public final String name;
    public final float regenModifier;
    public final float timeModifier;
    public final Consumer<? extends NodeBlockEntity> actions;

    NodeSubType(String name, float regenModifier, float timeModifier, Consumer<? extends NodeBlockEntity> actions)
    {
        this.name = name;
        this.regenModifier = regenModifier;
        this.timeModifier = timeModifier;
        this.actions = actions;
    }
}
