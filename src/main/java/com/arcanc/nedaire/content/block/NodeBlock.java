/**
 * @author ArcAnc
 * Created at: 24.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.block;

import com.arcanc.nedaire.content.block.block_entity.NodeBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NodeBlock extends NBlockBase implements EntityBlock
{
    public static final MapCodec<NodeBlock> CODEC = simpleCodec(NodeBlock::new);

    public NodeBlock(Properties props)
    {
        super(props);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState)
    {
        return new NodeBlockEntity(pPos, pState);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected @NotNull MapCodec<NodeBlock> codec()
    {
        return CODEC;
    }
}
