/**
 * @author ArcAnc
 * Created at: 23.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.nerwork.messages;

import com.arcanc.nedaire.content.nerwork.messages.packets.C2SContainerUpdatePacket;
import com.arcanc.nedaire.content.nerwork.messages.packets.IPacket;
import com.arcanc.nedaire.content.nerwork.messages.packets.S2CContainerDataPacket;
import com.arcanc.nedaire.content.nerwork.messages.packets.S2CCreateFluidTransportPacket;
import com.arcanc.nedaire.util.NDatabase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class NetworkEngine
{
    public static void setupMessages(final @NotNull RegisterPayloadHandlersEvent event)
    {
        final PayloadRegistrar registrar = event.registrar(NDatabase.MOD_ID);

        registerMessage(registrar, S2CContainerDataPacket.STREAM_CODEC, S2CContainerDataPacket.TYPE, PacketFlow.CLIENTBOUND);
        registerMessage(registrar, S2CCreateFluidTransportPacket.STREAM_CODEC, S2CCreateFluidTransportPacket.TYPE, PacketFlow.CLIENTBOUND);

        registerMessage(registrar, C2SContainerUpdatePacket.STREAM_CODEC, C2SContainerUpdatePacket.TYPE, PacketFlow.SERVERBOUND);
    }

    private <T extends IPacket> void registerMessage(
            PayloadRegistrar registrar, StreamCodec<? super RegistryFriendlyByteBuf,T> reader, CustomPacketPayload.Type<T> type
    )
    {
        registerMessage(registrar, reader, type, Optional.empty());
    }

    private static <T extends IPacket> void registerMessage(
            PayloadRegistrar registrar, StreamCodec<? super RegistryFriendlyByteBuf,T> reader, CustomPacketPayload.Type<T> type, @NotNull PacketFlow direction
    )
    {
        registerMessage(registrar, reader, type, Optional.of(direction));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static <T extends IPacket> void registerMessage(
            PayloadRegistrar registrar, StreamCodec<? super RegistryFriendlyByteBuf, T> reader, CustomPacketPayload.Type<T> type, @NotNull Optional<PacketFlow> direction
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

    public static void sendToServer(@NotNull final IPacket packet)
    {
        PacketDistributor.sendToServer(packet);
    }

    public static void sendToAllClients(@NotNull final IPacket packet)
    {
        PacketDistributor.sendToAllPlayers(packet);
    }

    public static void sendToPlayer(@NotNull ServerPlayer player, @NotNull final IPacket packet)
    {
        PacketDistributor.sendToPlayer(player, packet);
    }

    public static void sendToPlayerNear(@NotNull ServerLevel level, @Nullable ServerPlayer exclude, @NotNull Vec3 position, double radius, @NotNull IPacket packet)
    {
        PacketDistributor.sendToPlayersNear(level, exclude, position.x(), position.y(), position.z(), radius, packet);
    }
}
