package net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.goals;

import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.goals.AnimatedActionGoal;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.acetheeldritchking.roaring_knight_iss.TheRoaringSpellbooks;
import net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.RoaringHarbingerBoss;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ExtremeSlashAbilityGoal extends AnimatedActionGoal<RoaringHarbingerBoss> {
    public ExtremeSlashAbilityGoal(RoaringHarbingerBoss mob) {
        super(mob);
    }

    @Override
    protected boolean canStartAction() {
        // Do it if the mob is close to us, so we can get them out of our face
        return mob.getTarget() != null && mob.distanceToSqr(mob.getTarget()) < 7 * 7;
    }

    @Override
    protected int getActionTimestamp() {
        return 23;
    }

    @Override
    protected int getActionDuration() {
        return 47;
    }

    @Override
    protected int getCooldown() {
        return Utils.random.nextIntBetweenInclusive(245, 395);
        //return Utils.random.nextIntBetweenInclusive(50, 100);
    }

    @Override
    protected String getAnimationId() {
        return "extreme_slash";
    }

    @Override
    public void tick() {
        if (abilityTimer <= 22)
        {
            this.mob.getNavigation().stop();
            this.mob.lerpMotion(0, 0, 0);

            ASUtils.spawnParticlesInCircle(16, 5.5F, 0.8F, 0.1F, mob, ParticleHelper.ENDER_SPARKS);
        }

        if (abilityTimer > 20 && abilityTimer <= 22)
        {
            mob.playSound(SoundRegistry.DIVINE_SMITE_WINDUP.get(), 2.5f, Utils.random.nextIntBetweenInclusive(80, 110) * .01f);
        }

        super.tick();
    }

    @Override
    protected void doAction() {
        TheRoaringSpellbooks.LOGGER.debug("GO INTO EXTREME SLASH GOAL");

        float radius = 10.25f;

        mob.playSound(SoundRegistry.DIVINE_SMITE_CAST.get(), 2.5f, Utils.random.nextIntBetweenInclusive(80, 110) * .01f);

        List<LivingEntity> entitiesNearby = mob.level().getEntitiesOfClass(LivingEntity.class, mob.getBoundingBox().inflate(radius * 2));

        for (LivingEntity targets : entitiesNearby)
        {
            var damageSource = new DamageSource(DamageSources.getHolderFromResource(targets, DamageTypes.MAGIC), mob);
            if (targets != mob)
            {
                if (DamageSources.applyDamage(targets, 125, damageSource)) {
                    MagicManager.spawnParticles(mob.level(), ParticleHelper.FIERY_SMOKE, targets.getX(), targets.getY() + targets.getBbHeight() * .5f, targets.getZ(), 30, targets.getBbWidth() * .5f, targets.getBbHeight() * .5f, targets.getBbWidth() * .5f, .03, false);
                }
            }
        }

        CameraShakeManager.addCameraShake(new CameraShakeData(mob.level(), 20, mob.position(), radius));
    }
}
