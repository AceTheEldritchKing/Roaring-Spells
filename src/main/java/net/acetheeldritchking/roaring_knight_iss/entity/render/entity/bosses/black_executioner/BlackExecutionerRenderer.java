package net.acetheeldritchking.roaring_knight_iss.entity.render.entity.bosses.black_executioner;

import com.mojang.blaze3d.vertex.PoseStack;
import net.acetheeldritchking.roaring_knight_iss.TheRoaringSpellbooks;
import net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.RoaringHarbingerBoss;
import net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.RoaringHarbingerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BlackExecutionerRenderer extends GeoEntityRenderer<RoaringHarbingerBoss> {
    public BlackExecutionerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RoaringHarbingerModel());
    }

    @Override
    public ResourceLocation getTextureLocation(RoaringHarbingerBoss animatable) {
        return ResourceLocation.fromNamespaceAndPath(TheRoaringSpellbooks.MOD_ID, "textures/entity/harbinger_of_roaring.png");
    }

    @Override
    public void render(RoaringHarbingerBoss entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
