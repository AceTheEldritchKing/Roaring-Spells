package net.acetheeldritchking.roaring_knight_iss.utils;

import net.acetheeldritchking.roaring_knight_iss.particles.AfterImageParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class RKUtils {
    public static void spawnTelegraphedParticleLine(Vec3 to, Vec3 from, double spacing, Entity owner, ParticleOptions particle)
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
                    serverLevel.sendParticles(particle, point.x, point.y, point.z, 0, 0, 0, 0, 0);
                }
            }
        }
    }

    public static void spawnAfterImageParticle(float vec, int r, int g, int b, LivingEntity livingEntity)
    {
        if (livingEntity.level().isClientSide)
        {
            double theta = (livingEntity.yBodyRot) * (Math.PI / 180);
            theta += Math.PI / 2;
            double vecX = Math.cos(theta);
            double vecZ = Math.sin(theta);
            livingEntity.level().addParticle(new AfterImageParticleOptions(livingEntity.getId(), r, g, b,false,5), livingEntity.xOld + vecX * vec , livingEntity.yOld, livingEntity.zOld + vecZ * vec, 0, 0, 0);
        }
    }
}
