/**
 * @author ArcAnc
 * Created at: 14.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.util.helpers;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class BlockHelper
{
    public static final class BlockProperties
    {
        public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
        public static final DirectionProperty FACING = BlockStateProperties.FACING;
        public static final DirectionProperty VERTICAL_ATTACHMENT = DirectionProperty.create("vertical_attachment", Direction.UP, Direction.DOWN);
        public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
        public static final BooleanProperty ENABLED =  BlockStateProperties.ENABLED;
        public static final BooleanProperty LIT = BlockStateProperties.LIT;

        public static final BooleanProperty MIRRORED = BooleanProperty.create("mirrored");
        public static final BooleanProperty MULTIBLOCK_SLAVE = BooleanProperty.create("multiblock_slave");
    }

    public static class BlockMatcher
    {
        private final static List<MatcherPredicate> GLOBAL_MATCHERS = new ArrayList<>();
        private final static List<Preprocessor> PREPROCESSING = new ArrayList<>();

        public static void addPredicate(MatcherPredicate newPredicate)
        {
            GLOBAL_MATCHERS.add(newPredicate);
        }

        public static void addPreprocessor(Preprocessor preprocessor)
        {
            PREPROCESSING.add(preprocessor);
        }

        public static boolean matches(BlockState expected, BlockState found, Level world, BlockPos pos)
        {
            return matches(expected, found, world, pos, ImmutableList.of());
        }

        public static boolean matches(BlockState expected, BlockState found, Level world, BlockPos pos,
                                     List<MatcherPredicate> additional)
        {
            for(Preprocessor p : PREPROCESSING)
                found = p.preprocessFoundState(expected, found, world, pos);
            BlockState finalFound = found;
            return Stream.concat(GLOBAL_MATCHERS.stream(), additional.stream())
                    .map(pred -> pred.matches(expected, finalFound, world, pos))
                    .reduce(true, (accumulator, elem) -> elem && accumulator);
        }

        public interface MatcherPredicate
        {
            boolean matches(BlockState expected, BlockState found, @Nullable Level world, @Nullable BlockPos pos);
        }

        public interface Preprocessor
        {
            BlockState preprocessFoundState(
                    BlockState expected, BlockState found, @Nullable Level world, @Nullable BlockPos pos
            );
        }
    }

    public static @NotNull ResourceLocation getRegistryName (Block block)
    {
        return BuiltInRegistries.BLOCK.getKey(block);
    }
}
