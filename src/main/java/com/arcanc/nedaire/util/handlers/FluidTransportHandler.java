/**
 * @author ArcAnc
 * Created at: 21.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.util.handlers;

import com.arcanc.nedaire.content.block.block_entity.FluidTransmitterBlockEntity;
import com.arcanc.nedaire.content.nerwork.messages.NetworkEngine;
import com.arcanc.nedaire.content.nerwork.messages.packets.S2CCreateFluidTransportPacket;
import com.arcanc.nedaire.util.NDatabase;
import com.arcanc.nedaire.util.helpers.*;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.stream.Stream;

public class FluidTransportHandler
{
    private static final FluidTransportHandler SERVER_INSTANCE = new FluidTransportHandler();
    private static final FluidTransportHandler CLIENT_INSTANCE = new FluidTransportHandler();

    private final Object2ObjectLinkedOpenHashMap<UUID, Transport> TRANSPORT_DATA = new Object2ObjectLinkedOpenHashMap<>();
    private final Set<UUID> TO_REMOVE = new LinkedHashSet<>();
    public static final int POINTS_PER_ESSENCE = 5;
    public static final ResourceLocation ESSENTIA_TEXTURE = NDatabase.modRL("essentia");

    private FluidTransportHandler() {}

    public static FluidTransportHandler get(boolean isClient)
    {
        return isClient ? CLIENT_INSTANCE : SERVER_INSTANCE;
    }

    public static void loadLevel(final LevelEvent.@NotNull Load event)
    {
        LevelAccessor levelAccessor = event.getLevel();
        if (levelAccessor.isClientSide())
            return;

        ServerLevel level = (ServerLevel) levelAccessor;
        Object2ObjectLinkedOpenHashMap<UUID, Transport> transportData = getTransportData(false);
        if (level.dimension().equals(Level.OVERWORLD))
        {
            transportData.clear();
            transportData.trim();
        }
        FluidTransportSavedData data = FluidTransportSavedData.getInstance(level);
        data.getSavedInfo().forEach(transportData::putIfAbsent);
    }

    public static void unloadLevel(final LevelEvent.@NotNull Unload event)
    {
        if (event.getLevel().isClientSide())
        {
            Object2ObjectLinkedOpenHashMap<UUID, Transport> transportData = getTransportData(true);
            transportData.clear();
            transportData.trim();
        }
    }

    public static void saveLevel(final LevelEvent.@NotNull Save event)
    {
        LevelAccessor levelAccessor = event.getLevel();
        ServerLevel level = (ServerLevel) levelAccessor;

        FluidTransportSavedData.getInstance(level).setDirty();

    }

    public static void playerLoad (final @NotNull EntityJoinLevelEvent event)
    {
        Level level = event.getLevel();
        if (!level.isClientSide())
        {
            Object2ObjectLinkedOpenHashMap<UUID, Transport> transportData = getTransportData(false);
            transportData.forEach((uuid, transport) -> NetworkEngine.sendToAllClients(new S2CCreateFluidTransportPacket(transport)));
        }
    }

    public static void levelTickEvent(final LevelTickEvent.@NotNull Pre event)
    {
        FluidTransportHandler handler = FluidTransportHandler.get(event.getLevel().isClientSide());
        if (event.hasTime())
        {
            handler.TRANSPORT_DATA.values().forEach(transport -> transport.tick(event.getLevel()));

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
                ByteBufCodecs.<RegistryFriendlyByteBuf, Vec3>list().
                        apply(Codecs.VEC_3_STREAM_CODEC),
                Transport::getPosition,
                ByteBufCodecs.<RegistryFriendlyByteBuf, Vec3>list().
                        apply(Codecs.VEC_3_STREAM_CODEC),
                Transport :: getRoute,
                FluidStack.OPTIONAL_STREAM_CODEC,
                Transport :: getStack,
                ByteBufCodecs.INT,
                Transport :: getStep,
                ByteBufCodecs.INT,
                Transport :: getLastRoutePoint,
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
        public Transport(UUID id, List<Vec3> position, List<Vec3> route, FluidStack stack, int step, int lastRoutePoint)
        {
            this.id = id;
            this.level = RenderHelper.mc().level;
            this.position = position;
            this.route = route;
            this.stack = stack;
            this.step = step;
            this.lastRoutePoint = lastRoutePoint;
        }

        /**
         *  For Server Use Only
         */
        public Transport(Level level, List<Vec3> position, List<Vec3> route, FluidStack stack)
        {
            this.id = UUID.randomUUID();
            this.level = level;
            this.position = position;
            this.route = route;
            this.stack = stack;
        }

        private Transport (@NotNull Level level, @NotNull CompoundTag tag)
        {
            this.id = tag.getUUID(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.FluidTransmitter.FluidTransport.ID);
            this.level = level;
            this.step = tag.getInt(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.FluidTransmitter.FluidTransport.STEP);
            this.lastRoutePoint = tag.getInt(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.FluidTransmitter.FluidTransport.LAST_ROUTE_POINT);
            this.stack = FluidStack.OPTIONAL_CODEC.parse(NbtOps.INSTANCE, tag.get(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.FluidTransmitter.FluidTransport.FLUID)).
                    result().
                    orElse(FluidStack.EMPTY);
            this.position = new LinkedList<>();
            ListTag pos = tag.getList(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.FluidTransmitter.FluidTransport.POSITIONS, Tag.TAG_COMPOUND);
            pos.forEach(pt ->
            {
                if (pt instanceof CompoundTag posTag)
                    this.position.add(TagHelper.readVec3(posTag));
            });

            this.route = new LinkedList<>();
            ListTag routeList = tag.getList(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.FluidTransmitter.FluidTransport.ROUTE, Tag.TAG_COMPOUND);
            routeList.forEach(rt ->
            {
                if (rt instanceof CompoundTag routeTag)
                    this.route.add(TagHelper.readVec3(routeTag));
            });
        }

        public void tick(@NotNull Level ticker)
        {
            if (!level.dimension().equals(ticker.dimension()))
                return;
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

            for (int q = 0; q < position.size(); q++)
            {
                int index = q + lastRoutePoint;
                if (route.size() - 2 < index)
                    break;
                Vec3 start = position.get(q);
                Vec3 finish = route.get(index + 1);
                position.set(q, start.lerp(finish, t));
            }
            step++;
        }

        private void createReturnedTransport(List<Vec3> route, FluidStack fluidStack)
        {
            FluidTransportHandler.Transport tsr = new FluidTransportHandler.Transport(this.level, Stream.of(route.getFirst()).
                    collect(Codecs.linkedListCollector()), route, fluidStack);

            FluidTransportHandler.getTransportData(false).putIfAbsent(tsr.getId(), tsr);
            NetworkEngine.sendToAllClients(new S2CCreateFluidTransportPacket(tsr));
        }

        public CompoundTag saveTransport()
        {
            CompoundTag tag = new CompoundTag();

            tag.putUUID(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.FluidTransmitter.FluidTransport.ID, id);
            tag.putInt(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.FluidTransmitter.FluidTransport.STEP, step);
            tag.putInt(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.FluidTransmitter.FluidTransport.LAST_ROUTE_POINT, lastRoutePoint);
            ListTag routeList = new ListTag();
            for (Vec3 rt : route)
            {
                routeList.add(TagHelper.writeVec3(rt));
            }
            tag.put(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.FluidTransmitter.FluidTransport.ROUTE, routeList);

            ListTag posList = new ListTag();
            for (Vec3 pos : position)
            {
                posList.add(TagHelper.writeVec3(pos));
            }
            tag.put(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.FluidTransmitter.FluidTransport.POSITIONS, posList);
            Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, level.dimension()).
                    ifSuccess(savedTag -> tag.put(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.FluidTransmitter.FluidTransport.LEVEL, savedTag));

            FluidStack.OPTIONAL_CODEC.encodeStart(NbtOps.INSTANCE, stack).
                    ifSuccess(savedTag -> tag.put(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.FluidTransmitter.FluidTransport.FLUID, savedTag));
            return tag;
        }

        public static @NotNull Transport readCompound(Level level, CompoundTag tag)
        {
            return new Transport(level, tag);
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

        public int getStep()
        {
            return step;
        }

        public int getLastRoutePoint()
        {
            return lastRoutePoint;
        }
    }

    public static class FluidTransportSavedData extends SavedData
    {
        private static final String FILE_NAME = NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.FluidTransmitter.FluidTransport.FLUID_TRANSPORT;

        private final ServerLevel level;
        private final Object2ObjectLinkedOpenHashMap<UUID, Transport> savedInfo = new Object2ObjectLinkedOpenHashMap<>();

        public FluidTransportSavedData(ServerLevel level)
        {
            this.level = level;
            setDirty();
        }

        public static @NotNull FluidTransportSavedData getInstance(@NotNull ServerLevel level)
        {
            return level.getDataStorage().computeIfAbsent(new Factory<>(
                            () -> new FluidTransportSavedData(level),
                            (tag, provider) -> FluidTransportSavedData.load(level, tag)),
                    FILE_NAME);
        }

        public Object2ObjectLinkedOpenHashMap<UUID, Transport> getSavedInfo()
        {
            return savedInfo;
        }

        @Override
        public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries)
        {
            ListTag listTag = new ListTag();
            FluidTransportHandler.get(false).TRANSPORT_DATA.values().
                    forEach(transport ->
                    {
                        if (level.dimension().equals(transport.level.dimension()))
                            listTag.add(transport.saveTransport());
                    });

            tag.put(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.FluidTransmitter.FluidTransport.FLUID_TRANSPORT, listTag);
            return tag;
        }

        public static @NotNull FluidTransportSavedData load(@NotNull ServerLevel level, @NotNull CompoundTag tag)
        {
            FluidTransportSavedData data = new FluidTransportSavedData(level);

            ListTag tags = tag.getList(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.FluidTransmitter.FluidTransport.FLUID_TRANSPORT, Tag.TAG_COMPOUND);
            tags.forEach(dynTag ->
            {
                if (dynTag instanceof CompoundTag compoundTag)
                {
                    ResourceKey<Level> resourceKey = Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, compoundTag.get(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.FluidTransmitter.FluidTransport.LEVEL)).
                            result().
                            orElse(null);

                    if (level.dimension().equals(resourceKey))
                    {
                        Transport trn = Transport.readCompound(level, compoundTag);
                        data.savedInfo.putIfAbsent(trn.getId(), trn);
                    }
                }
            });

            data.setDirty();
            return data;
        }
    }
}
