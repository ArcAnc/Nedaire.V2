/**
 * @author ArcAnc
 * Created at: 23.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.block.block_entity.ticking;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

/*This code was taken from Immersive Engineering. Thanks BluSunrize, it's perfect*/
public interface NClientTickableBE extends NTickableBase
{
    void tickClient();

    static <T extends BlockEntity> BlockEntityTicker<T> makeTicker() {
        return (level, pos, state, blockEntity) -> {
            NClientTickableBE tickable = (NClientTickableBE) blockEntity;
            if (tickable.canTickAny())
                tickable.tickClient();
        };
    }
}
