package net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.keyframes;

import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackKeyframe;
import net.minecraft.world.phys.Vec3;

public class SwoonAttackKeyFrame extends AttackKeyframe {
    public record SwingData(boolean vertical, boolean mirrored)
    {
        //
    }

    final public SwingData swingData;

    public SwoonAttackKeyFrame(int timeStamp, Vec3 lungeVector, SwingData swingData) {
        this(timeStamp, lungeVector, Vec3.ZERO, swingData);
    }

    public SwoonAttackKeyFrame(int timeStamp, Vec3 lungeVector, Vec3 extraKnockback, SwingData swingData) {
        super(timeStamp, lungeVector, extraKnockback);
        this.swingData = swingData;
    }
}
