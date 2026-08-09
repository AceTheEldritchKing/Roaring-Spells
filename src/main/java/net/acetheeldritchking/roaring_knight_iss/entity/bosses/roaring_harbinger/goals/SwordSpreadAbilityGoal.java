package net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.goals;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.goals.AnimatedActionGoal;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.acetheeldritchking.roaring_knight_iss.TheRoaringSpellbooks;
import net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.RoaringHarbingerBoss;
import net.acetheeldritchking.roaring_knight_iss.entity.spells.dark_sabre_projectile.DarkSabreProjectile;
import net.acetheeldritchking.roaring_knight_iss.utils.RKServerConfig;

public class SwordSpreadAbilityGoal extends AnimatedActionGoal<RoaringHarbingerBoss> {
    public SwordSpreadAbilityGoal(RoaringHarbingerBoss mob) {
        super(mob);
    }

    @Override
    protected boolean canStartAction() {
        return mob.getTarget() != null && mob.distanceToSqr(mob.getTarget()) > 5 * 5;
    }

    @Override
    protected int getActionTimestamp() {
        return 29;
    }

    @Override
    protected int getActionDuration() {
        return 113;
    }

    @Override
    protected int getCooldown() {
        return Utils.random.nextIntBetweenInclusive(50, 150);
    }

    @Override
    protected String getAnimationId() {
        return "stomp_cast";
    }

    @Override
    public void tick() {
        if (mob.getTarget() != null)
        {
            mob.attackGoal.setTarget(mob.getTarget());
        }
        // Stop moving while we do this ability
        if (abilityTimer <= 69)
        {
            this.mob.getNavigation().stop();
            this.mob.lerpMotion(0, 0, 0);
        }
        super.tick();
    }

    @Override
    protected void doAction() {
        TheRoaringSpellbooks.LOGGER.debug("GO INTO SWORD SPREAD GOAL");
        TheRoaringSpellbooks.LOGGER.debug("Spread speed: " + RKServerConfig.swordSpreadSpeed);

        var target = mob.getTarget();
        if (target != null)
        {
            mob.playSound(SoundRegistry.SUMMONED_SWORDS_CAST.get(), 2.5f, Utils.random.nextIntBetweenInclusive(80, 110) * .01f);
            int delay = Utils.random.nextIntBetweenInclusive(65, 85);
            if (mob.isTitan())
            {
                DarkSabreProjectile.spawnDelayedSpreadGroup(mob.level(), mob.getTarget(), mob, 15, 4.0, delay, (float) (0.55 + 0.2F), 8);
            } else
            {
                DarkSabreProjectile.spawnDelayedSpreadGroup(mob.level(), mob.getTarget(), mob, 10, 4.0, delay, (float) 0.55, 5);
            }
        }
    }
}
