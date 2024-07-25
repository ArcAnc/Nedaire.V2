/**
 * @author ArcAnc
 * Created at: 11.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.util.filter;

import com.arcanc.nedaire.util.NDatabase;
import com.arcanc.nedaire.util.helpers.FluidHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

public record FilterMethod(ListType list, Route route, NBT nbt, TagCheck tag, ModOwner owner, Target target)
{
    public static final FilterMethod DEFAULT = new FilterMethod(ListType.DENY, Route.OUTPUT, NBT.IGNORE_NBT, TagCheck.IGNORE, ModOwner.IGNORE, Target.ROUND_ROBIN);

    private static final Function<IItemHandler, Set<FluidStack>> fluidSetGetter = iItemHandler ->
    {
        Set<FluidStack> set = new HashSet<>();
        for (int q = 0; q < iItemHandler.getSlots(); q++)
        {
            ItemStack itemStack = iItemHandler.getStackInSlot(q);
            FluidHelper.getFluidHandler(itemStack).
                    ifPresent(fluidHandler ->
                    {
                        for (int z = 0; z < fluidHandler.getTanks(); z++)
                        {
                            FluidStack fluid = fluidHandler.getFluidInTank(z);
                            if (!fluid.isEmpty())
                                set.add(fluid);
                        }
                    });
        }
        return set;
    };

    public enum ListType
    {
        ALLOW((iItemHandler, stack) ->
        {
            Set<FluidStack> set = fluidSetGetter.apply(iItemHandler);
            if (set.isEmpty())
                return false;
            for (FluidStack fluidStack : set)
                if (FluidStack.isSameFluid(fluidStack, stack))
                    return true;
            return false;
        }),
        DENY((iItemHandler, stack) ->
        {
            Set<FluidStack> set = fluidSetGetter.apply(iItemHandler);
            if (set.isEmpty())
                return true;
            for (FluidStack fluidStack : set)
                if (FluidStack.isSameFluid(fluidStack, stack))
                    return false;
            return true;
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
    }
    public enum Route
    {
        INPUT, OUTPUT, BIDIRECTION;
    }
    public enum NBT
    {
        CHECK_NBT (((iItemHandler, stack) ->
        {
            Set<FluidStack> set = fluidSetGetter.apply(iItemHandler);

            if (set.isEmpty())
                return true;
            for (FluidStack fluidStack : set)
            {
                if (FluidStack.isSameFluidSameComponents(fluidStack, stack))
                    return true;
            }
            return false;
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
    }
    public enum TagCheck
    {
        USE ((iItemHandler, stack) ->
        {
            Set<FluidStack> set = fluidSetGetter.apply(iItemHandler);
            if (set.isEmpty())
                return true;
            Set<TagKey<FluidType>> tagKeySet = new HashSet<>();
            for (FluidStack fluidStack : set)
            {
                NeoForgeRegistries.FLUID_TYPES.getHolder(NeoForgeRegistries.FLUID_TYPES.getKey(fluidStack.getFluidType())).
                        ifPresent(fluidTypeReference -> tagKeySet.addAll(fluidTypeReference.tags().collect(Collectors.toSet())));
            }
            Set<TagKey<FluidType>> stackTags = new HashSet<>();
            NeoForgeRegistries.FLUID_TYPES.getHolder(NeoForgeRegistries.FLUID_TYPES.getKey(stack.getFluidType())).
                    ifPresent(fluidTypeReference -> stackTags.addAll(fluidTypeReference.tags().collect(Collectors.toSet())));

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
    }
    public enum ModOwner
    {
        USE((iItemHandler, stack) ->
        {
            ResourceLocation stackLoc = NeoForgeRegistries.FLUID_TYPES.getKey(stack.getFluidType());
            if (stackLoc == null)
                return false;
            Set<FluidStack> set = fluidSetGetter.apply(iItemHandler);
            if (set.isEmpty())
                return false;
            for (FluidStack fluidStack : set)
            {
                ResourceLocation checkLoc = NeoForgeRegistries.FLUID_TYPES.getKey(fluidStack.getFluidType());
                if (checkLoc != null && checkLoc.getNamespace().equals(stackLoc.getNamespace()))
                    return true;
            }
            return false;
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
    }
    public enum Target
    {
        NEAREST_FIRST, FURTHERS_FIRST, RANDOM, ROUND_ROBIN;
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
