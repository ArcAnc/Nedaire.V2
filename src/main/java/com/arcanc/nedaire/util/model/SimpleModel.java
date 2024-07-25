/**
 * @author ArcAnc
 * Created at: 09.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.util.model;

import com.arcanc.nedaire.util.NDatabase;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.IModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.SimpleUnbakedGeometry;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class SimpleModel extends SimpleUnbakedGeometry<SimpleModel>
{

    private final ElementsModelWrapped model;
    private final SimpleModel.IFactory<BakedModel> factory;

    public SimpleModel(ElementsModelWrapped model, SimpleModel.IFactory<BakedModel> factory)
    {

        this.model = model;
        this.factory = factory;
    }

    @Override
    public @NotNull BakedModel bake(@NotNull IGeometryBakingContext owner, @NotNull ModelBaker bakery, @NotNull Function<Material, TextureAtlasSprite> spriteGetter, @NotNull ModelState modelTransform, @NotNull ItemOverrides overrides)
    {
        return factory.create(model.bake(owner, bakery, spriteGetter, modelTransform, overrides));
    }

    @Override
    public void addQuads(@NotNull IGeometryBakingContext owner, @NotNull IModelBuilder<?> modelBuilder, @NotNull ModelBaker bakery, @NotNull Function<Material, TextureAtlasSprite> spriteGetter, @NotNull ModelState modelTransform)
    {
        model.addQuads(owner, modelBuilder, bakery, spriteGetter, modelTransform);
    }

    public interface IFactory<T extends BakedModel>
    {
        T create(BakedModel originalModel);
    }

    public static class GeometryLoader implements IGeometryLoader<SimpleModel>
    {

        public static final ResourceLocation ID = NDatabase.modRL("fluid_storage");

        private final SimpleModel.IFactory<BakedModel> factory;

        public GeometryLoader(SimpleModel.IFactory<BakedModel> factory)
        {
            this.factory = factory;
        }

        @Override
        public @NotNull SimpleModel read(@NotNull JsonObject jsonObject, @NotNull JsonDeserializationContext deserializationContext)
        {
            return new SimpleModel(ElementsModelWrapped.Loader.INSTANCE.read(jsonObject, deserializationContext), factory);
        }
    }

    public static class ModelLoader extends CustomLoaderBuilder<BlockModelBuilder>
    {
        public ModelLoader(BlockModelBuilder parent, ExistingFileHelper existingFileHelper)
        {
            super(GeometryLoader.ID, parent, existingFileHelper, true);
        }
    }
}
