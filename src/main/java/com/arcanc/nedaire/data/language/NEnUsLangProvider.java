/**
 * @author ArcAnc
 * Created at: 2022-03-31
 * Copyright (c) 2022
 * <p>
 * This code is licensed under "Ancient's License of Common Sense"	
 * Details can be found in the license file in the root folder of this project
 */
package com.arcanc.nedaire.data.language;

import com.arcanc.nedaire.registration.NRegistration;
import com.arcanc.nedaire.util.NDatabase;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.jetbrains.annotations.NotNull;

public class NEnUsLangProvider extends LanguageProvider
{

	public NEnUsLangProvider(PackOutput output) 
	{
		super(output, NDatabase.MOD_ID, "en_us");
	}

	@Override
	protected void addTranslations() 
	{
		addItem(NRegistration.NFluids.ENERGON_RED.bucket(), "Red Energon Bucket");
		addItem(NRegistration.NFluids.ENERGON_DARK.bucket(),  "Dark Energon Bucket");
		addItem(NRegistration.NFluids.ENERGON_YELLOW.bucket(),   "Yellow Energon Bucket");
		addItem(NRegistration.NFluids.ENERGON_GREEN.bucket(),  "Green Energon Bucket");
		addItem(NRegistration.NFluids.ENERGON_BLUE.bucket(),  "Blue Energon Bucket");
		addItem(NRegistration.NItems.WRENCH,  "Wrench");

		addBlock(NRegistration.NBlocks.FLUID_STORAGE_BLOCK,  "Fluid Storage");
		addBlock(NRegistration.NBlocks.FLUID_TRANSMITTER_BLOCK,  "Fluid Transmitter");
	}
	

	@Override
	public @NotNull String getName()
	{
		return "Nedaire EnUs Provider";
	}

}
