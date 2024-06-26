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
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

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
    }

    private void createBucket(NRegistration.NFluids.@NotNull FluidEntry entry)
    {
        withExistingParent(name(entry.bucket()), neoLoc("item/bucket_drip")).
                customLoader(DynamicFluidContainerModelBuilder :: begin).
                fluid(entry.still().get()).
                applyFluidLuminosity(true).
                applyTint(true);
    }

    private @NotNull ResourceLocation neoLoc(String name)
    {
        return ResourceLocation.fromNamespaceAndPath("neoforge", name);
    }

    private @NotNull String name (@NotNull DeferredHolder<Item, ? extends Item> itemHolder)
    {
        return name(BuiltInRegistries.ITEM.getKey(itemHolder.get()).getPath());
    }
    private @NotNull String name (String name)
    {
        return ModelProvider.ITEM_FOLDER + "/" + name;
    }
}
