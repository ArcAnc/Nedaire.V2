/**
 * @author ArcAnc
 * Created at: 24.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.data;

import com.arcanc.nedaire.util.NDatabase;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class NItemModelProvider extends ItemModelProvider
{
    public NItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, NDatabase.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels()
    {

    }
}
