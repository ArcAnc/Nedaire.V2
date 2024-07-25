/**
 * @author ArcAnc
 * Created at: 26.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.fluid;

import com.arcanc.nedaire.api.EnergonType;
import com.arcanc.nedaire.util.helpers.RenderHelper;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class NEnergonFluidType extends NFluidType
{
    private final EnergonType energonType;

    public NEnergonFluidType(ResourceLocation stillTexture, ResourceLocation flowingTexture, ResourceLocation overlayTexture, @NotNull EnergonType type, Properties properties)
    {
        super(stillTexture, flowingTexture, overlayTexture, () -> 0, (camera, partialTick, level, renderDistance, darkenWorldAmount, fluidFogColor) -> new Vector3f(0,0,0), properties);
        this.energonType = type;
    }

    public IClientFluidTypeExtensions registerClientExtensions() {
        return new IClientFluidTypeExtensions() {
            @Override
            public @NotNull ResourceLocation getStillTexture() {
                return stillTexture;
            }

            @Override
            public @NotNull ResourceLocation getFlowingTexture() {
                return flowingTexture;
            }

            @Override
            public @Nullable ResourceLocation getOverlayTexture() {
                return overlayTexture;
            }

            @Override
            public int getTintColor() {
                return energonType.color();
            }

            @Override
            public @NotNull Vector3f modifyFogColor(@NotNull Camera camera, float partialTick, @NotNull ClientLevel level, int renderDistance, float darkenWorldAmount, @NotNull Vector3f fluidFogColor) {
                int[] color = RenderHelper.splitRGBA(energonType.color());
                return new Vector3f(color[0], color[1], color[2]);
            }

            @Override
            public void modifyFogRender(@NotNull Camera camera, FogRenderer.@NotNull FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, @NotNull FogShape shape) {
                RenderSystem.setShaderFogStart(1f);
                RenderSystem.setShaderFogEnd(6f); // distance when the fog starts
            }
        };
    }

    public EnergonType getEnergonType()
    {
        return energonType;
    }
}
