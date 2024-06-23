/**
 * @author ArcAnc
 * Created at: 23.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.gui.sync;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/*This code was taken from Immersive Engineering. Thanks BluSunrize. But was refactored too*/
public class GenericDataSerializers
{
    private static final List<DataSerializer<?>> SERIALIZERS = new ArrayList<>();
    public static final DataSerializer<Integer> INT32 = register(
            FriendlyByteBuf :: readVarInt, FriendlyByteBuf :: writeVarInt
    );
    public static final DataSerializer<FluidStack> FLUID_STACK = register(
            FluidStack.OPTIONAL_STREAM_CODEC :: decode, FluidStack.OPTIONAL_STREAM_CODEC :: encode,
            FluidStack :: copy, FluidStack :: matches
    );
    public static final DataSerializer<Boolean> BOOLEAN = register(
            FriendlyByteBuf :: readBoolean, FriendlyByteBuf :: writeBoolean
    );
    public static final DataSerializer<Float> FLOAT = register(
            FriendlyByteBuf :: readFloat, FriendlyByteBuf :: writeFloat
    );
    // Allows items to be synced without requiring a slot
    public static final DataSerializer<ItemStack> ITEM_STACK = register(
            ItemStack.OPTIONAL_STREAM_CODEC :: decode, ItemStack.OPTIONAL_STREAM_CODEC :: encode,
            ItemStack :: copy, ItemStack :: matches
    );
    public static final DataSerializer<byte[]> BYTE_ARRAY = register(
            buf -> FriendlyByteBuf.readByteArray(buf), (buf, bytes) -> FriendlyByteBuf.writeByteArray(buf, bytes),
            arr -> Arrays.copyOf(arr, arr.length), Arrays::equals
    );
    public static final DataSerializer<List<FluidStack>> FLUID_STACKS = register(
            fbb -> fbb.readList(pBuffer -> FluidStack.OPTIONAL_STREAM_CODEC.decode((RegistryFriendlyByteBuf) pBuffer)),
            (fbb, stacks) -> fbb.writeCollection(stacks, (pBuffer, pValue) -> FluidStack.OPTIONAL_STREAM_CODEC.encode((RegistryFriendlyByteBuf) pBuffer, pValue)),
            l -> l.stream().map(FluidStack::copy).toList(),
            (l1, l2) -> {
                if(l1.size()!=l2.size())
                    return false;
                for(int i = 0; i < l1.size(); ++i)
                    if(!FluidStack.matches(l1.get(i), l2.get(i)))
                        return false;
                return true;
            }
    );
    public static final DataSerializer<List<String>> STRINGS = register(
            fbb -> fbb.readList(FriendlyByteBuf::readUtf), (fbb, list) -> fbb.writeCollection(list, FriendlyByteBuf::writeUtf),
            ArrayList::new, List::equals
    );

    private static <T> @NotNull DataSerializer<T> register(
            Function<RegistryFriendlyByteBuf, T> read, BiConsumer<RegistryFriendlyByteBuf, T> write
    )
    {
        return register(read, write, t -> t, Objects::equals);
    }

    private static <T> @NotNull DataSerializer<T> register(
            Function<RegistryFriendlyByteBuf, T> read, BiConsumer<RegistryFriendlyByteBuf, T> write,
            UnaryOperator<T> copy, BiPredicate<T, T> equals
    )
    {
        DataSerializer<T> serializer = new DataSerializer<>(read, write, copy, equals, SERIALIZERS.size());
        SERIALIZERS.add(serializer);
        return serializer;
    }

    public static @NotNull DataPair<?> read(@NotNull RegistryFriendlyByteBuf buffer)
    {
        DataSerializer<?> serializer = SERIALIZERS.get(buffer.readVarInt());
        return serializer.read(buffer);
    }

    public record DataSerializer<T>(
            Function<RegistryFriendlyByteBuf, T> read,
            BiConsumer<RegistryFriendlyByteBuf, T> write,
            UnaryOperator<T> copy,
            BiPredicate<T, T> equals,
            int id
    )
    {
        public @NotNull DataPair<T> read(RegistryFriendlyByteBuf from)
        {
            return new DataPair<>(this, read().apply(from));
        }
    }

    public record DataPair<T>(DataSerializer<T> serializer, T data)
    {
        public void write(@NotNull RegistryFriendlyByteBuf to)
        {
            to.writeVarInt(serializer.id());
            serializer.write().accept(to, data);
        }
    }
}
