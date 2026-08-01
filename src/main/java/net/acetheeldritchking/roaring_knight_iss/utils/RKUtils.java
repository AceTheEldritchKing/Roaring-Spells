package net.acetheeldritchking.roaring_knight_iss.utils;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class RKUtils {
    public static void spawnTelegraphedParticleLine(Vec3 to, Vec3 from, double spacing, Entity owner)
    {
        if (!owner.level().isClientSide)
        {
            if (owner.level() instanceof ServerLevel serverLevel)
            {
                double distance = to.distanceTo(from);

                // We hate ever shrinking numbers
                if (distance < 1.0E-4)
                {
                    return;
                }

                Vec3 direction = from.subtract(to).normalize();
                int steps = (int) Math.floor(distance / spacing);

                for (int i = 0; i <= steps; i++)
                {
                    Vec3 point = to.add(direction.scale(i * spacing));
                    serverLevel.sendParticles(ParticleTypes.SCULK_SOUL, point.x, point.y, point.z, 0, 0, 0, 0, 0);
                }
            }
        }
    }
}
