package net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.goals;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.goals.AnimatedActionGoal;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.acetheeldritchking.roaring_knight_iss.TheRoaringSpellbooks;
import net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.RoaringHarbingerBoss;
import net.minecraft.world.phys.Vec3;

public class PursuitAbilityGoal extends AnimatedActionGoal<RoaringHarbingerBoss> {
    public PursuitAbilityGoal(RoaringHarbingerBoss mob) {
        super(mob);
    }

    @Override
    protected boolean canStartAction() {
        // Do this ONLY if the target is too far away, we don't want runners
        return mob.getTarget() != null && mob.distanceToSqr(mob.getTarget()) > 12 * 12;
    }

    @Override
    protected int getActionTimestamp() {
        return 26;
    }

    @Override
    protected int getActionDuration() {
        return 44;
    }

    @Override
    protected int getCooldown() {
        return Utils.random.nextIntBetweenInclusive(50, 100);
    }

    @Override
    protected String getAnimationId() {
        return "aggressive_teleport";
    }

    @Override
    public void tick() {
        // Make sure we have a target to latch onto to pursue them
        if (mob.getTarget() != null)
        {
            mob.attackGoal.setTarget(mob.getTarget());
        }
        if (abilityTimer == 11)
        {
            // Telegraph the sound to the start of the ability
            mob.playSound(SoundRegistry.ABYSSAL_TELEPORT.get(), 2.5f, Utils.random.nextIntBetweenInclusive(80, 110) * .01f);
        }
        super.tick();
    }

    @Override
    protected void doAction() {
        TheRoaringSpellbooks.LOGGER.debug("GO INTO AGGRESSIVE PURSUIT GOAL");

        mob.playSound(SoundRegistry.ABYSSAL_TELEPORT.get(), 2.5f, Utils.random.nextIntBetweenInclusive(80, 110) * .01f);

        var target = mob.getTarget();
        if (target != null)
        {
            // Get the current target's position and setPos to there
            Vec3 targetPos = target.position();

            mob.setPos(targetPos);
        }
    }
}
