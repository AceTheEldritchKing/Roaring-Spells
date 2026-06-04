package net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger;

import net.acetheeldritchking.roaring_knight_iss.TheRoaringSpellbooks;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class RoaringHarbingerModel extends GeoModel<RoaringHarbingerBoss> {
    @Override
    public ResourceLocation getModelResource(RoaringHarbingerBoss animatable) {
        return ResourceLocation.fromNamespaceAndPath(TheRoaringSpellbooks.MOD_ID, "geo/harbinger_of_roaring.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RoaringHarbingerBoss animatable) {
        return ResourceLocation.fromNamespaceAndPath(TheRoaringSpellbooks.MOD_ID, "textures/entity/harbinger_of_roaring.png");
    }

    @Override
    public ResourceLocation getAnimationResource(RoaringHarbingerBoss animatable) {
        return ResourceLocation.fromNamespaceAndPath(TheRoaringSpellbooks.MOD_ID, "animations/harbinger_of_roaring.animation.json");
    }

    @Override
    public void setCustomAnimations(RoaringHarbingerBoss animatable, long instanceId, AnimationState<RoaringHarbingerBoss> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
    }
}
