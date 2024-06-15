/**
 * @author ArcAnc
 * Created at: 14.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.util.helpers;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.NeoForgeRegistriesSetup;

public class BlockHelper
{
    public static final class BlockProperties
    {

        public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
        public static final DirectionProperty FACING = BlockStateProperties.FACING;
        public static final DirectionProperty VERTICAL_ATTACHMENT = DirectionProperty.create("vertical_attachment", Direction.UP, Direction.DOWN);
        public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
        public static final BooleanProperty MIRRORED = BooleanProperty.create("mirrored");
        public static final BooleanProperty ENABLED =  BlockStateProperties.ENABLED;
        public static final BooleanProperty LIT = BlockStateProperties.LIT;

        public static final BooleanProperty MULTIBLOCK_SLAVE = BooleanProperty.create("multiblock_slave");
    }

    public static ResourceLocation getRegistryName (Block block)
    {
        return BuiltInRegistries.BLOCK.getKey(block);
    }
}
