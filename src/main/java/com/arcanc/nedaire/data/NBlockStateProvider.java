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
import com.arcanc.nedaire.util.helpers.BlockHelper;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class NBlockStateProvider extends BlockStateProvider
{
    public NBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper)
    {
        super(output, NDatabase.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels()
    {
        registerSimpleBlock(NRegistration.NBlocks.NODE_BLOCK.get());
        registerFluidStorage(NRegistration.NBlocks.FLUID_STORAGE_BLOCK.get());
    }

    private void registerFluidStorage(Block block)
    {
        ResourceLocation texGlass = NDatabase.modRL(blockPrefix(NDatabase.BlocksInfo.Names.FLUID_STORAGE + "/" + NDatabase.BlocksInfo.Names.FLUID_STORAGE + "_glass"));
        ResourceLocation texTop = NDatabase.modRL(blockPrefix(NDatabase.BlocksInfo.Names.FLUID_STORAGE + "/" + NDatabase.BlocksInfo.Names.FLUID_STORAGE + "_updown"));

        ModelFile model = models().withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("block"))).
                renderType("cutout").
                texture("glass", texGlass).
                texture("updown", texTop).
                texture("particle", texGlass).
                texture("port", getPortTexture()).
                ao(false).
                element().
                from(3, 0, 3).
                to(13, 16, 13).
                allFaces((face, builder) ->
                {
                    builder.uvs(0, 0, 16, 16);
                    if (face.getAxis().isHorizontal())
                        builder.texture("#glass");
                    else
                        builder.texture("#updown").cullface(face);
                }).
                end().
                element().
                from(13, 16, 13).
                to(3, 0, 3).
                face(Direction.NORTH).
                uvs(0, 16, 16, 0).
                texture("#glass").
                end().
                face(Direction.SOUTH).
                uvs(0, 16, 16, 0).
                texture("#glass").
                end().
                face(Direction.WEST).
                uvs(0, 16, 16, 0).
                texture("#glass").
                end().
                face(Direction.EAST).
                uvs(0, 16, 16, 0).
                texture("#glass").
                end().
                end().
                element().
                from(-0.001f, -0.001f, -0.001f).
                to(16.001f, 16.001f, 16.001f).
                face(Direction.UP).
                texture("#port").
                cullface(Direction.UP).
                tintindex(Direction.UP.get3DDataValue()).
                end().
                face(Direction.DOWN).
                texture("#port").
                cullface(Direction.DOWN).
                tintindex(Direction.DOWN.get3DDataValue()).
                end().
                end();


        registerModels(block, model);
    }


    private void registerSimpleBlock(Block block)
    {
        ModelFile model = models().
                withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("cube_all"))).
                renderType("solid").
                texture("all", blockTexture(block)).
                texture("particle", blockTexture(block));

        registerModels(block, model);
    }

    private void registerModels(Block block, ModelFile model)
    {
        getVariantBuilder(block).partialState().addModels(new ConfiguredModel(model));

        itemModels().getBuilder(itemPrefix(name(block))).
                parent(model);
    }

    private ResourceLocation getPortTexture() {
        return NDatabase.modRL(blockPrefix("port"));
    }

    private String itemPrefix(String str) {
        return ModelProvider.ITEM_FOLDER + "/" + str;
    }

    private String blockPrefix(String str) {
        return ModelProvider.BLOCK_FOLDER + "/" + str;
    }

    private String name(Block block)
    {
        return BlockHelper.getRegistryName(block).getPath();
    }

    @Override
    public @NotNull String getName() {
        return "Nedaire Block States";
    }
}
