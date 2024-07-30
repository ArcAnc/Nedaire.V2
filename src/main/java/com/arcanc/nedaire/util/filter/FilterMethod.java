/**
 * @author ArcAnc
 * Created at: 11.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.util.filter;

import com.arcanc.nedaire.content.block.block_entity.FluidTransmitterBlockEntity;
import com.arcanc.nedaire.util.NDatabase;
import com.arcanc.nedaire.util.helpers.FluidHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record FilterMethod(ListType list, Route route, NBT nbt, TagCheck tag, ModOwner owner, Target target)
{
    public static final FilterMethod DEFAULT = new FilterMethod(ListType.DENY, Route.OUTPUT, NBT.IGNORE_NBT, TagCheck.IGNORE, ModOwner.IGNORE, Target.ROUND_ROBIN);

    private static final Function<IItemHandler, Set<FluidStack>> fluidSetGetter = iItemHandler ->
            IntStream.range(0, iItemHandler.getSlots()).
                    mapToObj(iItemHandler::getStackInSlot).
                    flatMap(itemStack -> FluidHelper.getFluidHandler(itemStack).
                            map(fluidHandler -> IntStream.range(0, fluidHandler.getTanks()).
                                    mapToObj(fluidHandler::getFluidInTank).
                                    filter(fluid -> !fluid.isEmpty())
                            ).
                            orElse(Stream.empty())
                    ).
                    collect(Collectors.toSet());

    public enum ListType implements FilterType<ListType>
    {
        ALLOW((iItemHandler, stack) ->
        {
            Set<FluidStack> set = fluidSetGetter.apply(iItemHandler);
            return !set.isEmpty() && set.stream().anyMatch(fluidStack -> FluidStack.isSameFluid(fluidStack, stack));
        }),
        DENY((iItemHandler, stack) ->
        {
            Set<FluidStack> set = fluidSetGetter.apply(iItemHandler);
            return set.isEmpty() || set.stream().noneMatch(fluidStack -> FluidStack.isSameFluid(fluidStack, stack));
        });

        private final BiPredicate<IItemHandler, FluidStack> predicate;

        ListType (BiPredicate<IItemHandler, FluidStack> predicate)
        {
            this.predicate = predicate;
        }

        public BiPredicate<IItemHandler, FluidStack> getPredicate()
        {
            return predicate;
        }

        public boolean test(IItemHandler handler, FluidStack stack)
        {
            return predicate.test(handler, stack);
        }

        @Override
        public ListType getValue()
        {
            return this;
        }

        @Override
        public int size()
        {
            return possibleValues().length;
        }

        @Override
        public ListType @NotNull [] possibleValues()
        {
            return ListType.values();
        }
    }
    public enum Route implements FilterType<Route>
    {
        INPUT, OUTPUT, BIDIRECTION;

        @Override
        public Route getValue()
        {
            return this;
        }

        @Override
        public int size()
        {
            return possibleValues().length;
        }

        @Override
        public @NotNull Route[] possibleValues()
        {
            return Route.values();
        }
    }
    public enum NBT implements FilterType<NBT>
    {
        CHECK_NBT (((iItemHandler, stack) ->
        {
            Set<FluidStack> set = fluidSetGetter.apply(iItemHandler);
            return !set.isEmpty() && set.stream().anyMatch(fluidStack -> FluidStack.isSameFluidSameComponents(fluidStack, stack));
        })),
        IGNORE_NBT((iItemHandler, stack) -> true);

        private final BiPredicate<IItemHandler, FluidStack> predicate;

        NBT(BiPredicate<IItemHandler, FluidStack> stackPredicate)
        {
            this.predicate = stackPredicate;
        }

        public BiPredicate<IItemHandler, FluidStack> getPredicate()
        {
            return predicate;
        }

        public boolean test(IItemHandler handler, FluidStack stack)
        {
            return this.predicate.test(handler, stack);
        }

        @Override
        public NBT getValue()
        {
            return this;
        }

        @Override
        public int size()
        {
            return possibleValues().length;
        }

        @Override
        public @NotNull NBT[] possibleValues()
        {
            return NBT.values();
        }
    }
    public enum TagCheck implements FilterType<TagCheck>
    {
        USE ((iItemHandler, stack) ->
        {
            Set<FluidStack> set = fluidSetGetter.apply(iItemHandler);
            if (set.isEmpty()) return true;

            Set<TagKey<FluidType>> tagKeySet = set.stream().
                    flatMap(fluidStack -> NeoForgeRegistries.FLUID_TYPES.getHolder(NeoForgeRegistries.FLUID_TYPES.getKey(fluidStack.getFluidType())).
                            map(fluidTypeReference -> fluidTypeReference.tags().collect(Collectors.toSet()).stream()).
                            orElse(Stream.empty())
                    ).
                    collect(Collectors.toSet());

            Set<TagKey<FluidType>> stackTags = NeoForgeRegistries.FLUID_TYPES.getHolder(NeoForgeRegistries.FLUID_TYPES.getKey(stack.getFluidType())).
                    map(fluidTypeReference -> fluidTypeReference.tags().collect(Collectors.toSet())).
                    orElse(Collections.emptySet());

            return tagKeySet.containsAll(stackTags);
        }),
        IGNORE ((iItemHandler, stack) -> true);

        private final BiPredicate<IItemHandler, FluidStack> predicate;

        TagCheck(BiPredicate<IItemHandler, FluidStack> predicate)
        {
            this.predicate = predicate;
        }

        public BiPredicate<IItemHandler, FluidStack> getPredicate()
        {
            return predicate;
        }

        public boolean test (IItemHandler handler, FluidStack stack)
        {
            return this.predicate.test(handler, stack);
        }

        @Override
        public TagCheck getValue()
        {
            return this;
        }

        @Override
        public int size()
        {
            return possibleValues().length;
        }

        @Override
        public @NotNull TagCheck[] possibleValues()
        {
            return TagCheck.values();
        }
    }
    public enum ModOwner implements FilterType<ModOwner>
    {
        USE((iItemHandler, stack) ->
        {
            ResourceLocation stackLoc = NeoForgeRegistries.FLUID_TYPES.getKey(stack.getFluidType());
            if (stackLoc == null)
                return false;
            Set<FluidStack> set = fluidSetGetter.apply(iItemHandler);
            if (set.isEmpty())
                return false;

            return set.stream().anyMatch(fluidStack ->
            {
                ResourceLocation checkLoc = NeoForgeRegistries.FLUID_TYPES.getKey(fluidStack.getFluidType());
                return checkLoc != null && checkLoc.getNamespace().equals(stackLoc.getNamespace());
            });
        }),
        IGNORE ((handler, stack) -> true);

        private final BiPredicate<IItemHandler, FluidStack> predicate;

        ModOwner (BiPredicate<IItemHandler, FluidStack> predicate)
        {
            this.predicate = predicate;
        }

        public BiPredicate<IItemHandler, FluidStack> getPredicate()
        {
            return predicate;
        }

        public boolean test(IItemHandler handler, FluidStack stack)
        {
            return this.predicate.test(handler, stack);
        }

        @Override
        public ModOwner getValue()
        {
            return this;
        }

        @Override
        public int size()
        {
            return possibleValues().length;
        }

        @Override
        public ModOwner[] possibleValues()
        {
            return ModOwner.values();
        }
    }
    public enum Target implements FilterType<Target>
    {
        NEAREST_FIRST(transmitter ->
        {
            List<BlockPos> sortedByDistance = getSortedPoses(transmitter.getAttachedPoses(), transmitter.getBlockPos(), true);
            int index = transmitter.getPrevTargetIndex();
            index = (++index) % sortedByDistance.size();
            transmitter.setPrevTargetIndex(index);
            return sortedByDistance.get(index);
        }),
        FURTHERS_FIRST(transmitter ->
        {
            List<BlockPos> sortedByDistance = getSortedPoses(transmitter.getAttachedPoses(), transmitter.getBlockPos(), false);
            int index = transmitter.getPrevTargetIndex();
            index = (++index) % sortedByDistance.size();
            transmitter.setPrevTargetIndex(index);
            return sortedByDistance.get(index);
        }),
        RANDOM (transmitter ->
        {
            List<BlockPos> attachedPoses = transmitter.getAttachedPoses();
            return attachedPoses.get(transmitter.getLevel().random.nextInt(attachedPoses.size()));
        }),
        ROUND_ROBIN(transmitter ->
        {
            int index = transmitter.getPrevTargetIndex();
            index = (++index) % transmitter.getAttachedPoses().size();
            transmitter.setPrevTargetIndex(index);
            return transmitter.getAttachedPoses().get(index);
        });

        private final Function<FluidTransmitterBlockEntity, BlockPos> function;

        Target (Function<FluidTransmitterBlockEntity, BlockPos> function)
        {
            this.function = function;
        }

        public BlockPos getNextTargetPos(FluidTransmitterBlockEntity blockEntity)
        {
            return function.apply(blockEntity);
        }

        @Override
        public Target getValue()
        {
            return this;
        }

        @Override
        public int size()
        {
            return possibleValues().length;
        }

        @Override
        public Target[] possibleValues()
        {
            return Target.values();
        }

        private static List<BlockPos> getSortedPoses(@NotNull List<BlockPos> attachedPoses, @NotNull BlockPos tilePos, boolean nearestFirst)
        {
            return attachedPoses.stream().
                    sorted(nearestFirst
                            ? Comparator.comparingDouble(tilePos::distSqr)
                            : Comparator.comparingDouble(tilePos::distSqr).reversed()).
                    collect(Collectors.toList());
        }
    }

    public static @NotNull FilterMethod copy(@NotNull FilterMethod method)
    {
        return new FilterMethod(method.list(), method.route(), method.nbt(), method.tag(), method.owner(), method.target());
    }

    public static @NotNull FilterMethod withListType (@NotNull FilterMethod method, @NotNull ListType type)
    {
        return new FilterMethod(type, method.route(), method.nbt(), method.tag(), method.owner(), method.target());
    }

    public static @NotNull FilterMethod withRoute (@NotNull FilterMethod method, @NotNull Route route)
    {
        return new FilterMethod(method.list(), route, method.nbt(), method.tag(), method.owner(), method.target());
    }

    public static @NotNull FilterMethod withNBT (@NotNull FilterMethod method, @NotNull NBT nbt)
    {
        return new FilterMethod(method.list(), method.route(), nbt, method.tag(), method.owner(), method.target());
    }

    public static @NotNull FilterMethod withTagCheck (@NotNull FilterMethod method, @NotNull TagCheck tag)
    {
        return new FilterMethod(method.list(), method.route(), method.nbt(), tag, method.owner(), method.target());
    }

    public static @NotNull FilterMethod withModOwner (@NotNull FilterMethod method, @NotNull ModOwner owner)
    {
        return new FilterMethod(method.list(), method.route(), method.nbt(), method.tag(), owner, method.target());
    }

    public static @NotNull FilterMethod withTarget (@NotNull FilterMethod method, @NotNull Target target)
    {
        return new FilterMethod(method.list(), method.route(), method.nbt(), method.tag(), method.owner(), target);
    }

    public @NotNull CompoundTag writeToNbt()
    {
        CompoundTag tag = new CompoundTag();

        tag.putInt(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.Filter.LIST_TYPE, this.list.ordinal());
        tag.putInt(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.Filter.ROUTE, this.route.ordinal());
        tag.putInt(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.Filter.NBT, this.nbt.ordinal());
        tag.putInt(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.Filter.TAG_CHECK, this.tag.ordinal());
        tag.putInt(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.Filter.MOD_OWNER, this.owner.ordinal());
        tag.putInt(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.Filter.TARGET, this.target.ordinal());

        return tag;
    }

    public static @NotNull FilterMethod readFromNbt (@NotNull CompoundTag tag)
    {
        return new FilterMethod(
                ListType.values()[tag.getInt(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.Filter.LIST_TYPE)],
                Route.values()[tag.getInt(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.Filter.ROUTE)],
                NBT.values()[tag.getInt(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.Filter.NBT)],
                TagCheck.values()[tag.getInt(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.Filter.TAG_CHECK)],
                ModOwner.values()[tag.getInt(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.Filter.MOD_OWNER)],
                Target.values()[tag.getInt(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.Filter.TARGET)]
        );
    }
}
