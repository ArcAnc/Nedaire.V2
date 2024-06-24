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
