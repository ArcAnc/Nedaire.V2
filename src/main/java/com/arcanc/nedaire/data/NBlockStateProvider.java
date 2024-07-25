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
import com.arcanc.nedaire.util.helpers.BlockHelper;
import com.arcanc.nedaire.util.model.SimpleModel;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

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
        ResourceLocation texTop = NDatabase.modRL(blockPrefix(NDatabase.BlocksInfo.Names.FLUID_STORAGE + "/updown"));
        ResourceLocation texGlass = NDatabase.modRL(blockPrefix(NDatabase.BlocksInfo.Names.FLUID_STORAGE + "/glass"));
        ResourceLocation texFrame = NDatabase.modRL(blockPrefix(NDatabase.BlocksInfo.Names.FLUID_STORAGE + "/frame"));
        ResourceLocation texUpg0 = NDatabase.modRL(blockPrefix(NDatabase.BlocksInfo.Names.FLUID_STORAGE + "/upg_0"));
        ResourceLocation texUpg1 = NDatabase.modRL(blockPrefix(NDatabase.BlocksInfo.Names.FLUID_STORAGE + "/upg_1"));
        ResourceLocation texUpg2 = NDatabase.modRL(blockPrefix(NDatabase.BlocksInfo.Names.FLUID_STORAGE + "/upg_2"));
        ResourceLocation texUpg3 = NDatabase.modRL(blockPrefix(NDatabase.BlocksInfo.Names.FLUID_STORAGE + "/upg_3"));
        ResourceLocation texUpg4 = NDatabase.modRL(blockPrefix(NDatabase.BlocksInfo.Names.FLUID_STORAGE + "/upg_4"));
        ModelFile model = models().withExistingParent(blockPrefix(name(block)), mcLoc(blockPrefix("block"))).
                    customLoader(SimpleModel.ModelLoader :: new).end().
                    renderType("cutout").
                    texture("glass", texGlass).
                    texture("updown", texTop).
                    texture("frame", texFrame).
                    texture("upg", texUpg0).
                    texture("upg1", texUpg1).
                    texture("upg2", texUpg2).
                    texture("upg3", texUpg3).
                    texture("upg4", texUpg4).
                    texture("particle", texGlass).
                    texture("port", getPortTexture()).
                    ao(false).
                    element().
                        from(3, 1.999f, 3).
                        to(13, 14.001f, 13).
                        allFaces((direction, faceBuilder) ->
                        {
                           if (direction.getAxis().isHorizontal())
                               faceBuilder.uvs(1, 2, 15,14).texture("#glass");
                           else
                               faceBuilder.uvs(0, 0, 16, 16).texture("#updown");
                        }).
                    end().
                    /*element().
                        from(13, 13.999f, 13).
                        to(3, 1.999f, 13).
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isHorizontal())
                                faceBuilder.uvs(1, 14, 15, 2).texture("#glass");
                            else
                                faceBuilder.uvs(16, 0, 0, 16).texture("#updown");
                        }).
                    end().*/
                    element().
                        from(11, 0, 1).
                        to(15, 4, 5).
                        allFaces((direction, faceBuilder) -> faceBuilder.uvs(0, 12, 4, 16).texture("#frame")).
                        face(EAST).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
                        face(SOUTH).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                        face(WEST).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
                        face(DOWN).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).cullface(DOWN).end().
                    end().
                    element().
                        from(11, 12, 1).
                        to(15, 16, 5).
                        allFaces((direction, faceBuilder) -> faceBuilder.uvs(0, 12, 4, 16).texture("#frame")).
                        face(NORTH).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                        face(EAST).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
                        face(WEST).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
                        face(UP).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).cullface(UP).end().
                        face(DOWN).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
                    end().
                    element().
                        from(11, 12, 11).
                        to(15, 16, 15).
                        allFaces((direction, faceBuilder) -> faceBuilder.uvs(0, 12, 4, 16).texture("#frame")).
                        face(NORTH).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
                        face(EAST).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                        face(SOUTH).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
                        face(UP).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).cullface(UP).end().
                        face(DOWN).end().
                    end().
                    element().
                        from(11, 0, 11).
                        to(15, 4, 15).
                        allFaces((direction, faceBuilder) -> faceBuilder.uvs(0, 12, 4, 16).texture("#frame")).
                        face(NORTH).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
                        face(SOUTH).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
                        face(WEST).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                        face(UP).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                        face(DOWN).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).cullface(DOWN).end().
                    end().
                    element().
                        from(1, 0, 11).
                        to(5, 4, 15).
                        allFaces((direction, faceBuilder) -> faceBuilder.uvs(0, 12, 4, 16).texture("#frame")).
                        face(NORTH).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                        face(EAST).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
                        face(WEST).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
                        face(UP).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
                        face(DOWN).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).cullface(DOWN).end().
                    end().
                    element().
                        from(1, 12,11).
                        to(5, 16, 15).
                        allFaces((direction, faceBuilder) -> faceBuilder.uvs(0, 12, 4, 16).texture("#frame")).
                        face(EAST).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
                        face(SOUTH).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                        face(WEST).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
                        face(UP).cullface(UP).end().
                        face(DOWN).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
                    end().
                    element().
                        from(1, 0,1).
                        to(5, 4, 5).
                        allFaces((direction, faceBuilder) -> faceBuilder.uvs(0, 12, 4, 16).texture("#frame")).
                        face(NORTH).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
                        face(EAST).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                        face(SOUTH).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
                        face(UP).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
                        face(DOWN).cullface(DOWN).end().
                    end().
                    element().
                        from(1, 12,1).
                        to(5, 16, 5).
                        allFaces((direction, faceBuilder) -> faceBuilder.uvs(0, 12, 4, 16).texture("#frame")).
                        face(NORTH).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
                        face(SOUTH).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end().
                        face(WEST).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end().
                        face(UP).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).cullface(UP).end().
                        face(DOWN).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end().
                    end().
                    element().
                        from(2, 0,5).
                        to(5, 2, 11).
                        /*FIXME: переписать на except, когда его добавят наконецто*/
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis() == Axis.Z)
                                return;
                            if (direction.getAxis().isHorizontal())
                                faceBuilder.uvs(4, 14, 10, 16);
                            else
                            {
                                faceBuilder.uvs(8, 8, 11, 14);
                                if (direction == DOWN)
                                    faceBuilder.cullface(direction);
                            }
                            faceBuilder.texture("#frame");

                        }).
                    end().
                    element().
                        from(11, 0,5).
                        to(14, 2, 11).
                        /*FIXME: переписать на except, когда его добавят наконецто*/
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis() == Axis.Z)
                                return;
                            if (direction.getAxis().isHorizontal())
                                faceBuilder.uvs(4, 14, 10, 16);
                            else
                            {
                                faceBuilder.uvs(8, 8, 11, 14);
                                if (direction == DOWN)
                                    faceBuilder.cullface(direction);
                            }
                            faceBuilder.texture("#frame");

                    }).
                    end().
                    element().
                        from(11, 14,5).
                        to(14, 16, 11).
                        /*FIXME: переписать на except, когда его добавят наконецто*/
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis() == Axis.Z)
                                return;
                            if (direction.getAxis().isHorizontal())
                                faceBuilder.uvs(4, 14, 10, 16);
                            else
                            {
                                faceBuilder.uvs(8, 8, 11, 14);
                                if (direction == UP)
                                    faceBuilder.cullface(direction);
                            }
                            faceBuilder.texture("#frame");

                        }).
                    end().
                    element().
                        from(2, 14,5).
                        to(5, 16, 11).
                        /*FIXME: переписать на except, когда его добавят наконецто*/
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis() == Axis.Z)
                                return;
                            if (direction.getAxis().isHorizontal())
                                faceBuilder.uvs(4, 14, 10, 16);
                            else
                            {
                                faceBuilder.uvs(8, 8, 11, 14);
                                if (direction == UP)
                                    faceBuilder.cullface(direction);
                            }
                            faceBuilder.texture("#frame");
                        }).
                    end().
                    element().
                        from(12, 4,12).
                        to(14, 12, 14).
                        /*FIXME: переписать на except, когда его добавят наконецто*/
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isVertical())
                                return;
                            faceBuilder.uvs(0, 4, 2, 12).texture("#frame");
                    }).
                    end().
                    element().
                        from(12, 4, 2).
                        to(14, 12, 4).
                        /*FIXME: переписать на except, когда его добавят наконецто*/
                        allFaces((direction, faceBuilder) ->
                        {
                           if (direction.getAxis().isVertical())
                               return;
                           faceBuilder.uvs(0, 4, 2, 12).texture("#frame");
                        }).
                    end().
                    element().
                        from(2, 4, 2).
                        to(4, 12, 4).
                        /*FIXME: переписать на except, когда его добавят наконецто*/
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isVertical())
                                return;
                            faceBuilder.uvs(0, 4, 2, 12).texture("#frame");
                        }).
                    end().
                    element().
                        from(2, 4, 12).
                        to(4, 12, 14).
                        /*FIXME: переписать на except, когда его добавят наконецто*/
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isVertical())
                                return;
                            faceBuilder.uvs(0, 4, 2, 12).texture("#frame");
                        }).
                    end().
                    element().
                        from(5, 14, 11).
                        to(11,16, 14).
                        /*FIXME: переписать на except, когда его добавят наконецто*/
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis() == Axis.X)
                                return;
                            if (direction.getAxis().isHorizontal())
                                faceBuilder.uvs(4, 14, 10, 16);
                            else
                            {
                                faceBuilder.uvs(8, 8, 11, 14).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90);
                                if (direction == UP)
                                    faceBuilder.cullface(direction);
                            }
                            faceBuilder.texture("#frame");
                        }).
                    end().
                    element().
                        from(5, 0, 11).
                        to(11, 2, 14).
                        /*FIXME: переписать на except, когда его добавят наконецто*/
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis() == Axis.X)
                                return;
                            if (direction.getAxis().isHorizontal())
                                faceBuilder.uvs(4, 14, 10, 16);
                            else
                            {
                                faceBuilder.uvs(8, 8, 11, 14).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90);
                                if (direction == DOWN)
                                    faceBuilder.cullface(direction);
                            }
                            faceBuilder.texture("#frame");
                    }).
                    end().
                    element().
                        from(5, 14, 2).
                        to(11, 16, 5).
                        /*FIXME: переписать на except, когда его добавят наконецто*/
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis() == Axis.X)
                                return;
                            if (direction.getAxis().isHorizontal())
                                faceBuilder.uvs(4, 14, 10, 16);
                            else
                            {
                                faceBuilder.uvs(8, 8, 11, 14).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90);
                                if (direction == UP)
                                    faceBuilder.cullface(direction);
                            }
                            faceBuilder.texture("#frame");
                        }).
                    end().
                    element().
                        from(5, 0, 2).
                        to(11, 2, 5).
                        /*FIXME: переписать на except, когда его добавят наконецто*/
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis() == Axis.X)
                                return;
                            if (direction.getAxis().isHorizontal())
                                faceBuilder.uvs(4, 14, 10, 16);
                            else
                            {
                                faceBuilder.uvs(8, 8, 11, 14).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90);
                                if (direction == DOWN)
                                    faceBuilder.cullface(direction);
                            }
                            faceBuilder.texture("#frame");
                        }).
                    end().
                    element().
                        from(1, 6.5f, 13).
                        to(3, 9.5f, 15).
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isHorizontal())
                                faceBuilder.uvs(14, 6, 16, 9);
                            else
                            {
                                if (direction == DOWN)
                                    faceBuilder.uvs(14, 10, 16, 12);
                                else
                                    faceBuilder.uvs(14, 3, 16, 5);
                            }
                            faceBuilder.texture("#upg").tintindex(1);
                        }).
                    end().
                    element().
                        from(13, 6.5f, 13).
                        to(15, 9.5f, 15).
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isHorizontal())
                                faceBuilder.uvs(14, 6, 16, 9);
                            else
                            {
                                if (direction == DOWN)
                                    faceBuilder.uvs(14, 10, 16, 12);
                                else
                                    faceBuilder.uvs(14, 3, 16, 5);
                            }
                            faceBuilder.texture("#upg").tintindex(1);
                        }).
                    end().
                    element().
                        from(13, 6.5f, 1).
                        to(15, 9.5f, 3).
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isHorizontal())
                                faceBuilder.uvs(14, 6, 16, 9);
                            else
                            {
                                if (direction == DOWN)
                                    faceBuilder.uvs(14, 10, 16, 12);
                                else
                                    faceBuilder.uvs(14, 3, 16, 5);
                            }
                            faceBuilder.texture("#upg").tintindex(1);
                        }).
                    end().
                    element().
                        from(1, 6.5f, 1).
                        to(3, 9.5f, 3).
                        allFaces((direction, faceBuilder) ->
                        {
                            if (direction.getAxis().isHorizontal())
                                faceBuilder.uvs(14, 6, 16, 9);
                            else
                            {
                                if (direction == DOWN)
                                    faceBuilder.uvs(14, 10, 16, 12);
                                else
                                    faceBuilder.uvs(14, 3, 16, 5);
                            }
                            faceBuilder.texture("#upg").tintindex(1);
                        }).
                    end().
                    element().
                        from(0, 1.998f, 0).
                        to(16, 1.998f, 16).
                        face(DOWN).uvs(0,0, 16, 16).texture("#port").end().
                    end().
                    element().
                        from(0, 14.002f, 0).
                        to(16, 14.002f, 16).
                        face(UP).uvs(0,0, 16, 16).texture("#port").end().
                    end();

        /*getVariantBuilder(block).forAllStates(state ->
        {
            int lvl = state.getValue(BlockHelper.BlockProperties.UPGRADE_LEVEL);

            return ConfiguredModel.builder().modelFile(models.get(lvl)).build();
        });

        itemModels().getBuilder(itemPrefix(name(block))).
                parent(models.getFirst());*/
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
