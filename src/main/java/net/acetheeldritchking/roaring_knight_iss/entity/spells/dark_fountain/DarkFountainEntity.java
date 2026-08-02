package net.acetheeldritchking.roaring_knight_iss.entity.spells.dark_fountain;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import net.acetheeldritchking.roaring_knight_iss.particles.RKParticleHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class DarkFountainEntity extends AoeEntity implements AntiMagicSusceptible {
    public DarkFountainEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void onAntiMagic(MagicData playerMagicData) {

    }

    @Override
    public void applyEffect(LivingEntity target) {

    }

    @Override
    public float getParticleCount() {
        return 1;
    }

    @Override
    public Optional<ParticleOptions> getParticle() {
        return Optional.of(RKParticleHelper.LARGE_DARK_BUBBLE);
    }
}
