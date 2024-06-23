/**
 * @author ArcAnc
 * Created at: 23.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.util.helpers;

import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PacketHelper
{
    public static <T> @NotNull List<T> readList(@NotNull RegistryFriendlyByteBuf buffer, Function<RegistryFriendlyByteBuf, T> readElement)
    {
        int numElements = buffer.readVarInt();
        List<T> result = new ArrayList<>(numElements);
        for(int i = 0; i < numElements; ++i)
            result.add(readElement.apply(buffer));
        return result;
    }
    public static <T> void writeList(@NotNull RegistryFriendlyByteBuf buffer, @NotNull List<T> toWrite, BiConsumer<T, RegistryFriendlyByteBuf> writeElement)
    {
        buffer.writeVarInt(toWrite.size());
        for(T element : toWrite)
            writeElement.accept(element, buffer);
    }
}
