/**
 * @author ArcAnc
 * Created at: 14.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.block;

import com.arcanc.nedaire.util.helpers.BlockHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockInterfaces
{
    public interface INWrencheble
    {
        InteractionResult onUsed(@NotNull UseOnContext ctx);
    }

    public interface BlockStateProvider
    {
        BlockState getState();

        void setState(BlockState newState);
    }

    public interface IGeneralMultiblock extends BlockStateProvider
    {
        @Nullable
        IGeneralMultiblock master();

        default boolean isDummy()
        {
            BlockState state = getState();
            if(state.hasProperty(BlockHelper.BlockProperties.MULTIBLOCK_SLAVE))
                return state.getValue(BlockHelper.BlockProperties.MULTIBLOCK_SLAVE);
            else
                return true;
        }
    }

}
