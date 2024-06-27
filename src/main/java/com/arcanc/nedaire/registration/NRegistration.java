/**
 * @author ArcAnc
 * Created at: 10.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Ancient's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.registration;

import com.arcanc.nedaire.content.block.NBlockBase;
import com.arcanc.nedaire.content.block.NodeBlock;
import com.arcanc.nedaire.content.block.block_entity.NodeBlockEntity;
import com.arcanc.nedaire.content.capabilities.energon.EnergonType;
import com.arcanc.nedaire.content.fluid.NEnergonFluidType;
import com.arcanc.nedaire.content.fluid.NFluid;
import com.arcanc.nedaire.content.fluid.NFluidType;
import com.arcanc.nedaire.content.gui.container_menu.NContainerMenu;
import com.arcanc.nedaire.content.items.ItemInterfaces;
import com.arcanc.nedaire.content.items.NBaseBlockItem;
import com.arcanc.nedaire.content.items.NBucketItem;
import com.arcanc.nedaire.util.NDatabase;
import com.arcanc.nedaire.util.helpers.BlockHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class NRegistration
{

    public static void init(final IEventBus modEventBus)
    {
        NRegistration.NEnergonTypes.init(modEventBus);
        NRegistration.NBlocks.init(modEventBus);
        NRegistration.NItems.init(modEventBus);
        NRegistration.NBlockEntities.init(modEventBus);
        NRegistration.NFluids.init(modEventBus);
        NRegistration.NMenuTypes.init(modEventBus);
        NRegistration.NCreativeTabs.init(modEventBus);
        //custom registries
        NRegistration.NMultiblocks.init(modEventBus);
    }

    public static final class NEnergonTypes
    {
        public static final ResourceKey<Registry<EnergonType>> REGISTRY_ENERGON_TYPE = ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace(NDatabase.CapabilitiesInfo.EnergonInfo.EnergonTypeInfo.TAG_LOCATION));

        public static final DeferredRegister<EnergonType> ENERGON_TYPES = DeferredRegister.create(REGISTRY_ENERGON_TYPE, NDatabase.MOD_ID);

        public static final Registry<EnergonType> ENERGON_TYPES_BUILTIN = ENERGON_TYPES.makeRegistry(registryBuilder -> makeRegistry(registryBuilder, REGISTRY_ENERGON_TYPE));

        public static final DeferredHolder<EnergonType, EnergonType> RED = registerType(NDatabase.CapabilitiesInfo.EnergonInfo.EnergonTypeInfo.RED, new Color(255, 0, 0, 255));
        public static final DeferredHolder<EnergonType, EnergonType> DARK = registerType(NDatabase.CapabilitiesInfo.EnergonInfo.EnergonTypeInfo.DARK, new Color(160, 64, 213, 255));
        public static final DeferredHolder<EnergonType, EnergonType> GREEN = registerType(NDatabase.CapabilitiesInfo.EnergonInfo.EnergonTypeInfo.GREEN, new Color(0, 255, 0, 255));
        public static final DeferredHolder<EnergonType, EnergonType> YELLOW = registerType(NDatabase.CapabilitiesInfo.EnergonInfo.EnergonTypeInfo.YELLOW, new Color(255, 255, 0, 255));
        public static final DeferredHolder<EnergonType, EnergonType> BLUE = registerType(NDatabase.CapabilitiesInfo.EnergonInfo.EnergonTypeInfo.BLUE, new Color(0, 255, 255, 255));

        private static @NotNull DeferredHolder<EnergonType, EnergonType> registerType(String name, Color color)
        {
            final EnergonType type = new EnergonType(color);
            return ENERGON_TYPES.register(name, () -> type);
        }

        public static void init(final @NotNull IEventBus modEventBus)
        {
            ENERGON_TYPES.register(modEventBus);
        }
    }

    public static final class NBlocks
    {
        public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(NDatabase.MOD_ID);

        public static final BlockRegObject<NodeBlock, NBaseBlockItem> NODE_BLOCK = new BlockRegObject<>(
                "node_block",
                () -> BlockBehaviour.Properties.of().noOcclusion().noCollission().noLootTable(),
                NodeBlock :: new,
                Item.Properties::new,
                NBaseBlockItem :: new);

        public static class BlockRegObject<T extends Block, I extends Item> implements Supplier<T>, ItemLike
        {
            private final DeferredHolder<Block, T> regObject;
            private final Supplier<Block.Properties> blockProps;
            private final DeferredHolder<Item, I> itemBlock;
            private final Supplier<Item.Properties> itemProps;

            public static @NotNull BlockRegObject<NBlockBase, NBaseBlockItem> simple (String name, Supplier<Block.Properties> props)
            {
                return simple(name, props, p -> {});
            }

            public static @NotNull BlockRegObject<NBlockBase, NBaseBlockItem> simple (String name, Supplier<Block.Properties> props, Consumer<NBlockBase> extra)
            {
                return new BlockRegObject<>(name, props, p -> Util.make(new NBlockBase(p), extra), NRegistration.NItems.baseProps, NBaseBlockItem::new);
            }

            public BlockRegObject(String name, Supplier<Block.Properties> blockProps, Function<Block.Properties, T> makeBlock, Supplier<Item.Properties> itemProps, BiFunction<T, Item.Properties, I> makeItem)
            {
                this.blockProps = blockProps;
                this.regObject = BLOCKS.register(name, () -> makeBlock.apply(blockProps.get()));
                this.itemProps = itemProps;
                this.itemBlock = NRegistration.NItems.ITEMS.register(name, () -> makeItem.apply(regObject.get(), itemProps.get()));
            }

            public BlockRegObject (T existing)
            {
                this.blockProps = () -> Block.Properties.ofFullCopy(existing);
                this.regObject = DeferredHolder.create(Registries.BLOCK, BuiltInRegistries.BLOCK.getKey(existing));
                this.itemBlock = DeferredHolder.create(Registries.ITEM, BuiltInRegistries.ITEM.getKey(existing.asItem()));
                this.itemProps = NRegistration.NItems.copyProps(itemBlock.get());
            }

            @Override
            public @Nonnull Item asItem()
            {
                return itemBlock.get();
            }

            @Override
            public T get()
            {
                return regObject.get();
            }

            public Supplier<Block.Properties> getBlockProperties()
            {
                return blockProps;
            }

            public Supplier<Item.Properties> getItemProperties()
            {
                return itemProps;
            }

            public ResourceLocation getId()
            {
                return regObject.getId();
            }

            public BlockState getDefaultBlockState()
            {
                return get().defaultBlockState();
            }

        }

        public static void init(IEventBus modEventBus)
        {
            BLOCKS.register(modEventBus);
        }
    }

    public static final class NItems
    {
        public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NDatabase.MOD_ID);

        protected static final Supplier<Item.Properties> baseProps = Item.Properties::new;

        @SuppressWarnings("deprecation")
        public static @Nonnull Supplier<Item.Properties> copyProps(@Nonnull Item itemBlock)
        {
            ItemStack stack = itemBlock.getDefaultInstance();
            Item.Properties p = new Item.Properties().
                    food(stack.getFoodProperties(null)).
                    stacksTo(stack.getMaxStackSize()).
                    durability(stack.getMaxDamage()).
                    craftRemainder(itemBlock.getCraftingRemainingItem()).
                    rarity(stack.getRarity());
            if (stack.get(DataComponents.JUKEBOX_PLAYABLE) != null)
                p.jukeboxPlayable(stack.get(DataComponents.JUKEBOX_PLAYABLE).song().key());
            if (stack.get(DataComponents.FIRE_RESISTANT) != null)
                p.fireResistant();
            if(!stack.isRepairable())
                p.setNoRepair();
            if (stack.get(DataComponents.ATTRIBUTE_MODIFIERS) != null)
                p.attributes(stack.get(DataComponents.ATTRIBUTE_MODIFIERS));
            return () -> p;
        }

        public static void init(final IEventBus modEventBus)
        {
            ITEMS.register(modEventBus);
        }
    }

    public static final class NBlockEntities
    {
        public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(
                BuiltInRegistries.BLOCK_ENTITY_TYPE, NDatabase.MOD_ID);

        public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NodeBlockEntity>> BE_NODE = BLOCK_ENTITIES.register(
                "node", makeType(NodeBlockEntity :: new, NBlocks.NODE_BLOCK));

        public static <T extends BlockEntity> Supplier<BlockEntityType<T>> makeType(BlockEntityType.BlockEntitySupplier<T> create, Supplier<? extends Block> valid)
        {
            return makeTypeMultipleBlocks(create, ImmutableSet.of(valid));
        }

        public static <T extends BlockEntity> Supplier<BlockEntityType<T>> makeTypeMultipleBlocks(
                BlockEntityType.BlockEntitySupplier<T> create, Collection<? extends Supplier<? extends Block>> valid
        )
        {
            return () -> new BlockEntityType<>(
                    create, ImmutableSet.copyOf(valid.stream().map(Supplier::get).collect(Collectors.toList())), null
            );
        }

        public static void init (final IEventBus modEventBus)
        {
            BLOCK_ENTITIES.register(modEventBus);
        }
    }

    public static final class NFluids
    {
        public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, NDatabase.MOD_ID);
        public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, NDatabase.MOD_ID);

        private static final Consumer<FluidType.Properties> baseEnergonTypeProps = typeProps -> typeProps.
                lightLevel(10).
                density(250).
                viscosity(500).
                rarity(Rarity.COMMON);

        private static final Consumer<BaseFlowingFluid.Properties> baseEnergonFluidProps = props -> props.slopeFindDistance(2).levelDecreasePerBlock(2).explosionResistance(100);

        public static final FluidEntry ENERGON_RED = FluidEntry.makeEnergon(
                NDatabase.CapabilitiesInfo.EnergonInfo.EnergonTypeInfo.RED,
                NEnergonTypes.RED,
                baseEnergonFluidProps,
                typeProps -> baseEnergonTypeProps.accept(typeProps.rarity(Rarity.EPIC))
        );

        public static final FluidEntry ENERGON_BLUE = FluidEntry.makeEnergon(
                NDatabase.CapabilitiesInfo.EnergonInfo.EnergonTypeInfo.BLUE,
                NEnergonTypes.BLUE,
                baseEnergonFluidProps,
                baseEnergonTypeProps
        );

        public static final FluidEntry ENERGON_GREEN = FluidEntry.makeEnergon(
                NDatabase.CapabilitiesInfo.EnergonInfo.EnergonTypeInfo.GREEN,
                NEnergonTypes.GREEN,
                baseEnergonFluidProps,
                typeProps -> baseEnergonTypeProps.accept(typeProps.rarity(Rarity.EPIC))
        );

        public static final FluidEntry ENERGON_DARK = FluidEntry.makeEnergon(
                NDatabase.CapabilitiesInfo.EnergonInfo.EnergonTypeInfo.DARK,
                NEnergonTypes.DARK,
                baseEnergonFluidProps,
                typeProps -> baseEnergonTypeProps.accept(typeProps.rarity(Rarity.EPIC))
        );

        public static final FluidEntry ENERGON_YELLOW = FluidEntry.makeEnergon(
                NDatabase.CapabilitiesInfo.EnergonInfo.EnergonTypeInfo.YELLOW,
                NEnergonTypes.YELLOW,
                baseEnergonFluidProps,
                typeProps -> baseEnergonTypeProps.accept(typeProps.rarity(Rarity.EPIC))
        );

        public record FluidEntry(DeferredHolder<Fluid, NFluid> still,
                                 DeferredHolder<Fluid, NFluid> flowing,
                                 DeferredHolder<Block, LiquidBlock> block,
                                 DeferredHolder<Item, BucketItem> bucket,
                                 DeferredHolder<FluidType, FluidType> type,
                                 List<Property<?>> props)
        {

            private static @NotNull FluidEntry makeEnergon(String name, DeferredHolder<EnergonType, EnergonType> energonType, Consumer<BaseFlowingFluid.Properties> props, @Nullable Consumer<FluidType.Properties> buildAttributes)
            {
                FluidType.Properties builder = FluidType.Properties.create()
                        .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                        .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY);
                if(buildAttributes!=null)
                    buildAttributes.accept(builder);
                DeferredHolder<FluidType, FluidType> type = FLUID_TYPES.register(
                        name, () -> makeEnergonTypeWithTextures(
                                builder,
                                NDatabase.FluidsInfo.getStillLoc("energon"),
                                NDatabase.FluidsInfo.getFlowLoc("energon"),
                                NDatabase.FluidsInfo.getOverlayLoc("energon"),
                                energonType.get())
                );
                Mutable<FluidEntry> thisMutable = new MutableObject<>();
                NFluid.FluidPropsGetter fluidProps = BaseFlowingFluid.Properties::new;
                DeferredHolder<Fluid, NFluid> still = FLUIDS.register(NDatabase.FluidsInfo.getStillLoc(name).getPath(), () -> NFluid.makeFluid(NFluid.NFluidSource :: new,
                        fluidProps.get(
                                        thisMutable.getValue().type(),
                                        thisMutable.getValue().still(),
                                        thisMutable.getValue().flowing()).
                                block(thisMutable.getValue().block()).
                                bucket(thisMutable.getValue().bucket()),
                        props));
                DeferredHolder<Fluid, NFluid> flowing = FLUIDS.register(NDatabase.FluidsInfo.getFlowLoc(name).getPath(), () -> NFluid.makeFluid(NFluid.NFluidFlowing :: new,
                        fluidProps.get(
                                        thisMutable.getValue().type(),
                                        thisMutable.getValue().still(),
                                        thisMutable.getValue().flowing()).
                                block(thisMutable.getValue().block()).
                                bucket(thisMutable.getValue().bucket()),
                        props));
                DeferredHolder<Block, LiquidBlock> block = NBlocks.BLOCKS.register(NDatabase.FluidsInfo.getBlockLocation(name).getPath(),
                        () -> new LiquidBlock(thisMutable.getValue().still().get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
                DeferredHolder<Item, BucketItem> bucket = NItems.ITEMS.register(NDatabase.FluidsInfo.getBucketLocation(name).getPath(), () -> makeBucket(still, 0));
                FluidEntry entry = new FluidEntry(still, flowing, block, bucket, type, List.of());
                thisMutable.setValue(entry);
                return entry;
            }
            private static @NotNull FluidEntry make(String name, NFluidType.FogGetter fogColor, Consumer<BaseFlowingFluid.Properties> props)
            {
                return make(name, () -> 0xffffffff, fogColor, props);
            }

            private static @NotNull FluidEntry make(String name, Supplier<Integer> tintColor, NFluidType.FogGetter fogColor, Consumer<BaseFlowingFluid.Properties> props)
            {
                return make(name, 0, NDatabase.FluidsInfo.getStillLoc(name), NDatabase.FluidsInfo.getFlowLoc(name), NDatabase.FluidsInfo.getOverlayLoc(name), tintColor, fogColor, props);
            }

            private static @NotNull FluidEntry make(String name, int burnTime, Supplier<Integer> tintColor, NFluidType.FogGetter fogColor, Consumer<BaseFlowingFluid.Properties> props)
            {
                return make(name, burnTime, NDatabase.FluidsInfo.getStillLoc(name), NDatabase.FluidsInfo.getFlowLoc(name), NDatabase.FluidsInfo.getOverlayLoc(name), tintColor, fogColor, props);
            }

            private static @NotNull FluidEntry make(String name, ResourceLocation stillTex, ResourceLocation flowingTex, ResourceLocation overlayTex, Supplier<Integer> tintColor, NFluidType.FogGetter fogColor, Consumer<BaseFlowingFluid.Properties> props)
            {
                return make(name, 0, stillTex, flowingTex, overlayTex, tintColor, fogColor, props);
            }

            private static @NotNull FluidEntry make(
                    String name, Supplier<Integer> tintColor, NFluidType.FogGetter fogColor, Consumer<BaseFlowingFluid.Properties> props, Consumer<FluidType.Properties> buildAttributes
            )
            {
                return make(name, 0, NDatabase.FluidsInfo.getStillLoc(name), NDatabase.FluidsInfo.getFlowLoc(name), NDatabase.FluidsInfo.getOverlayLoc(name), tintColor, fogColor, props, buildAttributes);
            }

            private static @NotNull FluidEntry make(String name, int burnTime, ResourceLocation stillTex, ResourceLocation flowingTex, ResourceLocation overlayTex, Supplier<Integer> tintColor, NFluidType.FogGetter fogColor, Consumer<BaseFlowingFluid.Properties> props)
            {
                return make(name, burnTime, stillTex, flowingTex, overlayTex, tintColor, fogColor, props, null);
            }

            private static @NotNull FluidEntry make(
                    String name, int burnTime,
                    ResourceLocation stillTex, ResourceLocation flowingTex, ResourceLocation overlayTex, Supplier<Integer> tintColor, NFluidType.FogGetter fogColor, @Nullable Consumer<BaseFlowingFluid.Properties> props,
                    @Nullable Consumer<FluidType.Properties> buildAttributes
            )
            {
                return make(
                        name, burnTime, stillTex, flowingTex, overlayTex, tintColor, fogColor, NFluid.NFluidSource :: new, NFluid.NFluidFlowing :: new, props, buildAttributes,
                        ImmutableList.of()
                );
            }

            private static @NotNull FluidEntry make(
                    String name, int burnTime,
                    ResourceLocation stillTex, ResourceLocation flowingTex, ResourceLocation overlayTex, Supplier<Integer> tintColor, NFluidType.FogGetter fogColor,
                    Function<BaseFlowingFluid.Properties, ? extends NFluid> makeStill, Function<BaseFlowingFluid.Properties, ? extends NFluid> makeFlowing, @Nullable Consumer<BaseFlowingFluid.Properties> props,
                    @Nullable Consumer<FluidType.Properties> buildAttributes, List<Property<?>> properties)
            {
                FluidType.Properties builder = FluidType.Properties.create().
                        sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).
                        sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY);
                if(buildAttributes!=null)
                    buildAttributes.accept(builder);
                DeferredHolder<FluidType, FluidType> type = FLUID_TYPES.register(
                        name, () -> makeTypeWithTextures(builder, stillTex, flowingTex, overlayTex, tintColor, fogColor)
                );
                Mutable<FluidEntry> thisMutable = new MutableObject<>();
                NFluid.FluidPropsGetter fluidProps = BaseFlowingFluid.Properties::new;
                DeferredHolder<Fluid, NFluid> still = FLUIDS.register(NDatabase.FluidsInfo.getStillLoc(name).getPath(), () -> NFluid.makeFluid(makeStill,
                        fluidProps.get(
                                        thisMutable.getValue().type(),
                                        thisMutable.getValue().still(),
                                        thisMutable.getValue().flowing()).
                                block(thisMutable.getValue().block()).
                                bucket(thisMutable.getValue().bucket()),
                        props));
                DeferredHolder<Fluid, NFluid> flowing = FLUIDS.register(NDatabase.FluidsInfo.getFlowLoc(name).getPath(), () -> NFluid.makeFluid(makeFlowing,
                        fluidProps.get(
                                        thisMutable.getValue().type(),
                                        thisMutable.getValue().still(),
                                        thisMutable.getValue().flowing()).
                                block(thisMutable.getValue().block()).
                                bucket(thisMutable.getValue().bucket()),
                        props));
                DeferredHolder<Block, LiquidBlock> block = NBlocks.BLOCKS.register(NDatabase.FluidsInfo.getBlockLocation(name).getPath(),
                        () -> new LiquidBlock(thisMutable.getValue().still().get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
                DeferredHolder<Item, BucketItem> bucket = NItems.ITEMS.register(NDatabase.FluidsInfo.getBucketLocation(name).getPath(), () -> makeBucket(still, burnTime));
                FluidEntry entry = new FluidEntry(still, flowing, block, bucket, type, properties);
                thisMutable.setValue(entry);
                return entry;
            }

            private static NEnergonFluidType makeEnergonTypeWithTextures(FluidType.Properties props, ResourceLocation stillTex, ResourceLocation flowingTex, ResourceLocation overlayTex, EnergonType type)
            {
                return new NEnergonFluidType(stillTex, flowingTex, overlayTex, type, props)
                {
                    @Override
                    public @NotNull String getDescriptionId()
                    {
                        ResourceLocation loc = NeoForgeRegistries.FLUID_TYPES.getKey(this);
                        if (loc != null)
                            return loc.getNamespace() + "." + "fluid_type" + "." + loc.getPath().replace('/', '.');
                        return "unregistered.fluid_type";
                    }
                };
            }

            private static FluidType makeTypeWithTextures(FluidType.Properties props, ResourceLocation stillTex, ResourceLocation flowingTex, ResourceLocation overlayTex, Supplier<Integer> tintColor, NFluidType.FogGetter fogColor)
            {
                return new NFluidType(stillTex, flowingTex, overlayTex, tintColor, fogColor, props)
                {
                    @Override
                    public @NotNull String getDescriptionId()
                    {
                        ResourceLocation loc = NeoForgeRegistries.FLUID_TYPES.getKey(this);
                        if (loc != null)
                            return loc.getNamespace() + "." + "fluid_type" + "." + loc.getPath().replace('/', '.');
                        return "unregistered.fluid_type";
                    }
                };
            }

            private static @NotNull BucketItem makeBucket (@NotNull DeferredHolder<Fluid, NFluid> still, int burnTime)
            {
                return new NBucketItem(still.get(), new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET))
                {
                    @Override
                    public int getBurnTime(@NotNull ItemStack itemStack, @Nullable RecipeType<?> recipeType)
                    {
                        return burnTime;
                    }
                };
            }

        }


        public static void init(@NotNull IEventBus modEventBus)
        {
            FLUIDS.register(modEventBus);
            FLUID_TYPES.register(modEventBus);
        }
    }

    public static final class NMenuTypes
    {
        public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, NDatabase.MOD_ID);


        public static class ArgContainer<T, C extends NContainerMenu>
        {
            private final DeferredHolder<MenuType<?>, MenuType<C>> type;
            private final ArgContainerConstructor<T, C> factory;

            private ArgContainer(DeferredHolder<MenuType<?>, MenuType<C>> type, ArgContainerConstructor<T, C> factory)
            {
                this.type = type;
                this.factory = factory;
            }

            public C create(int windowId, Inventory playerInv, T tile)
            {
                return factory.construct(getType(), windowId, playerInv, tile);
            }

            public MenuProvider provide(T arg)
            {
                return new MenuProvider()
                {
                    @Nonnull
                    @Override
                    public Component getDisplayName()
                    {
                        return Component.empty();
                    }

                    @Nullable
                    @Override
                    public AbstractContainerMenu createMenu(
                            int containerId, @Nonnull Inventory inventory, @Nonnull Player player
                    )
                    {
                        return create(containerId, inventory, arg);
                    }
                };
            }

            public MenuType<C> getType()
            {
                return type.get();
            }
        }

        public interface ArgContainerConstructor<T, C extends NContainerMenu>
        {
            C construct(MenuType<C> type, int windowId, Inventory inventoryPlayer, T te);
        }

        public static void init(final IEventBus modEventBus)
        {
            MENU_TYPES.register(modEventBus);
        }
    }

    public static final class NCreativeTabs
    {
        public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NDatabase.MOD_ID);

        public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_MODE_TABS.register(NDatabase.MOD_ID, () ->
                CreativeModeTab.builder().
                /*FIXME: add normal item as icon*/
                icon(() -> new ItemStack(Items.DIAMOND)).
                title(Component.literal(NDatabase.CreativeTabsInfo.NAME)).
                displayItems(NCreativeTabs :: fill).
                build());

        private static void fill(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output)
        {
            for(DeferredHolder<Item, ? extends Item> holder : NItems.ITEMS.getEntries()){
                Item item = holder.value();

                if(item instanceof ItemInterfaces.IMustAddToCreativeTab i && i.addSelfToCreativeTab()){
                    output.accept(item);
                }
            }
        }

        public static void init(final IEventBus modEventBus)
        {
            CREATIVE_MODE_TABS.register(modEventBus);
        }
    }

    public static final class NMultiblocks
    {
        public static void init(final IEventBus bus)
        {
            Preprocessors.register();
            Matchers.register();
        }
        public static final class Matchers
        {
            public static void register()
            {
                BlockHelper.BlockMatcher.addPredicate((expected, found, world, pos) -> expected == found);

                List<TagKey<Block>> genericTags = new ArrayList<>();
                /*FIXME: fill tags list, which may be used for checks*/
                BlockHelper.BlockMatcher.addPredicate((expected, found, world, pos) -> {
                    if(expected.getBlock()!=found.getBlock())
                        for(TagKey<Block> t : genericTags)
                            if(expected.is(t)&&found.is(t))
                                return true;
                    return false;
                });
            }
        }

        public static final class Preprocessors
        {
            public static void register()
            {
                //FourWayBlock (fences etc): ignore all connections
                List<Property<Boolean>> sideProperties = ImmutableList.of(
                        CrossCollisionBlock.NORTH, CrossCollisionBlock.EAST, CrossCollisionBlock.SOUTH, CrossCollisionBlock.WEST
                );
                BlockHelper.BlockMatcher.addPreprocessor((expected, found, world, pos) -> {
                    if(expected.getBlock() instanceof CrossCollisionBlock &&expected.getBlock()==found.getBlock())
                        for(Property<Boolean> side : sideProperties)
                            if(!expected.getValue(side))
                                found = found.setValue(side, false);
                    return found;
                });

                //Ignore hopper facing
                BlockHelper.BlockMatcher.addPreprocessor((expected, found, world, pos) -> {
                    if(expected.getBlock()== Blocks.HOPPER&&found.getBlock()==Blocks.HOPPER)
                        return found.setValue(HopperBlock.FACING, expected.getValue(HopperBlock.FACING));
                    return found;
                });

                //Allow multiblocks to be formed underwater
                BlockHelper.BlockMatcher.addPreprocessor((expected, found, world, pos) -> {
                    // Un-waterlog if the expected state is dry, but the found one is not
                    if(expected.hasProperty(BlockHelper.BlockProperties.WATERLOGGED)&&found.hasProperty(BlockHelper.BlockProperties.WATERLOGGED)
                            &&!expected.getValue(BlockHelper.BlockProperties.WATERLOGGED)&&found.getValue(BlockHelper.BlockProperties.WATERLOGGED))
                        return found.setValue(BlockHelper.BlockProperties.WATERLOGGED, false);
                    else
                        return found;
                });
            }
        }
    }



    public static <T> @NotNull RegistryBuilder<T> makeRegistry(@NotNull RegistryBuilder<T> registryBuilder, @NotNull ResourceKey<? extends Registry<T>> key)
    {
        return registryBuilder.defaultKey(key.location()).maxId(Integer.MAX_VALUE - 1).sync(true);
    }
}
