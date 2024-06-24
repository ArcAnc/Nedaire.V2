/**
 * @author ArcAnc
 * Created at: 2023-01-03
 * Copyright (c) 2023
 * 
 * This code is licensed under "Ancient's License of Common Sense"	
 * Details can be found in the license file in the root folder of this project
 */
package com.arcanc.nedaire.data;

import com.arcanc.nedaire.util.NDatabase;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class NSpriteSourceProvider extends SpriteSourceProvider
{

	public NSpriteSourceProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper fileHelper)
	{
		super(output, lookupProvider, NDatabase.MOD_ID, fileHelper);
	}

	@Override
	protected void gather()
	{
		atlas(SpriteSourceProvider.BLOCKS_ATLAS).addSource(new SingleFile(NDatabase.modRL("block/node/normal"), Optional.empty()));
	}

}
