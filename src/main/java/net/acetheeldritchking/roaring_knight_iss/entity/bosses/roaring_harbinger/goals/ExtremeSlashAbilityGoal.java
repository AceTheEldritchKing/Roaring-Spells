package net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.goals;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.goals.AnimatedActionGoal;
import net.acetheeldritchking.roaring_knight_iss.TheRoaringSpellbooks;
import net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.RoaringHarbingerBoss;
import net.minecraft.world.damagesource.DamageSource;

public class ExtremeSlashAbilityGoal extends AnimatedActionGoal<RoaringHarbingerBoss> {
    public ExtremeSlashAbilityGoal(RoaringHarbingerBoss mob) {
        super(mob);
    }

    @Override
    protected boolean canStartAction() {
        // Do it if the mob is close to us, so we can get them out of our face
        return mob.onGround() && mob.getTarget() != null && mob.distanceToSqr(mob.getTarget()) < 7 * 7;
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
        }

        super.tick();
    }

    @Override
    protected void doAction() {
        TheRoaringSpellbooks.LOGGER.debug("GO INTO EXTREME SLASH GOAL");

        mob.getTarget().hurt(new DamageSource(mob.level().damageSources().damageTypes.getHolderOrThrow(ISSDamageTypes.ELDRITCH_MAGIC), mob), 25);
    }
}
