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
import com.arcanc.nedaire.content.items.ItemInterfaces;
import com.arcanc.nedaire.content.items.NBaseBlockItem;
import com.arcanc.nedaire.util.NDatabase;
import com.arcanc.nedaire.util.helpers.BlockHelper;
import com.google.common.collect.ImmutableList;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class NRegistration
{
    public static void init(final IEventBus modEventBus)
    {
        NRegistration.NBlocks.init(modEventBus);
        NRegistration.NItems.init(modEventBus);
        NRegistration.NCreativeTabs.init(modEventBus);
        //custom registries
        NRegistration.NMultiblocks.init(modEventBus);
    }

    public static class NBlocks
    {
        public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(NDatabase.MOD_ID);

        public static class BlockRegObject<T extends Block, I extends Item> implements Supplier<T>, ItemLike
        {
            private final DeferredHolder<Block, T> regObject;
            private final Supplier<Block.Properties> blockProps;
            private final DeferredHolder<Item, I> itemBlock;
            private final Supplier<Item.Properties> itemProps;

            public static BlockRegObject<NBlockBase, NBaseBlockItem> simple (String name, Supplier<Block.Properties> props)
            {
                return simple(name, props, p -> {});
            }

            public static BlockRegObject<NBlockBase, NBaseBlockItem> simple (String name, Supplier<Block.Properties> props, Consumer<NBlockBase> extra)
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

    public static class NItems
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
}
