package net.acetheeldritchking.roaring_knight_iss.entity.render.spells;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.render.RenderHelper;
import io.redspace.ironsspellbooks.render.SpellRenderingHelper;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.acetheeldritchking.roaring_knight_iss.entity.spells.dark_fountain.DarkFountainSpireEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class DarkFountainSpireRenderer extends EntityRenderer<DarkFountainSpireEntity> {
    public DarkFountainSpireRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRender(DarkFountainSpireEntity livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public void render(DarkFountainSpireEntity spire, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        float maxRadius = spire.isFullFountain ? 6.5F : 2.5F;
        float minRadius = 0.005F;

        float deltaTicks = spire.tickCount + partialTick;
        float deltaUV = -deltaTicks % 10;
        float max = Mth.frac(deltaUV * 0.2F - Mth.floor(deltaUV * 0.1F));
        float min = -1.0F + max;
        float f = deltaTicks / DarkFountainSpireEntity.WARMUP_TIME;
        f *= f;
        float radius = Mth.clampedLerp(maxRadius, minRadius, f);
        VertexConsumer innerSpire = bufferSource.getBuffer(RenderHelper.CustomerRenderType.magic(SpellRenderingHelper.BEACON));
        float halfRadius = radius * 0.5F;
        float quarterRadius = halfRadius * 0.5F;
        float yMin = spire.onGround() ? 0 : (float) (Utils.findRelativeGroundLevel(spire.level(), spire.position(), 8) - spire.getY());

        for (int i = 0; i < 4; i++)
        {
            // White outline
            RenderHelper.quadBuilder()
                    .vertex(-halfRadius, yMin, -halfRadius).uv(0, min).normal(0, 1, 0)
                    .vertex(-halfRadius, yMin, halfRadius).uv(1, min).normal(0, 1, 0)
                    .vertex(-halfRadius, 250, halfRadius).uv(1, max).normal(0, 1, 0)
                    .vertex(-halfRadius, 250, -halfRadius).uv(0, max).normal(0, 1, 0)
                    // F0F0F5
                    .color(Mth.clamp(1.0f * f, 0, 1), Mth.clamp(.0f * f * f, 0, 1), Mth.clamp(1.0f * f * f, 0, 1))
                    //.color(Mth.clamp(0.941F * f, 0, 1), Mth.clamp(0.941F * f * f, 0, 1), Mth.clamp(0.961F * f * f, 0, 1))
                    .light(LightTexture.FULL_BRIGHT)
                    .overlay(OverlayTexture.NO_OVERLAY)
                    .matrix(poseStack.last().pose())
                    .build(innerSpire);
            // Dark, darker, yet darker
            RenderHelper.quadBuilder()
                    .vertex(-quarterRadius, yMin, -quarterRadius).uv(0, min).normal(0, 1, 0)
                    .vertex(-quarterRadius, yMin, quarterRadius).uv(1, min).normal(0, 1, 0)
                    .vertex(-quarterRadius, 250, quarterRadius).uv(1, max).normal(0, 1, 0)
                    .vertex(-quarterRadius, 250, -quarterRadius).uv(0, max).normal(0, 1, 0)
                    // 03000F
                    .color(Mth.clamp(.0F * f, 0, 1), Mth.clamp(.0F * f, 0, 1), Mth.clamp(1.0F * f * f, 0, 1))
                    //.color(Mth.clamp(0.012F * f, 0, 1), Mth.clamp(0.0F * f, 0, 1), Mth.clamp(0.059F * f * f, 0, 1))
                    .light(LightTexture.FULL_BRIGHT)
                    .overlay(OverlayTexture.NO_OVERLAY)
                    .matrix(poseStack.last().pose())
                    .build(innerSpire);
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
        }

        poseStack.popPose();

        super.render(spire, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(DarkFountainSpireEntity darkFountainSpireEntity) {
        return SpellRenderingHelper.BEACON;
    }
}
