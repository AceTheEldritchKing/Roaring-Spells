package net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.goals;

import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.goals.AnimatedActionGoal;
import net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.RoaringHarbingerBoss;

public class RingedFountainAbilityGoal extends AnimatedActionGoal<RoaringHarbingerBoss> {
    public RingedFountainAbilityGoal(RoaringHarbingerBoss mob) {
        super(mob);
    }

    @Override
    protected boolean canStartAction() {
        return true;
    }

    @Override
    protected int getActionTimestamp() {
        return 0;
    }

    @Override
    protected int getActionDuration() {
        return 0;
    }

    @Override
    protected int getCooldown() {
        return 0;
    }

    @Override
    protected String getAnimationId() {
        return "";
    }

    @Override
    protected void doAction() {

    }
}
