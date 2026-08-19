package net.acetheeldritchking.roaring_knight_iss.utils;

import dev.chocoboy.cascade.Vfx;
import dev.chocoboy.cascade.engine.effect.BlendMode;
import dev.chocoboy.cascade.engine.effect.SpriteId;
import dev.chocoboy.cascade.engine.emitter.ShapeSpec;
import dev.chocoboy.cascade.engine.tween.Easings;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.RoaringHarbingerBoss;
import net.acetheeldritchking.roaring_knight_iss.entity.spells.dark_fountain.DarkFountainSpireEntity;
import net.acetheeldritchking.roaring_knight_iss.particles.AfterImageParticleOptions;
import net.acetheeldritchking.roaring_knight_iss.particles.RKParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

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

    public static void spawnFountainSpireWindmill(int branches, int perBranch, double initialRadius, double radiusIncrement, double curveFactor, LivingEntity caster, Level level, int spellLevel, boolean isFullFountain)
    {
        float angleIncrement = (float) (2 * Math.PI / branches);

        for (int branch = 0; branch < branches; ++branch) {
            //System.out.println("Spawn Halberds Field");
            float baseAngle = angleIncrement * branch;

            for (int i = 0; i < perBranch; ++i) {
                double currentRadius = initialRadius + i * radiusIncrement;
                float currentAngle = (float) (baseAngle + i * angleIncrement / initialRadius + (i * curveFactor));

                double offsetX = currentRadius * Math.cos(currentAngle);
                double offsetZ = currentRadius * Math.sin(currentAngle);

                double spawnX = caster.getX() + offsetX;
                double spawnY = caster.getY() + 0.3D;
                double spawnZ = caster.getZ() + offsetZ;

                double deltaX = level.random.nextGaussian() * 0.007D;
                double deltaY = level.random.nextGaussian() * 0.007D;
                double deltaZ = level.random.nextGaussian() * 0.007D;

                if (!level.isClientSide()) {
                    level.addParticle(RKParticleHelper.STAR_SPARKLE, spawnX, spawnY, spawnZ, deltaX, deltaY, deltaZ);
                }

                spawnFountainSpireDir(spawnX, spawnY, spawnZ, level, spellLevel, caster, isFullFountain);
            }
        }
    }

    public static void spawnFountainSpireDir(double x, double y, double z, Level level, int spellLevel, LivingEntity caster, boolean isFull)
    {
        BlockPos pos = new BlockPos((int) x, (int) y, (int) z);
        boolean flag = false;
        double d0 = 0.0D;

        int maxIterations = Mth.clamp(spellLevel * 4, 1, 25);
        int iterationCount = 0;

        do {
            //System.out.println("Trying to find block at pos: " + pos);

            BlockPos pos1 = pos.below();
            BlockState blockState = level.getBlockState(pos1);

            if (blockState.isFaceSturdy(level, pos1, Direction.UP)) {

                //System.out.println("Found a sturdy block at: " + pos1);

                if (!level.isEmptyBlock(pos)) {
                    BlockState blockState1 = level.getBlockState(pos);
                    VoxelShape shape = blockState1.getCollisionShape(level, pos);

                    if (!shape.isEmpty()) {
                        d0 = shape.max(Direction.Axis.Y);
                    }
                }

                flag = true;
                break;
            }

            pos = pos.below();
            iterationCount++;

        } while (pos.getY() >= Mth.floor(y) && iterationCount < maxIterations);

        if (flag)
        {
            Vec3 spawnPos = new Vec3(x, y, z);
            Vec3 spawn = Utils.moveToRelativeGroundLevel(caster.level(), spawnPos, 3, 18);
            DarkFountainSpireEntity darkFountainSpire = new DarkFountainSpireEntity(caster.level(), spawn.x, spawn.y + d0, spawn.z, isFull);
            darkFountainSpire.setOwner(caster);
            if (caster instanceof RoaringHarbingerBoss boss)
            {
                if (boss.isTitan())
                {
                    darkFountainSpire.setDamage(30F);
                } else
                {
                    darkFountainSpire.setDamage(15F);
                }
            } else
            {
                darkFountainSpire.setDamage(15F);
            }

            caster.level().addFreshEntity(darkFountainSpire);
        }
    }

    private static int BRIGHT_COLOR = 0xB36BFF;
    private static int ACCENT_COLOR = 0x4C30BA;
    private static int DARK_COLOR = 0x080E45;
    private static int BASE_COLOR = 0xC357EB;
    private static final int WHITE = 0xFFFFFF;

    // This is just Gravity Star from Cascade with a few adjustments really
    public static void gravityStar(ServerLevel level, Vec3 pos)
    {
        Vfx.at(level)
                .parallel(
                        // the zone itself: a real translucent dome mesh, not a cloud of particles
                        s -> s.dome(pos, 4.5f, BRIGHT_COLOR, 200),
                        // a few motes pulled inward off the shell, so the dome reads as gravity, not decor
                        s -> s.emit(pos, Vfx.emitter()
                                .shape(ShapeSpec.hemisphere(4.5f))
                                .lifetime(200).speed(0.12f)
                                .implode()
                                .size(0.14f, 0.04f, Easings.EASE_IN_QUAD)
                                .alpha(0.6f, 0.0f, Easings.LINEAR)
                                .gradient(Easings.LINEAR, ACCENT_COLOR, DARK_COLOR)
                                .rate(9.0f, 74)
                                .sprite(SpriteId.GLOW).stretch(0.8f).trail(4)),
                        // a soft core orb swells at the center as the field tightens
                        s -> s.emit(pos, Vfx.emitter()
                                .shape(ShapeSpec.sphere(0.3f))
                                .lifetime(209).speed(0.0f)
                                .size(0.5f, 1.0f, Easings.EASE_OUT_QUAD)
                                .alpha(0.9f, 0.0f, Easings.LINEAR)
                                .gradient(Easings.LINEAR, BRIGHT_COLOR, BASE_COLOR)
                                .rate(2.5f, 74)
                                .sprite(SpriteId.GLOW)),
                        s -> s.light(pos, BASE_COLOR, 5.0f, 82))
                .delay(209)
                // collapse to a point, the field winks white
                .emit(pos, Vfx.emitter()
                        .shape(ShapeSpec.sphere(3.0f))
                        .count(120).lifetime(9).speed(0.55f)
                        .implode()
                        .size(0.16f, 0.0f, Easings.EASE_IN_QUAD)
                        .alpha(1.0f, 0.0f, Easings.LINEAR)
                        .gradient(Easings.LINEAR, BASE_COLOR, WHITE)
                        .sprite(SpriteId.SPARK).stretch(2.0f).trail(4))
                .delay(18)
                .run(() -> gravityStarBlast(level, pos))
                .delay(35)
                // aftermath: low dust drifts outward and settles
                .emit(pos, Vfx.emitter()
                        .shape(ShapeSpec.disc(2.5f))
                        .count(120).lifetime(70).speed(0.05f)
                        .size(0.16f, 0.0f, Easings.LINEAR)
                        .alpha(0.6f, 0.0f, Easings.LINEAR)
                        .gradient(Easings.LINEAR, ACCENT_COLOR, DARK_COLOR)
                        .curl(0.02f, 0.5f)
                        .sprite(SpriteId.SMOKE).blend(BlendMode.ALPHA).lit())
                .play();
    }

    private static void gravityStarBlast(ServerLevel level, Vec3 pos)
    {
        Vfx.emitter()
                .shape(ShapeSpec.disc(0.5f))
                .count(200).lifetime(26).speed(0.8f)
                .size(0.24f, 0.0f, Easings.EASE_OUT_QUAD)
                .alpha(1.0f, 0.0f, Easings.LINEAR)
                .gradient(Easings.LINEAR, WHITE, BRIGHT_COLOR, DARK_COLOR)
                .drag(0.06f)
                .sprite(SpriteId.GLOW).stretch(3.0f).trail(5)
                .play(level, pos);
        Vfx.emitter()
                .shape(ShapeSpec.ring(0.6f))
                .count(110).lifetime(20).speed(0.9f)
                .size(0.32f, 0.0f, Easings.LINEAR)
                .alpha(1.0f, 0.0f, Easings.LINEAR)
                .gradient(Easings.LINEAR, ACCENT_COLOR, WHITE)
                .sprite(SpriteId.RING).stretch(1.6f)
                .play(level, pos);
        Vfx.emitter()
                .shape(ShapeSpec.ring(1.2f))
                .count(90).lifetime(28).speed(0.55f)
                .size(0.4f, 0.0f, Easings.LINEAR)
                .alpha(0.8f, 0.0f, Easings.LINEAR)
                .gradient(Easings.LINEAR, BRIGHT_COLOR, DARK_COLOR)
                .sprite(SpriteId.RING).stretch(1.4f)
                .play(level, pos);
        radialBolts(level, pos, 12, BRIGHT_COLOR, 0.3f, 0.4f, 12, 9.0, 0.0);
        Vfx.light(level, pos, ACCENT_COLOR, 9.0f, 26);
        Vfx.shake(level, pos, 3.4f, 20);
    }

    private static void radialBolts(ServerLevel level, Vec3 pos, int count, int color, float width, float arc, int duration, double reach, double lift)
    {
        for (int i = 0; i < count; i++)
        {
            double angle = Math.PI * 2.0 * i / count;
            Vec3 dir = new Vec3(Math.cos(angle), lift, Math.sin(angle));
            Vfx.beam().color(color).width(width).arc(arc).duration(duration).play(level, pos, pos.add(dir.scale(reach)));
        }
    }

    public static int getBrightColor() {
        return BRIGHT_COLOR;
    }

    public static void setBrightColor(int brightColor) {
        BRIGHT_COLOR = brightColor;
    }

    public static int getAccentColor() {
        return ACCENT_COLOR;
    }

    public static void setAccentColor(int accentColor) {
        ACCENT_COLOR = accentColor;
    }

    public static int getDarkColor() {
        return DARK_COLOR;
    }

    public static void setDarkColor(int darkColor) {
        DARK_COLOR = darkColor;
    }

    public static int getBaseColor() {
        return BASE_COLOR;
    }

    public static void setBaseColor(int baseColor) {
        BASE_COLOR = baseColor;
    }
}
