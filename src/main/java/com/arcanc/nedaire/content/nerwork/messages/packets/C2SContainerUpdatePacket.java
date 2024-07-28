/**
 * @author ArcAnc
 * Created at: 26.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.nerwork.messages.packets;

import com.arcanc.nedaire.content.gui.container_menu.IScreenMessageReceive;
import com.arcanc.nedaire.util.NDatabase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record C2SContainerUpdatePacket(int containerId, CompoundTag tag) implements IPacket
{
    public static final CustomPacketPayload.Type<C2SContainerUpdatePacket> TYPE = new CustomPacketPayload.Type<>(NDatabase.modRL("message_container_update"));
    public static final StreamCodec<FriendlyByteBuf, C2SContainerUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            C2SContainerUpdatePacket :: containerId,
            ByteBufCodecs.COMPOUND_TAG,
            C2SContainerUpdatePacket::tag,
            C2SContainerUpdatePacket::new
    );

    @Override
    public void process(@NotNull IPayloadContext context)
    {
        ServerPlayer player = serverPlayer(context);
        if (player != null)
            context.enqueueWork(() ->
            {
                player.resetLastActionTime();
                if (player.containerMenu.containerId == containerId() &&
                    player.containerMenu instanceof IScreenMessageReceive nMenu)
                        nMenu.receiveMessageFromScreen(tag());
            });
    }

    @Override
    public @NotNull Type<C2SContainerUpdatePacket> type()
    {
        return TYPE;
    }
}
