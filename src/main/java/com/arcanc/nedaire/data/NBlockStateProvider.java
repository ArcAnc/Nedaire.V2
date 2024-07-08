/**
 * @author ArcAnc
 * Created at: 24.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.data;

import com.arcanc.nedaire.content.block.FluidTransmitterBlock;
import com.arcanc.nedaire.registration.NRegistration;
import com.arcanc.nedaire.util.NDatabase;
import com.arcanc.nedaire.util.Upgrade;
import com.arcanc.nedaire.util.helpers.BlockHelper;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.arcanc.nedaire.util.helpers.BlockHelper.BlockProperties.ENABLED;
import static com.arcanc.nedaire.util.helpers.BlockHelper.BlockProperties.FACING;
import static net.minecraft.core.Direction.*;

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
        registerFluidTransmitter(NRegistration.NBlocks.FLUID_TRANSMITTER_BLOCK.get());
    }

    private void registerFluidTransmitter(FluidTransmitterBlock block)
    {
        ResourceLocation glassTex = NDatabase.modRL(blockPrefix(NDatabase.BlocksInfo.Names.FLUID_TRANSMITTER + "/glass"));
        ResourceLocation baseTex = NDatabase.modRL(blockPrefix(NDatabase.BlocksInfo.Names.FLUID_TRANSMITTER + "/base"));
        ResourceLocation frameTex = NDatabase.modRL(blockPrefix(NDatabase.BlocksInfo.Names.FLUID_TRANSMITTER + "/frame"));
        ResourceLocation buttonTex = NDatabase.modRL(blockPrefix(NDatabase.BlocksInfo.Names.FLUID_TRANSMITTER + "/button"));

        ModelFile modelEnabled = models().withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("block"))).
                renderType("cutout").
                texture("glass", glassTex).
                texture("base", baseTex).
                texture("frame", frameTex).
                texture("button", buttonTex).
                texture("particle", glassTex).
                ao(false).
                element().
                    from(5,0,5).
                    to(11, 2, 11).
                    allFaces((face, builder) ->
                    {
                       builder.texture("#base");
                       if (face.getAxis().isHorizontal())
                           builder.uvs(0,0,12, 4);
                       else
                       {
                           builder.uvs(0, 4, 12, 16);
                           if (face == DOWN)
                               builder.cullface(face);
                       }
                    }).
                end().
                element().
                    from(7,2,7).
                    to(9, 5, 9).
                    face(NORTH).end().
                    face(SOUTH).end().
                    face(WEST).end().
                    face(EAST).end().
                    faces((face, builder) -> builder.uvs(0, 0, 8, 12).texture("#glass")).
                end().
                element().
                    from(6, 2, 6).
                    to(7, 5, 7).
                    face(NORTH).end().
                    face(SOUTH).end().
                    face(WEST).end().
                    face(EAST).end().
                    faces((face, builder) -> builder.uvs(12, 4, 16, 16).texture("#frame")).
                end().
                element().
                    from(6,2,9).
                    to(7, 5, 10).
                    face(NORTH).end().
                    face(SOUTH).end().
                    face(WEST).end().
                    face(EAST).end().
                    faces((face, builder) -> builder.uvs(12, 4, 16, 16).texture("#frame")).
                end().
                element().
                    from(9,2,9).
                    to(10, 5, 10).
                    face(NORTH).end().
                    face(SOUTH).end().
                    face(WEST).end().
                    face(EAST).end().
                    faces((face, builder) -> builder.uvs(12, 4, 16, 16).texture("#frame")).
                end().
                element().
                    from(9,2,6).
                    to(10, 5, 7).
                    face(NORTH).end().
                    face(SOUTH).end().
                    face(WEST).end().
                    face(EAST).end().
                    faces((face, builder) -> builder.uvs(12, 4, 16, 16).texture("#frame")).
                end().
                element().
                    from(6,5,7).
                    to(7, 6, 9).
                    face(DOWN).end().
                    face(UP).end().
                    face(WEST).end().
                    face(EAST).end().
                    faces((face, builder) ->
                    {
                        builder.uvs(4, 0, 12, 4).texture("#frame");
                        if (face.getAxis().isVertical())
                            builder.rotation(ModelBuilder.FaceRotation.CLOCKWISE_90);
                    }).
                end().
                element().
                    from(7,5,9).
                    to(9, 6, 10).
                    face(DOWN).end().
                    face(UP).end().
                    face(NORTH).end().
                    face(SOUTH).end().
                    faces((face, builder) ->
                    {
                        builder.uvs(4, 0, 12, 4).texture("#frame");
                        if (face.getAxis().isVertical())
                            builder.rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN);
                    }).
                end().
                element().
                    from(7,5,6).
                    to(9, 6, 7).
                    face(DOWN).end().
                    face(UP).end().
                    face(NORTH).end().
                    face(SOUTH).end().
                    faces((face, builder) ->
                    {
                        builder.uvs(4, 0, 12, 4).texture("#frame");
                        if (face.getAxis().isVertical())
                            builder.rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN);
                    }).
                end().
                element().
                    from(9,5,6).
                    to(10, 6, 7).
                    face(EAST).end().
                    face(NORTH).end().
                    face(UP).end().
                    faces((face, builder) ->
                    {
                        builder.uvs(0, 0, 4, 4).texture("#frame");
                        if (face != NORTH)
                            builder.rotation(ModelBuilder.FaceRotation.CLOCKWISE_90);
                    }).
                end().
                element().
                    from(9,5,9).
                    to(10, 6, 10).
                    face(EAST).end().
                    face(SOUTH).end().
                    face(UP).end().
                    faces((face, builder) ->
                    {
                        builder.uvs(0, 0, 4, 4).texture("#frame");
                        if (face == SOUTH)
                            builder.rotation(ModelBuilder.FaceRotation.CLOCKWISE_90);
                        else if (face == UP)
                            builder.rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN);
                    }).
                end().
                element().
                    from(6,5,9).
                    to(7, 6, 10).
                    face(WEST).end().
                    face(SOUTH).end().
                    face(UP).end().
                    faces((face, builder) ->
                    {
                        builder.uvs(0, 0, 4, 4).texture("#frame");
                        if (face == WEST)
                            builder.rotation(ModelBuilder.FaceRotation.CLOCKWISE_90);
                        else if (face == UP)
                            builder.rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90);
                }).
                end().
                    element().
                    from(6,5,6).
                    to(7, 6, 7).
                    face(NORTH).end().
                    face(WEST).end().
                    face(UP).end().
                    faces((face, builder) ->
                    {
                        builder.uvs(0, 0, 4, 4).texture("#frame");
                        if (face == NORTH)
                            builder.rotation(ModelBuilder.FaceRotation.CLOCKWISE_90);
                    }).
                end().
                element().
                    from(9,5,7).
                    to(10, 6, 9).
                    face(EAST).end().
                    face(WEST).end().
                    face(UP).end().
                    face(DOWN).end().
                    faces((face, builder) ->
                    {
                        builder.uvs(4, 0, 12, 4).texture("#frame");
                        if (face.getAxis().isVertical())
                            builder.rotation(ModelBuilder.FaceRotation.CLOCKWISE_90);
                    }).
                end().
                    element().
                    from(7,6,7).
                    to(9, 7, 9).
                    emissivity(5, 5).
                    face(NORTH).end().
                    face(SOUTH).end().
                    face(WEST).end().
                    face(EAST).end().
                    face(UP).end().
                    faces((face, builder) ->
                    {
                        builder.texture("#button");
                        if (face != UP)
                            builder.uvs(8,8, 16,12);
                        else
                            builder.uvs(8, 0, 16, 8);
                    }).
                end();

        ModelFile modelDisabled = models().withExistingParent(blockPrefix(name(block)) + "_dis", mcLoc(blockPrefix("block"))).
                renderType("cutout").
                texture("glass", glassTex).
                texture("base", baseTex).
                texture("frame", frameTex).
                texture("button", buttonTex).
                texture("particle", glassTex).
                ao(false).
                element().
                    from(5,0,5).
                    to(11, 2, 11).
                    allFaces((face, builder) ->
                    {
                        builder.texture("#base");
                        if (face.getAxis().isHorizontal())
                            builder.uvs(0,0,12, 4);
                        else
                        {
                            builder.uvs(0, 4, 12, 16);
                            if (face == DOWN)
                                builder.cullface(face);
                        }
                }).
                end().
                element().
                    from(7,2,7).
                    to(9, 5, 9).
                    face(NORTH).end().
                    face(SOUTH).end().
                    face(WEST).end().
                    face(EAST).end().
                    faces((face, builder) -> builder.uvs(0, 0, 8, 12).texture("#glass")).
                end().
                element().
                    from(6, 2, 6).
                    to(7, 5, 7).
                    face(NORTH).end().
                    face(SOUTH).end().
                    face(WEST).end().
                    face(EAST).end().
                    faces((face, builder) -> builder.uvs(12, 4, 16, 16).texture("#frame")).
                end().
                element().
                    from(6,2,9).
                    to(7, 5, 10).
                    face(NORTH).end().
                    face(SOUTH).end().
                    face(WEST).end().
                    face(EAST).end().
                    faces((face, builder) -> builder.uvs(12, 4, 16, 16).texture("#frame")).
                end().
                element().
                    from(9,2,9).
                    to(10, 5, 10).
                    face(NORTH).end().
                    face(SOUTH).end().
                    face(WEST).end().
                    face(EAST).end().
                    faces((face, builder) -> builder.uvs(12, 4, 16, 16).texture("#frame")).
                end().
                element().
                    from(9,2,6).
                    to(10, 5, 7).
                    face(NORTH).end().
                    face(SOUTH).end().
                    face(WEST).end().
                    face(EAST).end().
                    faces((face, builder) -> builder.uvs(12, 4, 16, 16).texture("#frame")).
                end().
                element().
                    from(6,5,7).
                    to(7, 6, 9).
                    face(DOWN).end().
                    face(UP).end().
                    face(WEST).end().
                    face(EAST).end().
                    faces((face, builder) ->
                    {
                        builder.uvs(4, 0, 12, 4).texture("#frame");
                        if (face.getAxis().isVertical())
                            builder.rotation(ModelBuilder.FaceRotation.CLOCKWISE_90);
                    }).
                end().
                element().
                    from(7,5,9).
                    to(9, 6, 10).
                    face(DOWN).end().
                    face(UP).end().
                    face(NORTH).end().
                    face(SOUTH).end().
                    faces((face, builder) ->
                    {
                        builder.uvs(4, 0, 12, 4).texture("#frame");
                        if (face.getAxis().isVertical())
                            builder.rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN);
                    }).
                end().
                element().
                    from(7,5,6).
                    to(9, 6, 7).
                    face(DOWN).end().
                    face(UP).end().
                    face(NORTH).end().
                    face(SOUTH).end().
                    faces((face, builder) ->
                    {
                        builder.uvs(4, 0, 12, 4).texture("#frame");
                        if (face.getAxis().isVertical())
                            builder.rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN);
                    }).
                end().
                element().
                    from(9,5,6).
                    to(10, 6, 7).
                    face(EAST).end().
                    face(NORTH).end().
                    face(UP).end().
                    faces((face, builder) ->
                    {
                        builder.uvs(0, 0, 4, 4).texture("#frame");
                        if (face != NORTH)
                            builder.rotation(ModelBuilder.FaceRotation.CLOCKWISE_90);
                    }).
                end().
                element().
                    from(9,5,9).
                    to(10, 6, 10).
                    face(EAST).end().
                    face(SOUTH).end().
                    face(UP).end().
                    faces((face, builder) ->
                    {
                        builder.uvs(0, 0, 4, 4).texture("#frame");
                        if (face == SOUTH)
                            builder.rotation(ModelBuilder.FaceRotation.CLOCKWISE_90);
                        else if (face == UP)
                            builder.rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN);
                    }).
                end().
                element().
                    from(6,5,9).
                    to(7, 6, 10).
                    face(WEST).end().
                    face(SOUTH).end().
                    face(UP).end().
                    faces((face, builder) ->
                    {
                        builder.uvs(0, 0, 4, 4).texture("#frame");
                        if (face == WEST)
                            builder.rotation(ModelBuilder.FaceRotation.CLOCKWISE_90);
                        else if (face == UP)
                            builder.rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90);
                    }).
                end().
                element().
                    from(6,5,6).
                    to(7, 6, 7).
                    face(NORTH).end().
                    face(WEST).end().
                    face(UP).end().
                    faces((face, builder) ->
                    {
                        builder.uvs(0, 0, 4, 4).texture("#frame");
                        if (face == NORTH)
                            builder.rotation(ModelBuilder.FaceRotation.CLOCKWISE_90);
                    }).
                end().
                element().
                    from(9,5,7).
                    to(10, 6, 9).
                    face(EAST).end().
                    face(WEST).end().
                    face(UP).end().
                    face(DOWN).end().
                    faces((face, builder) ->
                    {
                        builder.uvs(4, 0, 12, 4).texture("#frame");
                        if (face.getAxis().isVertical())
                            builder.rotation(ModelBuilder.FaceRotation.CLOCKWISE_90);
                    }).
                end().
                element().
                    from(7,6,7).
                    to(9, 7, 9).
                    face(NORTH).end().
                    face(SOUTH).end().
                    face(WEST).end().
                    face(EAST).end().
                    face(UP).end().
                    faces((face, builder) ->
                    {
                        builder.texture("#button");
                        if (face != UP)
                            builder.uvs(0,8, 8,12);
                        else
                            builder.uvs(0, 0, 8, 8);
                    }).
                end();

        getVariantBuilder(block).forAllStates(state ->
        {
            Direction direction = state.getValue(FACING);
            boolean enabled = state.getValue(ENABLED);
            ConfiguredModel.Builder<?> builder = ConfiguredModel.builder();
            builder.modelFile(enabled ? modelEnabled : modelDisabled);

            if (direction.getAxis().isVertical())
                builder.rotationX(180 * direction.get3DDataValue());
            else
            {
                builder.rotationX(90);
                builder.rotationY(90 * direction.get2DDataValue());
            }

            return builder.
                    build();
        });

        itemModels().getBuilder(itemPrefix(name(block))).
                parent(modelDisabled);
    }

    private void registerFluidStorage(Block block)
    {
        List<ModelFile> models = new ArrayList<>();
        ResourceLocation texTop = NDatabase.modRL(blockPrefix(NDatabase.BlocksInfo.Names.FLUID_STORAGE + "/updown"));
        for (Upgrade upg : Upgrade.values())
        {
            ResourceLocation texGlass = NDatabase.modRL(blockPrefix(NDatabase.BlocksInfo.Names.FLUID_STORAGE + "/glass_" + upg.getLvl()));

            ModelFile model = models().withExistingParent(blockPrefix(name(block)) + "_" + upg.getLvl(), mcLoc(blockPrefix("block"))).
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

            models.add(model);
        }

        getVariantBuilder(block).forAllStates(state ->
        {
            int lvl = state.getValue(BlockHelper.BlockProperties.UPGRADE_LEVEL);

            return ConfiguredModel.builder().modelFile(models.get(lvl)).build();
        });

        itemModels().getBuilder(itemPrefix(name(block))).
                parent(models.getFirst());
        //registerModels(block, model);
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

    private @NotNull ResourceLocation getPortTexture()
    {
        return NDatabase.modRL(blockPrefix("port"));
    }

    private @NotNull String itemPrefix(String str)
    {
        return ModelProvider.ITEM_FOLDER + "/" + str;
    }

    private @NotNull String blockPrefix(String str)
    {
        return ModelProvider.BLOCK_FOLDER + "/" + str;
    }

    private @NotNull String name(Block block)
    {
        return BlockHelper.getRegistryName(block).getPath();
    }

    @Override
    public @NotNull String getName() {
        return "Nedaire Block States";
    }
}
