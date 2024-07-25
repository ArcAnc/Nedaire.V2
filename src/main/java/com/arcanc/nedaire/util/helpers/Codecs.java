/**
 * @author ArcAnc
 * Created at: 25.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.util.helpers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class Codecs
{
    public static final StreamCodec<RegistryFriendlyByteBuf, Vec3> VEC_3_STREAM_CODEC = StreamCodec.of(
            (buffer, vec3) ->
                {
                    buffer.writeDouble(vec3.x());
                    buffer.writeDouble(vec3.y());
                    buffer.writeDouble(vec3.z());
                },
            buffer -> new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));

    public static final StreamCodec<RegistryFriendlyByteBuf, UUID> UUID_STREAM_CODEC = StreamCodec.of(
            (buffer, value) -> buffer.writeUUID(value),
            buffer -> FriendlyByteBuf.readUUID(buffer));
}
