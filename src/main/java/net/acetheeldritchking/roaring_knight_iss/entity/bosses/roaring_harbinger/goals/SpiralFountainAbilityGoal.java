package net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.goals;

import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.goals.AnimatedActionGoal;
import net.acetheeldritchking.roaring_knight_iss.TheRoaringSpellbooks;
import net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.RoaringHarbingerBoss;
import net.acetheeldritchking.roaring_knight_iss.utils.RKUtils;

public class SpiralFountainAbilityGoal extends AnimatedActionGoal<RoaringHarbingerBoss> {
    public SpiralFountainAbilityGoal(RoaringHarbingerBoss mob) {
        super(mob);
    }

    @Override
    protected boolean canStartAction() {
        return mob.getTarget() != null && mob.onGround();
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
        return 100;
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
        TheRoaringSpellbooks.LOGGER.debug("GO INTO SPIRAL FOUNTAIN GOAL");

        RKUtils.spawnFountainSpireWindmill(5, 3, 1.5, 0.75, 0.5, mob, mob.level(), 5, false);
    }
}
