package net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.goals;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackKeyframe;
import io.redspace.ironsspellbooks.entity.mobs.wizards.GenericAnimatedWarlockAttackGoal;
import net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.RoaringHarbingerBoss;
import net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.keyframes.RedCleaveKeyFrame;
import net.acetheeldritchking.roaring_knight_iss.particle.RedCleaveParticleOptions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class RoaringHarbingerAttackGoal extends GenericAnimatedWarlockAttackGoal<RoaringHarbingerBoss> {
    final RoaringHarbingerBoss boss;

    public RoaringHarbingerAttackGoal(RoaringHarbingerBoss entity, double pSpeedModifier, int minAttackInterval, int maxAttackInterval) {
        super(entity, pSpeedModifier, minAttackInterval, maxAttackInterval);
        this.boss = entity;
    }

    @Override
    protected double movementSpeed() {
        return this.meleeMoveSpeedModifier;
    }

    @Override
    protected void doMovement(double distanceSquared) {
        double speed = (boss.isCasting() ? 0.75F : 1F) * movementSpeed();
        boss.lookAt(target, 30, 30);
        var meleeRange = meleeRange();
        float strafeMultiplier = getStrafeMultiplier();

        if (distanceSquared < spellcastingRangeSqr && seeTime >= 5)
        {
            boss.getNavigation().stop();
            if (++strafeTime > 40)
            {
                if (boss.getRandom().nextDouble() < 0.08D)
                {
                    strafingClockwise = !strafingClockwise;
                    strafeTime = 0;
                }
            }

            float strafeForward = meleeMoveSpeedModifier;

            if (distanceSquared > meleeRange * meleeRange * 3 * 3)
            {
                strafeForward *= 2F;
            } else if (distanceSquared > meleeRange * meleeRange * 0.75F * 0.75F)
            {
                strafeForward *= 1.3F;
            } else
            {
                strafeForward *= -1.15F;
            }

            int strafeDir = strafingClockwise ? 1 : -1;
            boss.getMoveControl().strafe(strafeForward * strafeMultiplier, (float) (speed * strafeDir * strafeMultiplier));
        } else
        {
            if (boss.tickCount % 5 == 0)
            {
                boss.setXxa(0);
                boss.getNavigation().moveTo(this.target, speedModifier);
            }
        }
    }

    public void setTarget(LivingEntity target) {
        this.target = target;
    }

    @Override
    protected void onHitFrame(AttackKeyframe attackKeyframe, float meleeRange) {
        super.onHitFrame(attackKeyframe, meleeRange);
        // Red Cleave
        if (attackKeyframe instanceof RedCleaveKeyFrame apostleKeyFrame)
        {
            boolean mirrored = apostleKeyFrame.swingData.mirrored();
            boolean vertical = apostleKeyFrame.swingData.vertical();

            Vec3 forward = boss.getForward();
            float reach = 2 * boss.getScale();
            Vec3 hitLoc = boss.getBoundingBox().getCenter().add(boss.getForward().multiply(reach, 0.5F, reach));
            MagicManager.spawnParticles(boss.level(),
                    new RedCleaveParticleOptions((float) forward.x, (float) forward.y, (float) forward.z, mirrored, vertical, mob.getScale()), hitLoc.x, hitLoc.y, hitLoc.z, 1, 0, 0, 0, 0, true);
        }
    }

    @Override
    protected void handleAttackLogic(double distanceSquared) {
        super.handleAttackLogic(distanceSquared);

        if (meleeAnimTimer > 0 && currentAttack != null)
        {
            int shortcut = 5;
            if (meleeAnimTimer < shortcut)
            {
                if (currentAttack.attacks.keySet().intStream().noneMatch(i -> i > currentAttack.lengthInTicks - shortcut))
                {
                    meleeAnimTimer = 0;
                }
            }
        }
    }
}
