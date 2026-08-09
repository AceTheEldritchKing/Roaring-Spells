package net.acetheeldritchking.roaring_knight_iss.entity.spells.dark_fountain;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import net.acetheeldritchking.roaring_knight_iss.particles.RKParticleHelper;
import net.acetheeldritchking.roaring_knight_iss.registries.RKEntityRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class DarkFountainEntity extends AoeEntity implements AntiMagicSusceptible {
    public DarkFountainEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public DarkFountainEntity(Level level) {
        this(RKEntityRegistry.DARK_FOUNTAIN.get(), level);
    }

    private DamageSource damageSource;

    @Override
    public void onAntiMagic(MagicData playerMagicData) {
        // Do smth cool here one day
    }

    @Override
    public void applyEffect(LivingEntity target) {
        if (damageSource == null) {
            damageSource = new DamageSource(DamageSources.getHolderFromResource(target, ISSDamageTypes.FIRE_FIELD), this, getOwner());
        }
        if (!DamageSources.isFriendlyFireBetween(this.getOwner(), target)) {
            DamageSources.ignoreNextKnockback(target);
            if (target.hurt(damageSource, getDamage())) {
                target.setRemainingFireTicks(60);
            }
        }
    }

    @Override
    public float getParticleCount() {
        return 1 * (getRadius()/3);
    }

    @Override
    protected float particleYOffset() {
        return .05f;
    }

    @Override
    protected float getParticleSpeedModifier() {
        return 1.5f;
    }

    @Override
    public Optional<ParticleOptions> getParticle() {
        return Optional.of(RKParticleHelper.LARGE_DARK_BUBBLE);
    }
}
