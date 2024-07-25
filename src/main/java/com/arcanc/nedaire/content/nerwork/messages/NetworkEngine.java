/**
 * @author ArcAnc
 * Created at: 23.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.nerwork.messages;

import com.arcanc.nedaire.content.nerwork.messages.packets.IPacket;
import com.arcanc.nedaire.content.nerwork.messages.packets.S2CPacketContainerData;
import com.arcanc.nedaire.content.nerwork.messages.packets.S2CPacketCreateFluidTransport;
import com.arcanc.nedaire.util.NDatabase;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class NetworkEngine
{
    public static void setupMessages(final @NotNull RegisterPayloadHandlersEvent event)
    {
        final PayloadRegistrar registrar = event.registrar(NDatabase.MOD_ID);

        registerMessage(registrar, S2CPacketContainerData.STREAM_CODEC, S2CPacketContainerData.TYPE, PacketFlow.CLIENTBOUND);
        registerMessage(registrar, S2CPacketCreateFluidTransport.STREAM_CODEC, S2CPacketCreateFluidTransport.TYPE, PacketFlow.CLIENTBOUND);
    }

    private <T extends IPacket> void registerMessage(
            PayloadRegistrar registrar, StreamCodec<RegistryFriendlyByteBuf,T> reader, CustomPacketPayload.Type<T> type
    )
    {
        registerMessage(registrar, reader, type, Optional.empty());
    }

    private static <T extends IPacket> void registerMessage(
            PayloadRegistrar registrar, StreamCodec<RegistryFriendlyByteBuf,T> reader, CustomPacketPayload.Type<T> type, @NotNull PacketFlow direction
    )
    {
        registerMessage(registrar, reader, type, Optional.of(direction));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static <T extends IPacket> void registerMessage(
            PayloadRegistrar registrar, StreamCodec<RegistryFriendlyByteBuf, T> reader, CustomPacketPayload.Type<T> type, @NotNull Optional<PacketFlow> direction
    )
    {
        if(direction.isPresent())
            if (direction.get() == PacketFlow.CLIENTBOUND)
                registrar.playToClient(type, reader, T :: process);
            else
                registrar.playToServer(type, reader, T :: process);
        else
            registrar.playBidirectional(type, reader, T :: process);

    }
}
