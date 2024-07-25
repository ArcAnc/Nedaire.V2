/**
 * @author ArcAnc
 * Created at: 21.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.util.handlers;

import com.arcanc.nedaire.Nedaire;
import com.arcanc.nedaire.content.block.block_entity.FluidTransmitterBlockEntity;
import com.arcanc.nedaire.content.nerwork.messages.packets.S2CPacketCreateFluidTransport;
import com.arcanc.nedaire.util.NDatabase;
import com.arcanc.nedaire.util.helpers.BlockHelper;
import com.arcanc.nedaire.util.helpers.Codecs;
import com.arcanc.nedaire.util.helpers.FluidHelper;
import com.arcanc.nedaire.util.helpers.RenderHelper;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class FluidTransportHandler
{
    private static final FluidTransportHandler SERVER_INSTANCE = new FluidTransportHandler();
    private static final FluidTransportHandler CLIENT_INSTANCE = new FluidTransportHandler();

    private final Object2ObjectLinkedOpenHashMap<UUID, Transport> TRANSPORT_DATA = new Object2ObjectLinkedOpenHashMap<>();
    private final Set<UUID> TO_REMOVE = new LinkedHashSet<>();
    private static final int POINTS_PER_ESSENCE = 7;
    public static final ResourceLocation ESSENTIA_TEXTURE = NDatabase.modRL("essentia");

    private FluidTransportHandler() {}

    public static FluidTransportHandler get(boolean isClient)
    {
        return isClient ? CLIENT_INSTANCE : SERVER_INSTANCE;
    }

    public static void levelTickEvent(final LevelTickEvent.@NotNull Pre event)
    {
        FluidTransportHandler handler = FluidTransportHandler.get(event.getLevel().isClientSide());
        if (event.hasTime())
        {
            handler.TRANSPORT_DATA.values().forEach(Transport :: tick);

            for (UUID id : handler.TO_REMOVE)
            {
                handler.TRANSPORT_DATA.remove(id);
                handler.TRANSPORT_DATA.trim();
            }
        }
    }

    public static void essenceRenderer(final @NotNull RenderLevelStageEvent event)
    {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES)
            return;

        Vec3 cameraPos = event.getCamera().getPosition();
        CLIENT_INSTANCE.TRANSPORT_DATA.values().stream()
                .filter(trn -> !trn.getPosition().isEmpty())
                .forEach(trn -> renderTransport(event, cameraPos, trn));
    }

    private static void renderTransport(final @NotNull RenderLevelStageEvent event, @NotNull Vec3 cameraPos, @NotNull Transport trn)
    {
        float t = 1f / RenderHelper.mc().getFps();
        for (int q = 0; q < trn.position.size(); q++)
        {
            int index = q + trn.lastRoutePoint;
            if (trn.route.size() - 1 < index)
                break;
            Vec3 start = trn.position.get(q);
            Vec3 finish =  trn.route.get(index + 1);
            trn.position.set(q, start.lerp(finish, t));
        }
        trn.step++;


        List<List<Vec3>> renderMesh = RenderHelper.getCirclesAroundPoints(trn.getPosition(), 0.025f, 8, true);

        TextureAtlasSprite sprite = RenderHelper.getTexture(ESSENTIA_TEXTURE);
        IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(trn.getStack().getFluidType());
        int color = ext.getTintColor();

        int[] splitColor = RenderHelper.splitRGBA(color);

        PoseStack pose = event.getPoseStack();
        Tesselator tesselator = Tesselator.getInstance();
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);

        BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);

        pose.pushPose();
        pose.translate(-cameraPos.x(), -cameraPos.y(), -cameraPos.z());

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11. GL_ONE_MINUS_SRC_ALPHA);

        for (int q = 0; q < renderMesh.size() - 1; q++)
        {
            List<Vec3> start = renderMesh.get(q);
            List<Vec3> finish = renderMesh.get(q + 1);
            for (int z = 0; z < start.size(); z++)
            {
                Vec3 p00 = start.get(z);
                Vec3 p01 = finish.get(z);
                Vec3 p10 = start.get(z == start.size() -1 ? 0 : z + 1);
                Vec3 p11 = finish.get(z == start.size() -1 ? 0 : z + 1);

                float u0 = sprite.getU0();
                float u1 = sprite.getU1();
                float v0 = sprite.getV0();
                float v1 = sprite.getV1();

                builder.addVertex(pose.last(), (float)p00.x(), (float)p00.y(), (float)p00.z()).
                        setColor(splitColor[0], splitColor[1], splitColor[2], splitColor[3]).
                        setUv(u0, v0).
                        setLight(LightTexture.FULL_BRIGHT);
                builder.addVertex(pose.last(), (float)p10.x(), (float)p10.y(), (float)p10.z()).
                        setColor(splitColor[0], splitColor[1], splitColor[2], splitColor[3]).
                        setUv(u1, v0).
                        setLight(LightTexture.FULL_BRIGHT);
                builder.addVertex(pose.last(), (float)p11.x(), (float)p11.y(), (float)p11.z()).
                        setColor(splitColor[0], splitColor[1], splitColor[2], splitColor[3]).
                        setUv(u1, v1).
                        setLight(LightTexture.FULL_BRIGHT);
                builder.addVertex(pose.last(), (float)p01.x(), (float)p01.y(), (float)p01.z()).
                        setColor(splitColor[0], splitColor[1], splitColor[2], splitColor[3]).
                        setUv(u0, v1).
                        setLight(LightTexture.FULL_BRIGHT);
            }
        }

        MeshData data = builder.build();
        if (data != null)
            BufferUploader.drawWithShader(data);
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.defaultBlendFunc();
        pose.popPose();

    }

    public static Object2ObjectLinkedOpenHashMap<UUID, Transport> getTransportData(boolean isClient)
    {
        return get(isClient).TRANSPORT_DATA;
    }

    public static class Transport
    {

        public static final StreamCodec<RegistryFriendlyByteBuf, Transport> STREAM_CODEC = StreamCodec.composite(
                Codecs.UUID_STREAM_CODEC,
                Transport :: getId,
                Codecs.VEC_3_STREAM_CODEC,
                transport -> transport.getPosition().getLast(),
                ByteBufCodecs.<RegistryFriendlyByteBuf, Vec3>list().
                        apply(Codecs.VEC_3_STREAM_CODEC),
                Transport :: getRoute,
                FluidStack.OPTIONAL_STREAM_CODEC,
                Transport :: getStack,
                Transport :: new
        );

        public List<Vec3> position;
        public Level level;
        public List<Vec3> route;
        private int lastRoutePoint;
        private int step;
        private final UUID id;

        private final FluidStack stack;

        /**
        * For Client Use Only
        */
        public Transport(UUID id, Vec3 position, List<Vec3> route, FluidStack stack)
        {
            this.id = id;
            this.level = RenderHelper.mc().level;
            this.position = new LinkedList<>();
            this.position.add(position);
            this.route = route;
            this.stack = stack;
        }

        /**
         *  For Server Use Only
         */
        public Transport(Level level, Vec3 position, List<Vec3> route, FluidStack stack)
        {
            this.id = UUID.randomUUID();
            this.level = level;
            this.position = new LinkedList<>();
            this.position.add(position);
            this.route = route;
            this.stack = stack;
        }

        public void tick()
        {
            FluidTransportHandler transportHandler = get(level.isClientSide());
            if (lastRoutePoint >= route.size() - 1)
            {
                if (!level.isClientSide())
                {
                    BlockHelper.getTileEntity(level, route.get(lastRoutePoint)).
                            flatMap(blockEntity ->
                            {
                                if (blockEntity instanceof FluidTransmitterBlockEntity)
                                {
                                    BlockState state = blockEntity.getBlockState();
                                    Direction dir = state.getValue(BlockHelper.BlockProperties.FACING);
                                    return FluidHelper.getFluidHandler(level, blockEntity.getBlockPos().relative(dir));
                                }
                                return FluidHelper.getFluidHandler(blockEntity);
                            }).ifPresentOrElse(handler ->
                            {
                                int filledAmount = handler.fill(stack, IFluidHandler.FluidAction.EXECUTE);
                                if (stack.getAmount() - filledAmount > 0)
                                {
                                    createReturnedTransport(Lists.reverse(route), stack.copyWithAmount(stack.getAmount() - filledAmount));
                                }
                            }, () ->
                                    createReturnedTransport(Lists.reverse(route), stack.copy()));
                }
                transportHandler.TO_REMOVE.add(id);
                return;
            }


            int stepsPerTick = 20;
            float t = 1f / stepsPerTick;

            if (step >= stepsPerTick)
            {
                step = 0;
                lastRoutePoint++;
                if (lastRoutePoint < POINTS_PER_ESSENCE)
                    position.addFirst(route.getFirst());
                else if (lastRoutePoint >= route.size() - POINTS_PER_ESSENCE)
                   position.removeLast();
            }

            if (!level.isClientSide())
            {
                for (int q = 0; q < position.size(); q++)
                {
                    int index = q + lastRoutePoint;
                    if (route.size() - 1 < index)
                        break;
                    Vec3 start = position.get(q);
                    Vec3 finish = route.get(index + 1);
                    position.set(q, start.lerp(finish, t));
                }
                step++;
            }
        }

        private void createReturnedTransport(List<Vec3> route, FluidStack fluidStack)
        {
            FluidTransportHandler.Transport tsr = new FluidTransportHandler.Transport(this.level, route.getFirst(), route, fluidStack);

            FluidTransportHandler.getTransportData(false).putIfAbsent(tsr.getId(), tsr);
            PacketDistributor.sendToAllPlayers(new S2CPacketCreateFluidTransport(tsr));
        }

        public CompoundTag saveTransport()
        {
            CompoundTag tag = new CompoundTag();

            return tag;
        }

        public void readCompound(CompoundTag tag)
        {

        }

        public void sendSyncPacket()
        {

        }

        public void receiveSyncPacket()
        {

        }

        public FluidStack getStack()
        {
            return stack;
        }

        public UUID getId()
        {
            return id;
        }

        public List<Vec3> getPosition()
        {
            return position;
        }

        public List<Vec3> getRoute()
        {
            return route;
        }
    }
}
