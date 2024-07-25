/**
 * @author ArcAnc
 * Created at: 10.07.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.nedaire.util.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Revived from 1.14
 * @author Mojang
 * <p>
 * Thanks tterrag!
 * <p>
 * Rewrited for 1.21 reality. Now original code not worked as needed
 * <p></p>
 * FIXME: add {@link net.neoforged.neoforge.client.model.QuadTransformers quadTranfomer} for UV
 */

public class RetexturedBakedQuad extends BakedQuad
{

    private final TextureAtlasSprite texture;

    public RetexturedBakedQuad(@NotNull BakedQuad quad, TextureAtlasSprite textureIn)
    {
        super(Arrays.copyOf(quad.getVertices(), quad.getVertices().length), quad.getTintIndex(), FaceBakery.calculateFacing(quad.getVertices()), quad.getSprite(), quad.isShade(), quad.hasAmbientOcclusion());
        this.texture = textureIn;
        this.remapQuad();

    }

    private void remapQuad()
    {
        for (int i = 0; i < 4; ++i)
        {
            int j = i * IQuadTransformer.STRIDE + IQuadTransformer.UV0;

            float u = Float.intBitsToFloat(vertices[j]);
            float v = Float.intBitsToFloat(vertices[j+1]);

            this.vertices[j] = Float.floatToRawIntBits(this.texture.getU(getUnInterpolatedU(this.sprite, u)));
            this.vertices[j + 1] = Float.floatToRawIntBits(this.texture.getV(getUnInterpolatedV(this.sprite, v)));
        }
    }

    @Override
    public @NotNull TextureAtlasSprite getSprite()
    {
        return texture;
    }

    private static float getUnInterpolatedU(@NotNull TextureAtlasSprite sprite, float u)
    {
        float f = sprite.getU1() - sprite.getU0();
        return (u - sprite.getU0()) / f;
    }

    private static float getUnInterpolatedV(@NotNull TextureAtlasSprite sprite, float v)
    {
        float f = sprite.getV1() - sprite.getV0();
        return (v - sprite.getV0()) / f;
    }

}
