package net.acetheeldritchking.roaring_knight_iss.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;

public class UniqueRarityColorHelper {
    // From L_Ender's Cataclysm
    // https://github.com/lender544/new1.20.1/blob/1.21/src/main/java/com/github/L_Ender/cataclysm/client/CustomRarity/CMRarity.java
    public static int getPulsingColor(long cycle,int color1,int color2) {
        float progress = (float)(Math.sin((System.currentTimeMillis() % cycle) / (double)cycle * 2.0 * Math.PI) + 1.0) / 2.0f;

        int R1 = (color1 >> 16) & 0xFF;
        int G1 = (color1 >> 8) & 0xFF;
        int B1 = color1 & 0xFF;
        int R2 = (color2 >> 16) & 0xFF;
        int G2 = (color2 >> 8) & 0xFF;
        int B2 = color2 & 0xFF;


        int r = (int) Mth.lerp(progress, R1, R2);
        int g = (int) Mth.lerp(progress, G1, G2);
        int b = (int) Mth.lerp(progress, B1, B2);

        return (r << 16) | (g << 8) | b;
    }

    public static int getPulsingBlendColor(long cycle,int color1,int color2, int blend) {
        //float progress = (float)(Math.sin((System.currentTimeMillis() % cycle) / (double)cycle * 2.0 * Math.PI) + 1.0) / 2.0f;
        float time = (float) (System.currentTimeMillis() % cycle) / cycle;
        float progress = (float) ((1.0 - Math.cos(time * 2.0 * Math.PI)) / 2.0F);

        // Color 1
        int R1 = (color1 >> 16) & 0xFF;
        int G1 = (color1 >> 8) & 0xFF;
        int B1 = color1 & 0xFF;
        // Color 2
        int R2 = (color2 >> 16) & 0xFF;
        int G2 = (color2 >> 8) & 0xFF;
        int B2 = color2 & 0xFF;
        // Blend
        int blendR = (blend >> 16) & 0xFF;
        int blendG = (blend >> 8) & 0xFF;
        int blendB = blend & 0xFF;

        int r;
        int g;
        int b;
        if (progress <= 0.5F)
        {
            float first = progress * 2.0F;
            r = (int) Mth.lerp(first, R1, blendR);
            g = (int) Mth.lerp(first, G1, blendG);
            b = (int) Mth.lerp(first, B1, blendB);
        } else
        {
            float second = progress * 2.0F;
            r = (int) Mth.lerp(second, blendR, R2);
            g = (int) Mth.lerp(second, blendG, G2);
            b = (int) Mth.lerp(second, blendB, B2);
        }
        return (r << 16) | (g << 8) | b;

        //int r = (int) Mth.lerp(progress, r1, r2);
        //int g = (int) Mth.lerp(progress, g1, g2);
        //int b = (int) Mth.lerp(progress, b1, b2);
    }
}
