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
import com.arcanc.nedaire.registration.NRegistration;
import com.arcanc.nedaire.util.inventory.fluids.SimpleFluidHandler;
import com.mojang.serialization.MapCodec;
import mcjty.theoneprobe.api.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class NodeBlock extends NBaseEntityBlock<NodeBlockEntity> implements IProbeInfoAccessor
{
    public static final MapCodec<NodeBlock> CODEC = simpleCodec(NodeBlock :: new);

    public NodeBlock(Properties blockProps)
    {
        super(NRegistration.NBlockEntities.BE_NODE, blockProps);
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

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, @NotNull Level level, BlockState blockState, @NotNull IProbeHitData iProbeHitData)
    {
        BlockEntity tile = level.getBlockEntity(iProbeHitData.getPos());
        if (tile instanceof NodeBlockEntity be)
        {
            SimpleFluidHandler handler = be.getHandler(null);

            TankReference[] references = TankReference.createSplitHandler(handler);

            for (TankReference ref : references)
            {
                iProbeInfo.tank(ref, iProbeInfo.
                        defaultProgressStyle().
                        alignment(ElementAlignment.ALIGN_CENTER).
                        suffix(" | %s EE", ref.getCapacity()));
            }
        }
    }
}
