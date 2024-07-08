/**
 * @author ArcAnc
 * Created at: 24.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.data;

import com.arcanc.nedaire.registration.NRegistration;
import com.arcanc.nedaire.util.NDatabase;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.core.Direction.*;

public class NItemModelProvider extends ItemModelProvider
{
    public NItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, NDatabase.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels()
    {
        createBucket(NRegistration.NFluids.ENERGON_RED);
        createBucket(NRegistration.NFluids.ENERGON_BLUE);
        createBucket(NRegistration.NFluids.ENERGON_DARK);
        createBucket(NRegistration.NFluids.ENERGON_YELLOW);
        createBucket(NRegistration.NFluids.ENERGON_GREEN);

        createWrench();
    }

    private void createWrench ()
    {
        /*FIXME: rewrite model file. Those is wrong. And mark model as taken from create mod*/

        withExistingParent(itemPrefix(NRegistration.NItems.WRENCH), mcLoc("block/block")).
        renderType("solid").
        texture("main", itemPrefix(NRegistration.NItems.WRENCH)).
        element().
                from(7.7f, 0, 7.5f).
                to(8.7f, 4.5f, 8.5f).
                face(NORTH).uvs(2, 3, 5, 16).end().
                face(EAST).uvs(2,5, 5, 14).end().
                face(SOUTH).uvs(2, 3, 5, 16).end().
                face(WEST).uvs(2,5, 5, 14).end().
                face(DOWN).uvs(2, 11, 5, 14).end().
                textureAll("#main").
        end().
        element().
                from(7.5f, 4.5f, 7.5f).
                to(9, 14.5f, 8.5f).
                face(NORTH).uvs(12, 0, 16, 16).end().
                face(EAST).uvs(13, 0, 15, 12).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                face(SOUTH).uvs(16, 0, 12, 12).end().
                face(WEST).uvs(15, 0, 13, 12).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                face(UP).uvs(12, 10, 16, 14).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                face(DOWN).uvs(12, 6, 16, 10).end().
                textureAll("#main").
        end().
        element().
                from(9.10355f, 5.5f, 7.64645f).
                to(9.60355f, 10.5f, 8.14645f).
                rotation().
                    angle(-45f).
                    axis(Axis.Y).
                    origin(9.25f,11f, 8f).
                end().
                face(NORTH).uvs(0, 0, 2, 14).end().
                face(EAST).uvs(0, 2, 2, 16).end().
                face(SOUTH).uvs(0, 0, 2, 14).end().
                face(WEST).uvs(0, 2, 2, 16).end().
                face(UP).uvs(0, 0, 1, 1).end().
                face(DOWN).uvs(0, 0, 1, 1).end().
                textureAll("#main").
        end().
        element().
                from(9.00355f, 5.7f, 7.54645f).
                to(9.70355f, 6.2f, 8.24645f).
                rotation().
                    angle(-45f).
                    axis(Axis.Y).
                    origin(9.25f,10.7f, 8f).
                end().
                face(NORTH).uvs(4,1,6,3).end().
                face(EAST).uvs(6, 3, 8, 5).end().
                face(SOUTH).uvs(4, 3, 6, 5).end().
                face(WEST).uvs(6, 5, 8, 7).end().
                face(UP).uvs(6, 1, 8, 3).end().
                face(DOWN).uvs(4, 5, 6, 7).end().
                textureAll("#main").
        end().
        element().
                from(7, 14, 7).
                to(9.75f, 15.25f, 9).
                face(NORTH).uvs(12, 9, 9, 15).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                face(EAST).uvs(8, 8, 12, 10).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
                face(SOUTH).uvs(9, 8, 12, 14).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                face(WEST).uvs(8, 6, 12, 8).end().
                face(UP).uvs(9, 0, 12, 6).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                face(DOWN).uvs(9, 10, 12, 16).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                textureAll("#main").
        end().
        element().
                from(7, 12, 7).
                to(10, 13, 9).
                face(NORTH).uvs(12, 8, 8, 16).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
                face(EAST).uvs(8, 8, 12, 10).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
                face(SOUTH).uvs(8, 0, 12, 8).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                face(WEST).uvs(8, 6, 12, 8).end().
                face(UP).uvs(8, 0, 12, 8).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                face(DOWN).uvs(8, 8, 12, 16).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                textureAll("#main").
        end().
        element().
                from(10, 12.5f, 7).
                to(11.2f, 13, 9).
                face(NORTH).uvs(12, 9, 9, 15).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                face(EAST).uvs(8, 8, 12, 10).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
                face(SOUTH).uvs(9, 8, 12, 14).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                face(WEST).uvs(8, 6, 12, 8).end().
                face(UP).uvs(9, 0, 12, 6).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                face(DOWN).uvs(9, 10, 12, 16).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                textureAll("#main").
        end().
        element().
                from(9.55f, 14, 7).
                to(11.2f, 14.65f, 9).
                face(NORTH).uvs(12, 9, 9, 15).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                face(EAST).uvs(8, 8, 12, 10).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
                face(SOUTH).uvs(9, 8, 12, 14).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                face(WEST).uvs(8, 6, 12, 8).end().
                face(UP).uvs(9, 0, 12, 6).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                face(DOWN).uvs(9, 10, 12, 16).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                textureAll("#main").
        end().
        element().
                from(10.45f, 12.67f, 7.01f).
                to(11.77f, 13.17f, 8.99f).
                rotation().
                    angle(22.5f).
                    axis(Axis.Z).
                    origin(11.9f, 11.15f, 7).
                end().
                face(NORTH).uvs(12, 9, 10, 14).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
                face(EAST).uvs(8, 8, 12, 10).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
                face(SOUTH).uvs(9, 8, 12, 14).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                face(WEST).uvs(8, 6, 12, 8).end().
                face(UP).uvs(9, 0, 12, 6).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                face(DOWN).uvs(9, 10, 12, 16).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                textureAll("#main").
        end().
        element().
                from(10.35f, 14.38f, 7.01f).
                to(11.9f, 15.08f, 8.99f).
                rotation().
                    angle(-22.5f).
                    axis(Axis.Z).
                    origin(10.5f, 16.65f, 7).
                end().
                face(NORTH).uvs(12, 9, 9, 15).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                face(EAST).uvs(8, 8, 12, 10).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
                face(SOUTH).uvs(9, 8, 12, 14).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                face(WEST).uvs(8, 6, 12, 8).end().
                face(UP).uvs(9, 0, 12, 6).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                face(DOWN).uvs(9, 10, 12, 16).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                textureAll("#main").
        end().
        element().
                from(7.35f, 10.5f, 7.45f).
                to(9.9f, 11, 8.55f).
                face(NORTH).uvs(12, 9, 10, 15).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                face(EAST).uvs(8, 8, 12, 10).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
                face(SOUTH).uvs(10, 8, 12, 14).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                face(WEST).uvs(8, 6, 12, 8).end().
                face(UP).uvs(10, 0, 12, 6).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                face(DOWN).uvs(10, 10, 12, 16).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                textureAll("#main").
        end().
        element().
                from(7.35f, 5, 7.45f).
                to(9.9f, 5.5f, 8.55f).
                face(NORTH).uvs(12, 9, 10, 15).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                face(EAST).uvs(8, 8, 12, 10).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
                face(SOUTH).uvs(10, 8, 12, 14).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                face(WEST).uvs(8, 6, 12, 8).end().
                face(UP).uvs(10, 0, 12, 6).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                face(DOWN).uvs(10, 10, 12, 16).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                textureAll("#main").
        end().
        element().
                from(8.45f, 10.7f, 7.75f).
                to(9.7f, 12.5f, 8.25f).
                rotation().
                    angle(-22.5f).
                    axis(Axis.Z).
                    origin(9.225f, 11.25f, 8).
                end().
                face(NORTH).uvs(5, 3, 8, 7).end().
                face(EAST).uvs(6, 3, 8, 7).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                face(SOUTH).uvs(8, 0, 5, 4).end().
                textureAll("#main").
        end().
        transforms().
                transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).
                    translation(0, 3.25f, 0).
                    rotation(0, 90, 0).
                    end().
                transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).
                    translation(0, 3.75f, 0).
                    rotation(0, -90, 0).
                    end().
                transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).
                    translation(1,4,1).
                    rotation(-4.5f, 100.25f, 10).
                    end().
                transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).
                    translation(1, 4, 1).
                    rotation(17.25f, 267, 10).
                    end().
                transform(ItemDisplayContext.GROUND).
                    translation(0, -2.3f, 0).
                    rotation(-90, 0, 0).
                    scale(0.76914f).
                    end().
                transform(ItemDisplayContext.GUI).
                    translation(0.5f, 0, 0).
                    rotation(28, -163, 43).
                    scale(1.09453f).
                    end().
                transform(ItemDisplayContext.FIXED).
                    translation(0.5f, 0.5f, 0).
                    rotation(0, 160.5f, 0).
                    end().
        end();
    }

    private void createBucket(NRegistration.NFluids.@NotNull FluidEntry entry)
    {
        withExistingParent(itemPrefix(entry.bucket()), neoLoc(itemPrefix("bucket_drip"))).
                customLoader(DynamicFluidContainerModelBuilder :: begin).
                fluid(entry.still().get()).
                applyFluidLuminosity(true).
                applyTint(true);
    }

    private @NotNull ResourceLocation neoLoc(String name)
    {
        return ResourceLocation.fromNamespaceAndPath("neoforge", name);
    }

    private @NotNull String itemPrefix(@NotNull DeferredHolder<Item, ? extends Item> itemHolder)
    {
        return itemPrefix(BuiltInRegistries.ITEM.getKey(itemHolder.get()).getPath());
    }
    private @NotNull String itemPrefix(String name)
    {
        return ModelProvider.ITEM_FOLDER + "/" + name;
    }
}
