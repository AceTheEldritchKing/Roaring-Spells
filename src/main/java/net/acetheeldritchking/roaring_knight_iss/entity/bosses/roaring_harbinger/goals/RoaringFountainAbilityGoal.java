package net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.goals;

import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.goals.AnimatedActionGoal;
import net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.RoaringHarbingerBoss;

public class RoaringFountainAbilityGoal extends AnimatedActionGoal<RoaringHarbingerBoss> {
    public RoaringFountainAbilityGoal(RoaringHarbingerBoss mob) {
        super(mob);
    }

    @Override
    protected boolean canStartAction() {
        return true;
    }

    @Override
    protected int getActionTimestamp() {
        return 173;
    }

    @Override
    protected int getActionDuration() {
        return 205;
    }

    @Override
    protected int getCooldown() {
        return 100;
    }

    @Override
    protected String getAnimationId() {
        return "";
    }

    @Override
    protected void doAction() {

    }
}
