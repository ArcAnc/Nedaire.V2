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

		add(NDatabase.GUIInfo.Descriptions.getDescription(NDatabase.GUIInfo.Descriptions.Filter.BUTTON_MINUS), "Lower transfer per tick\n§8CTRL§r -10\n§8SHIFT§r -100\n§8SHIFT + CTRL§r -1000");
		add(NDatabase.GUIInfo.Descriptions.getDescription(NDatabase.GUIInfo.Descriptions.Filter.BUTTON_PLUS), "Increase transfer per tick\n§8CTRL§r +10\n§8SHIFT§r +100\n§8SHIFT + CTRL§r +1000");

		add(NDatabase.GUIInfo.Descriptions.getDescription(NDatabase.GUIInfo.Descriptions.Filter.BUTTON_LIST_TYPE[0]), "List Type: Allow");
		add(NDatabase.GUIInfo.Descriptions.getDescription(NDatabase.GUIInfo.Descriptions.Filter.BUTTON_LIST_TYPE[1]), "List Type: Deny");
		add(NDatabase.GUIInfo.Descriptions.getDescription(NDatabase.GUIInfo.Descriptions.Filter.BUTTON_ROUTE[0]), "Route: Input");
		add(NDatabase.GUIInfo.Descriptions.getDescription(NDatabase.GUIInfo.Descriptions.Filter.BUTTON_ROUTE[1]), "Route: Output");
		add(NDatabase.GUIInfo.Descriptions.getDescription(NDatabase.GUIInfo.Descriptions.Filter.BUTTON_ROUTE[2]), "Route: Bidirectional");
		add(NDatabase.GUIInfo.Descriptions.getDescription(NDatabase.GUIInfo.Descriptions.Filter.BUTTON_NBT[0]), "NBT Filter: Check");
		add(NDatabase.GUIInfo.Descriptions.getDescription(NDatabase.GUIInfo.Descriptions.Filter.BUTTON_NBT[1]), "NBT Filter: Ignore");
		add(NDatabase.GUIInfo.Descriptions.getDescription(NDatabase.GUIInfo.Descriptions.Filter.BUTTON_TAG[0]), "Tag Filter: Check");
		add(NDatabase.GUIInfo.Descriptions.getDescription(NDatabase.GUIInfo.Descriptions.Filter.BUTTON_TAG[1]), "Tag Filter: Ignore");
		add(NDatabase.GUIInfo.Descriptions.getDescription(NDatabase.GUIInfo.Descriptions.Filter.BUTTON_MOD_OWNER[0]), "Mod Owner Filter: Check");
		add(NDatabase.GUIInfo.Descriptions.getDescription(NDatabase.GUIInfo.Descriptions.Filter.BUTTON_MOD_OWNER[1]), "Mod Owner Filter: Ignore");
		add(NDatabase.GUIInfo.Descriptions.getDescription(NDatabase.GUIInfo.Descriptions.Filter.BUTTON_TARGET[0]), "Target: Nearest First");
		add(NDatabase.GUIInfo.Descriptions.getDescription(NDatabase.GUIInfo.Descriptions.Filter.BUTTON_TARGET[1]), "Target: Furthest First");
		add(NDatabase.GUIInfo.Descriptions.getDescription(NDatabase.GUIInfo.Descriptions.Filter.BUTTON_TARGET[2]), "Target: Random");
		add(NDatabase.GUIInfo.Descriptions.getDescription(NDatabase.GUIInfo.Descriptions.Filter.BUTTON_TARGET[3]), "Target: Round Robin");
	}
	

	@Override
	public @NotNull String getName()
	{
		return "Nedaire EnUs Provider";
	}

}
