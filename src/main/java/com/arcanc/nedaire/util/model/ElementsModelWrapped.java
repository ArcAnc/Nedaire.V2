/**
 * @author ArcAnc
 * Created at: 09.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.util.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.client.model.IModelBuilder;
import net.neoforged.neoforge.client.model.SimpleModelState;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.SimpleUnbakedGeometry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ElementsModelWrapped extends SimpleUnbakedGeometry<ElementsModelWrapped>
{

    private final List<BlockElement> elements;

    private ElementsModelWrapped(List<BlockElement> elements)
    {
        this.elements = elements;
    }

    @Override
    public void addQuads(@NotNull IGeometryBakingContext context,
                         @NotNull IModelBuilder<?> modelBuilder,
                         @NotNull ModelBaker bakery,
                         @NotNull Function<Material, TextureAtlasSprite> spriteGetter,
                         @NotNull ModelState modelState)
    {

        Transformation rootTransform = context.getRootTransform();
        if (!rootTransform.isIdentity())
            modelState = new SimpleModelState(modelState.getRotation().compose(rootTransform), modelState.isUvLocked());

        for (BlockElement element : elements)
        {
            for (Direction direction : element.faces.keySet())
            {
                var face = element.faces.get(direction);
                var sprite = spriteGetter.apply(context.getMaterial(face.texture()));
                var quad = BlockModel.bakeFace(element, face, sprite, direction, modelState);

                if (face.cullForDirection() == null)
                    modelBuilder.addUnculledFace(quad);
                else
                    modelBuilder.addCulledFace(modelState.getRotation().rotateTransform(face.cullForDirection()), quad);
            }
        }
    }

    public static final class Loader implements IGeometryLoader<ElementsModelWrapped>
    {
        public static final Loader INSTANCE = new Loader();

        private Loader()
        {

        }

        @Override
        public @NotNull ElementsModelWrapped read(@NotNull JsonObject jsonObject, @NotNull JsonDeserializationContext deserializationContext) throws JsonParseException
        {

            if (!jsonObject.has("elements"))
            {
                throw new JsonParseException("An element model must have an \"elements\" member.");
            }
            List<BlockElement> elements = new ArrayList<>();
            for (JsonElement element : GsonHelper.getAsJsonArray(jsonObject, "elements"))
            {
                elements.add(deserializationContext.deserialize(element, BlockElement.class));
            }
            return new ElementsModelWrapped(elements);
        }

    }

}
