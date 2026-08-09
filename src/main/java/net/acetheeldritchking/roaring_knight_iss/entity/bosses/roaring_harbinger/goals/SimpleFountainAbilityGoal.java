package net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.goals;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.goals.AnimatedActionGoal;
import net.acetheeldritchking.roaring_knight_iss.TheRoaringSpellbooks;
import net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.RoaringHarbingerBoss;
import net.acetheeldritchking.roaring_knight_iss.entity.spells.dark_fountain.DarkFountainSpireEntity;
import net.minecraft.world.phys.Vec3;

public class SimpleFountainAbilityGoal extends AnimatedActionGoal<RoaringHarbingerBoss> {
    public SimpleFountainAbilityGoal(RoaringHarbingerBoss mob) {
        super(mob);
    }

    @Override
    protected boolean canStartAction() {
        return true;
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
        TheRoaringSpellbooks.LOGGER.debug("GO INTO SIMPLE FOUNTAIN GOAL");

        Vec3 lookDir = mob.getLookAngle();
        Vec3 flatDir = new Vec3(lookDir.x, 0, lookDir.z).normalize();
        double offset = 3.0;
        Vec3 spawnPos = mob.position().add(flatDir.scale(offset));

        Vec3 spawn = Utils.moveToRelativeGroundLevel(mob.level(), spawnPos, 3, 18);

        // Fountain
        DarkFountainSpireEntity darkFountainSpire = new DarkFountainSpireEntity(mob.level(), false);
        darkFountainSpire.setOwner(mob);
        darkFountainSpire.moveTo(spawn);
        if (mob.isTitan())
        {
            darkFountainSpire.setDamage(30F);
        } else
        {
            darkFountainSpire.setDamage(15F);
        }

        mob.level().addFreshEntity(darkFountainSpire);
    }
}
