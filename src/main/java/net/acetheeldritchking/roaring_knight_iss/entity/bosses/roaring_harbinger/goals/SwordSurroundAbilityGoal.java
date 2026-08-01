package net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.goals;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.goals.AnimatedActionGoal;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.acetheeldritchking.roaring_knight_iss.TheRoaringSpellbooks;
import net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.RoaringHarbingerBoss;
import net.acetheeldritchking.roaring_knight_iss.entity.spells.dark_sabre_projectile.DarkSabreProjectile;
import net.acetheeldritchking.roaring_knight_iss.utils.RKServerConfig;

public class SwordSurroundAbilityGoal extends AnimatedActionGoal<RoaringHarbingerBoss> {
    public SwordSurroundAbilityGoal(RoaringHarbingerBoss mob) {
        super(mob);
    }

    @Override
    protected boolean canStartAction() {
        return mob.getTarget() != null && mob.distanceToSqr(mob.getTarget()) > 4 * 4;
    }

    @Override
    protected int getActionTimestamp() {
        return 25;
    }

    @Override
    protected int getActionDuration() {
        return 42;
    }

    @Override
    protected int getCooldown() {
        return Utils.random.nextIntBetweenInclusive(50, 150);
    }

    @Override
    protected String getAnimationId() {
        return "slash_cast";
    }

    @Override
    public void tick() {
        if (mob.getTarget() != null)
        {
            mob.attackGoal.setTarget(mob.getTarget());
        }
        super.tick();
    }

    @Override
    protected void doAction() {
        TheRoaringSpellbooks.LOGGER.debug("GO INTO SWORD SURROUND GOAL");

        var target = mob.getTarget();
        if (target != null)
        {
            mob.playSound(SoundRegistry.SUMMONED_SWORDS_CAST.get(), 2.5f, Utils.random.nextIntBetweenInclusive(80, 110) * .01f);
            int delay = Utils.random.nextIntBetweenInclusive(80, 100);
            if (mob.isTitan())
            {
                DarkSabreProjectile.spawnDelayedSurroundGroup(mob.level(), mob.getTarget(), mob, 7, 5.0, delay, (float) (RKServerConfig.swordSurroundSpeed + 0.2F), 15);
            } else
            {
                DarkSabreProjectile.spawnDelayedSurroundGroup(mob.level(), mob.getTarget(), mob, 5, 5.0, delay, (float) RKServerConfig.swordSurroundSpeed, 10);
            }
        }
    }
}
