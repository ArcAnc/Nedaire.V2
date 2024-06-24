/**
 * @author ArcAnc
 * Created at: 23.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.gui.nerwork.messages;

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

        registerMessage(registrar, S2CMessageContainerData.STREAM_CODEC, S2CMessageContainerData.TYPE, PacketFlow.CLIENTBOUND);
    }

    private <T extends IMessage> void registerMessage(
            PayloadRegistrar registrar, StreamCodec<RegistryFriendlyByteBuf,T> reader, CustomPacketPayload.Type<T> type
    )
    {
        registerMessage(registrar, reader, type, Optional.empty());
    }

    private static <T extends IMessage> void registerMessage(
            PayloadRegistrar registrar, StreamCodec<RegistryFriendlyByteBuf,T> reader, CustomPacketPayload.Type<T> type, @NotNull PacketFlow direction
    )
    {
        registerMessage(registrar, reader, type, Optional.of(direction));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static <T extends IMessage> void registerMessage(
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
