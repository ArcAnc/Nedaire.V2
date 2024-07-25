/**
 * @author ArcAnc
 * Created at: 09.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.block.model_loaders;

import com.arcanc.nedaire.content.block.block_entity.FluidStorageBlockEntity;
import com.arcanc.nedaire.util.NDatabase;
import com.arcanc.nedaire.util.Upgrade;
import com.arcanc.nedaire.util.helpers.RenderHelper;
import com.arcanc.nedaire.util.model.RetexturedBakedQuad;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

public class FluidStorageBakedModel extends BakedModelWrapper<BakedModel> implements IDynamicBakedModel
{
    private static final ResourceLocation[] FRAME_TEXTURE = new ResourceLocation[]
            {
                    NDatabase.modRL("block/" + NDatabase.BlocksInfo.Names.FLUID_STORAGE + "/upg_0"),
                    NDatabase.modRL("block/" + NDatabase.BlocksInfo.Names.FLUID_STORAGE + "/upg_1"),
                    NDatabase.modRL("block/" + NDatabase.BlocksInfo.Names.FLUID_STORAGE + "/upg_2"),
                    NDatabase.modRL("block/" + NDatabase.BlocksInfo.Names.FLUID_STORAGE + "/upg_3"),
                    NDatabase.modRL("block/" + NDatabase.BlocksInfo.Names.FLUID_STORAGE + "/upg_4")
            };

    private static final EnumMap<Upgrade, BakedQuad[]> UPGRADE_QUAD_CACHE = new EnumMap<>(Upgrade.class);

    public FluidStorageBakedModel(BakedModel originalModel)
    {
        super(originalModel);
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state,
                                             @Nullable Direction side,
                                             @NotNull RandomSource rand,
                                             @NotNull ModelData extraData,
                                             @Nullable RenderType renderType)
    {
        LinkedList<BakedQuad> quads = new LinkedList<>(originalModel.getQuads(state, side, rand, extraData, renderType));

        if (side != null || quads.isEmpty())
            return quads;

        if (!extraData.has(FluidStorageBlockEntity.UPGRADE_MODEL_PROPERTY))
            return quads;

        Upgrade upg = extraData.get(FluidStorageBlockEntity.UPGRADE_MODEL_PROPERTY);
        ResourceLocation loc = FRAME_TEXTURE[upg.getLvl()];

        for (int q = 0; q < quads.size(); q++)
        {
            BakedQuad quad = quads.get(q);
            if (quad.isTinted() && quad.getTintIndex() == 1)
            {
                BakedQuad[] cachedQuads = UPGRADE_QUAD_CACHE.get(upg);
                if (cachedQuads == null || cachedQuads.length < 24)
                    cachedQuads = new BakedQuad[24];

                if (cachedQuads[0] == null)
                {
                    for (int z = 0; z < 24; z++)
                    {
                        quad = quads.get(q + z);
                        cachedQuads[z] = new RetexturedBakedQuad(quad, RenderHelper.getTexture(loc));
                    }
                    UPGRADE_QUAD_CACHE.put(upg, cachedQuads);
                }
                for (int z = 0; z < 24; z++)
                {
                    quads.set(q + z, cachedQuads[z]);
                }
                break;
            }
        }
        return quads;
    }
}
