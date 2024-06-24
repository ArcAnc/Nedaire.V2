/**
 * @author ArcAnc
 * Created at: 23.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.gui.nerwork.messages;

import com.arcanc.nedaire.content.gui.container_menu.NContainerMenu;
import com.arcanc.nedaire.content.gui.sync.GenericDataSerializers;
import com.arcanc.nedaire.util.NDatabase;
import com.arcanc.nedaire.util.helpers.PacketHelper;
import com.arcanc.nedaire.util.helpers.RenderHelper;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record S2CMessageContainerData(List<Pair<Integer, GenericDataSerializers.DataPair<?>>> synced) implements IMessage
{
    public static final CustomPacketPayload.Type<S2CMessageContainerData> TYPE = new CustomPacketPayload.Type<>(NDatabase.modRL("message_container_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CMessageContainerData> STREAM_CODEC = StreamCodec.composite(
            StreamCodec.of(
                    (pBuffer, pValue) -> PacketHelper.writeList(pBuffer, pValue,
                        (pair, buf) ->
                        {
                            buf.writeVarInt(pair.getFirst());
                            pair.getSecond().write(buf);
                        }),
                    pBuffer -> PacketHelper.readList(pBuffer, pb -> Pair.of(pb.readVarInt(), GenericDataSerializers.read(pb)))),
            S2CMessageContainerData:: synced,
            S2CMessageContainerData:: new
            );

    public S2CMessageContainerData(List<Pair<Integer, GenericDataSerializers.DataPair<?>>> synced)
    {
        this.synced = synced;
    }

    @Override
    public void process(@NotNull IPayloadContext context)
    {
        context.enqueueWork(() -> {
            AbstractContainerMenu currentContainer = RenderHelper.clientPlayer().containerMenu;
            if(currentContainer instanceof NContainerMenu ieContainer)
                ieContainer.receiveSync(synced);
        });
    }

    @Override
    public @NotNull Type<S2CMessageContainerData> type()
    {
        return TYPE;
    }
}
