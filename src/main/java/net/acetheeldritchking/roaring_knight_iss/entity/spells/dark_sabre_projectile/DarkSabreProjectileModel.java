package net.acetheeldritchking.roaring_knight_iss.entity.spells.dark_sabre_projectile;

import net.acetheeldritchking.roaring_knight_iss.TheRoaringSpellbooks;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DarkSabreProjectileModel extends GeoModel<DarkSabreProjectile> {
    @Override
    public ResourceLocation getModelResource(DarkSabreProjectile darkSabreProjectile) {
        return ResourceLocation.fromNamespaceAndPath(TheRoaringSpellbooks.MOD_ID, "geo/dark_sabre_projectile.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DarkSabreProjectile darkSabreProjectile) {
        return ResourceLocation.fromNamespaceAndPath(TheRoaringSpellbooks.MOD_ID, "textures/entity/dark_sabre_projectile.png");
    }

    @Override
    public ResourceLocation getAnimationResource(DarkSabreProjectile darkSabreProjectile) {
        return ResourceLocation.fromNamespaceAndPath(TheRoaringSpellbooks.MOD_ID, "animations/dark_sabre_projectile.animation.json");
    }
}
