/**
 * @author ArcAnc
 * Created at: 22.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.nerwork.messages.packets;

import com.arcanc.nedaire.util.NDatabase;
import com.arcanc.nedaire.util.handlers.FluidTransportHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public record S2CCreateFluidTransportPacket(FluidTransportHandler.Transport transport) implements IPacket
{
    public static final CustomPacketPayload.Type<S2CCreateFluidTransportPacket> TYPE = new CustomPacketPayload.Type<>(NDatabase.modRL("message_create_fluid_transport"));
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CCreateFluidTransportPacket> STREAM_CODEC = StreamCodec.composite(
            FluidTransportHandler.Transport.STREAM_CODEC,
            S2CCreateFluidTransportPacket:: transport,
            S2CCreateFluidTransportPacket:: new);


    @Override
    public void process(@NotNull IPayloadContext context)
    {
        context.enqueueWork(() ->
        {
            Map<UUID, FluidTransportHandler.Transport> data = FluidTransportHandler.getTransportData(true);
            data.putIfAbsent(transport().getId(), transport());
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
