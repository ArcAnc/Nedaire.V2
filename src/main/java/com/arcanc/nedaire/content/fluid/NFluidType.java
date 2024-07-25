/**
 * @author ArcAnc
 * Created at: 18.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.content.fluid;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class NFluidType extends FluidType
{
    protected final ResourceLocation stillTexture;
    protected final ResourceLocation flowingTexture;
    protected final ResourceLocation overlayTexture;
    protected final Supplier<Integer> tintColor;
    protected final NFluidType.FogGetter fogColor;

    public NFluidType(final ResourceLocation stillTexture, final ResourceLocation flowingTexture, final ResourceLocation overlayTexture, final Supplier<Integer> tintColor, final NFluidType.FogGetter fogColor, final Properties properties)
    {
        super(properties);

        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
        this.overlayTexture = overlayTexture;
        this.tintColor = tintColor;
        this.fogColor = fogColor;
    }

    public IClientFluidTypeExtensions registerClientExtensions()
    {
        return new IClientFluidTypeExtensions()
        {
            @Override
            public @NotNull ResourceLocation getStillTexture()
            {
                return stillTexture;
            }

            @Override
            public @NotNull ResourceLocation getFlowingTexture()
            {
                return flowingTexture;
            }

            @Override
            public @Nullable ResourceLocation getOverlayTexture()
            {
                return overlayTexture;
            }

            @Override
            public int getTintColor()
            {
                return tintColor.get();
            }

            @Override
            public @NotNull Vector3f modifyFogColor(@NotNull Camera camera, float partialTick, @NotNull ClientLevel level, int renderDistance, float darkenWorldAmount, @NotNull Vector3f fluidFogColor)
            {
                return fogColor.getFog(camera, partialTick, level, renderDistance, darkenWorldAmount, fluidFogColor);
            }

            @Override
            public void modifyFogRender(@NotNull Camera camera, FogRenderer.@NotNull FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, @NotNull FogShape shape)
            {
                RenderSystem.setShaderFogStart(1f);
                RenderSystem.setShaderFogEnd(6f); // distance when the fog starts
            }
        };
    }

    public ResourceLocation getStillTexture()
    {
        return stillTexture;
    }

    public ResourceLocation getFlowingTexture()
    {
        return flowingTexture;
    }

    public Supplier<Integer> getTintColor()
    {
        return tintColor;
    }

    public ResourceLocation getOverlayTexture()
    {
        return overlayTexture;
    }

    public NFluidType.FogGetter getFogColor()
    {
        return fogColor;
    }

    public interface FogGetter
    {
        Vector3f getFog(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor);

        static Vector3f interColor(Vector3f curColor, @NotNull Vector3f targetColor, float delta)
        {
            Vector3f diff = new Vector3f();

            targetColor.sub(curColor, diff);

            if (diff.equals(0,0,0))
                return targetColor;

            diff.mul(delta, diff);
            return curColor.add(diff, new Vector3f());
        }

        static int getIntFromColor(@NotNull Vector3f color)
        {
            int R = Math.round(255 * color.x());
            int G = Math.round(255 * color.y());
            int B = Math.round(255 * color.z());

            R = (R << 16) & 0x00FF0000;
            G = (G << 8) & 0x0000FF00;
            B = B & 0x000000FF;

            return 0xFF000000 | R | G | B;
        }
    }
}