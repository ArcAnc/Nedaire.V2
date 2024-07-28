/**
 * @author ArcAnc
 * Created at: 01.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.block.block_entity;

import com.arcanc.nedaire.content.block.BlockInterfaces;
import com.arcanc.nedaire.content.block.block_entity.ticking.NServerTickableBE;
import com.arcanc.nedaire.content.gui.container_menu.FluidTransmitterContainer;
import com.arcanc.nedaire.content.nerwork.messages.packets.S2CCreateFluidTransportPacket;
import com.arcanc.nedaire.registration.NRegistration;
import com.arcanc.nedaire.util.NDatabase;
import com.arcanc.nedaire.util.filter.FilterMethod;
import com.arcanc.nedaire.util.handlers.FluidTransportHandler;
import com.arcanc.nedaire.util.helpers.BlockHelper;
import com.arcanc.nedaire.util.helpers.FluidHelper;
import com.arcanc.nedaire.util.helpers.RenderHelper;
import com.arcanc.nedaire.util.inventory.items.IInventoryCallback;
import com.arcanc.nedaire.util.inventory.items.SimpleItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FluidTransmitterBlockEntity extends RedstoneSensitiveBlockEntity implements BlockInterfaces.INWrencheable, NServerTickableBE, BlockInterfaces.INInteractionObject<FluidTransmitterBlockEntity>
{

    private final int WORK_MODIFIER = 20;
    private final List<BlockPos> attachedPoses = new ArrayList<>();
    private final int CONNECT_DISTANCE = 16;
    private FilterMethod filterMethod;
    private int prevTargetIndex;
    public static final int MAX_TRANSFER = 10000;
    public static final int MIN_TRANSFER = 1;
    private int transferAmount = 10;

    private final SimpleItemHandler handler = new SimpleItemHandler(new IInventoryCallback()
    {
        @Override
        public void onInventoryChanged(int slot)
        {
            FluidTransmitterBlockEntity.this.setChanged();
        }
    });

    public FluidTransmitterBlockEntity(BlockPos pPos, BlockState pBlockState)
    {
        super(NRegistration.NBlockEntities.BE_FLUID_TRANSMITTER.get(), pPos, pBlockState);
        filterMethod = FilterMethod.DEFAULT;

        for (int q = 0; q < 12; q++)
            handler.addSlot().setValidator(FluidHelper :: isFluidHandler).setCapacity(1).finishSlot();
    }

    private boolean canWork()
    {
        return this.getLevel().getGameTime() % WORK_MODIFIER == 0;
    }


    @Override
    public void tickServer()
    {
        boolean isEnabled = false;
        if (isPowered())
        {
            isEnabled = true;
            if (canWork())
            {
                BlockPos targetPos = BlockPos.ZERO;
                BlockPos sourcePos = BlockPos.ZERO;
                clearRemovedPoses();

                if (!attachedPoses.isEmpty())
                {
                    switch (filterMethod.route())
                    {
                        case OUTPUT, BIDIRECTION ->
                        {
                            sourcePos = getBlockPos().offset(getBlockState().getValue(BlockHelper.BlockProperties.FACING).getNormal());
                            targetPos = switch (filterMethod.target())
                            {
                                case NEAREST_FIRST ->
                                {
                                    List<BlockPos> sortedByDistance = getSortedClosestPoses();
                                    prevTargetIndex++;
                                    if (prevTargetIndex >= sortedByDistance.size())
                                        prevTargetIndex = 0;
                                    yield sortedByDistance.get(prevTargetIndex);
                                }
                                case FURTHERS_FIRST ->
                                {
                                    List<BlockPos> sortedByDistance = getSortedFurthersPoses();
                                    prevTargetIndex++;
                                    if (prevTargetIndex >= sortedByDistance.size())
                                        prevTargetIndex = 0;
                                    yield sortedByDistance.get(prevTargetIndex);
                                }
                                case RANDOM -> attachedPoses.get(getLevel().random.nextInt(attachedPoses.size()));
                                case ROUND_ROBIN ->
                                {
                                    prevTargetIndex++;
                                    if (prevTargetIndex >= attachedPoses.size())
                                        prevTargetIndex = 0;
                                    yield attachedPoses.get(prevTargetIndex);
                                }
                            };
                        }
                        case INPUT ->
                        {
                            targetPos = getBlockPos().offset(getBlockState().getValue(BlockHelper.BlockProperties.FACING).getNormal());
                            sourcePos = switch (filterMethod.target())
                            {
                                case NEAREST_FIRST ->
                                {
                                    List<BlockPos> sortedByDistance = getSortedClosestPoses();
                                    prevTargetIndex++;
                                    if (prevTargetIndex >= sortedByDistance.size())
                                        prevTargetIndex = 0;
                                    yield sortedByDistance.get(prevTargetIndex);
                                }
                                case FURTHERS_FIRST ->
                                {
                                    List<BlockPos> sortedByDistance = getSortedFurthersPoses();
                                    prevTargetIndex++;
                                    if (prevTargetIndex >= sortedByDistance.size())
                                        prevTargetIndex = 0;
                                    yield sortedByDistance.get(prevTargetIndex);
                                }
                                case RANDOM -> attachedPoses.get(getLevel().random.nextInt(attachedPoses.size()));
                                case ROUND_ROBIN ->
                                {
                                    prevTargetIndex++;
                                    if (prevTargetIndex >= attachedPoses.size())
                                        prevTargetIndex = 0;
                                    yield attachedPoses.get(prevTargetIndex);
                                }
                            };
                        }
                    }

                    if (targetPos != BlockPos.ZERO || sourcePos != BlockPos.ZERO)
                    {
                        if (FluidHelper.isFluidHandler(getLevel(), targetPos, null) && FluidHelper.isFluidHandler(getLevel(), sourcePos, null))
                        {
                            Optional<IFluidHandler> targetHandler = FluidHelper.getFluidHandler(getLevel(), targetPos);
                            Optional<IFluidHandler> sourceHandler = FluidHelper.getFluidHandler(getLevel(), sourcePos);
                            FluidStack transferStack = FluidStack.EMPTY;

                            if (FluidHelper.hasEmptySpace(targetHandler) && !FluidHelper.isEmpty(sourceHandler))
                            {
                                transferStack = sourceHandler.map(fluidHandler ->
                                {
                                    for (int q = 0; q < fluidHandler.getTanks(); q++)
                                    {
                                        FluidStack stackForTransfer = fluidHandler.getFluidInTank(q);

                                        if (filterMethod.list().test(handler, stackForTransfer) &&
                                                filterMethod.owner().test(handler,stackForTransfer) &&
                                                filterMethod.nbt().test(handler, stackForTransfer) &&
                                                filterMethod.tag().test(handler, stackForTransfer))
                                            return fluidHandler.drain(stackForTransfer.copyWithAmount(Math.min(stackForTransfer.getAmount(), transferAmount)), IFluidHandler.FluidAction.EXECUTE);
                                    }
                                    return FluidStack.EMPTY;
                                }).orElse(FluidStack.EMPTY);
                            }

                            if (!transferStack.isEmpty())
                            {
                                List<Vec3> route = RenderHelper.getSpiralAroundVector(getBlockPos().getCenter().subtract(0, 0.25f, 0), targetPos.getCenter(), 0.15f, 70, 5);
                                addFluidStackTransport(route, transferStack);
                            }
                        }
                    }
                }
            }
        }
        if (getBlockState().getValue(BlockHelper.BlockProperties.ENABLED) != isEnabled)
        {
            getLevel().setBlock(getBlockPos(), getBlockState().setValue(BlockHelper.BlockProperties.ENABLED, isEnabled), Block.UPDATE_CLIENTS);
        }
    }

    private void addFluidStackTransport(List<Vec3> route, FluidStack transferStack)
    {
        FluidTransportHandler.Transport tsr = new FluidTransportHandler.Transport(this.level, route.getFirst(), route, transferStack);

        FluidTransportHandler.getTransportData(false).putIfAbsent(tsr.getId(), tsr);
        PacketDistributor.sendToAllPlayers(new S2CCreateFluidTransportPacket(tsr));
    }

    private void clearRemovedPoses()
    {
        if (attachedPoses.isEmpty())
            return;
        Set<BlockPos> posesForDelete = new HashSet<>();
        for (BlockPos pos : attachedPoses)
        {
            if (!FluidHelper.isFluidHandler(getLevel(), pos))
                posesForDelete.add(pos);
        }

        if (!posesForDelete.isEmpty())
        {
            attachedPoses.removeAll(posesForDelete);
            markDirty();
        }

    }

    @Override
    public InteractionResult onUsed(@NotNull ItemStack stack, UseOnContext ctx)
    {
        BlockPos pos;
        if (stack.has(NRegistration.NDataComponents.POSITION))
        {
            pos = stack.getOrDefault(NRegistration.NDataComponents.POSITION, BlockPos.ZERO);
            stack.remove(NRegistration.NDataComponents.POSITION);
        }
        else
        {
            pos = BlockPos.ZERO;
        }

        if (pos != BlockPos.ZERO)
        {
            Level level = ctx.getLevel();
            return FluidHelper.getFluidHandler(level, pos).map(fluidHandler ->
            {
                if (getBlockPos().closerToCenterThan(pos.getCenter(), CONNECT_DISTANCE))
                {
                    attachedPoses.add(pos);
                    markDirty();
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
                return InteractionResult.PASS;
            }).orElse(InteractionResult.PASS);
        }
        return InteractionResult.PASS;
    }

    @Contract(pure = true)
    private @NotNull List<BlockPos> getSortedClosestPoses()
    {
        List<BlockPos> list = new ArrayList<>(attachedPoses);
        list.sort((pos1, pos2) ->
        {
            double dist1 = getBlockPos().distSqr(pos1);
            double dist2 = getBlockPos().distSqr(pos2);
            return Double.compare(dist1, dist2);
        });
        return list;
    }

    @Contract(pure = true)
    private @NotNull List<BlockPos> getSortedFurthersPoses()
    {
        List<BlockPos> list = new ArrayList<>(attachedPoses);
        list.sort((pos1, pos2) ->
        {
            double dist1 = getBlockPos().distSqr(pos1);
            double dist2 = getBlockPos().distSqr(pos2);
            return Double.compare(dist1, dist2) * -1;
        });
        return list;
    }

    public int getTransferAmount()
    {
        return transferAmount;
    }

    public void setTransferAmount(int transferAmount)
    {
        this.transferAmount = transferAmount;
    }

    public SimpleItemHandler getHandler()
    {
        return handler;
    }

    public int getCONNECT_DISTANCE()
    {
        return CONNECT_DISTANCE;
    }

    public List<BlockPos> getAttachedPoses()
    {
        return attachedPoses;
    }

    public @NotNull FilterMethod getFilterMethod() {
        return filterMethod;
    }

    public void setFilterMethod(@NotNull FilterMethod filterMethod)
    {
        this.filterMethod = filterMethod;
    }

    @Override
    public void readCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        super.readCustomTag(tag, registries, descrPacket);

        filterMethod = FilterMethod.readFromNbt(tag.getCompound(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.Filter.FILTER_TAG));

        attachedPoses.clear();
        ListTag list = tag.getList(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.FluidTransmitter.POS_LIST, Tag.TAG_LONG);
        for (Tag value : list)
        {
            LongTag longTag = (LongTag) value;
            attachedPoses.add(BlockPos.of(longTag.getAsLong()));
        }

        prevTargetIndex = tag.getInt(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.FluidTransmitter.PREV_TARGET_INDEX);
        transferAmount = tag.getInt(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.FluidTransmitter.TRANSFER_AMOUNT);
        handler.deserializeNBT(registries, tag.getCompound(NDatabase.CapabilitiesInfo.InventoryInfo.CAPABILITY_NAME));
    }

    @Override
    public void writeCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        super.writeCustomTag(tag, registries, descrPacket);

        tag.put(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.Filter.FILTER_TAG, filterMethod.writeToNbt());

        ListTag list = new ListTag();
        for (BlockPos pos : attachedPoses)
        {
            LongTag longTag = LongTag.valueOf(pos.asLong());
            list.add(longTag);
        }

        tag.put(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.FluidTransmitter.POS_LIST, list);

        tag.putInt(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.FluidTransmitter.PREV_TARGET_INDEX, prevTargetIndex);
        tag.putInt(NDatabase.BlocksInfo.BlockEntities.TagAddress.Machines.FluidTransmitter.TRANSFER_AMOUNT, transferAmount);
        tag.put(NDatabase.CapabilitiesInfo.InventoryInfo.CAPABILITY_NAME, handler.serializeNBT(registries));
    }

    @Override
    public @Nullable FluidTransmitterBlockEntity getGuiMaster()
    {
        return this;
    }

    @Override
    public NRegistration.NMenuTypes.ArgContainer<FluidTransmitterBlockEntity, FluidTransmitterContainer> getContainerType()
    {
        return NRegistration.NMenuTypes.FLUID_TRANSMITTER;
    }

    @Override
    public boolean canUseGui(Player player)
    {
        return true;
    }
}
