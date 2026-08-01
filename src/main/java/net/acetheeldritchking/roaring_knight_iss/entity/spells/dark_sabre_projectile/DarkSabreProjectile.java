package net.acetheeldritchking.roaring_knight_iss.entity.spells.dark_sabre_projectile;

import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.util.NBT;
import net.acetheeldritchking.roaring_knight_iss.registries.RKEntityRegistry;
import net.acetheeldritchking.roaring_knight_iss.utils.RKUtils;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class DarkSabreProjectile extends AbstractMagicProjectile implements IEntityWithComplexSpawn, GeoAnimatable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public DarkSabreProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        setNoGravity(true);
    }

    public DarkSabreProjectile(Level pLevel) {
        this(RKEntityRegistry.DARK_SABRE_PROJECTILE.get(), pLevel);
    }

    // new constructor for "surround" mode
    public DarkSabreProjectile(Level pLevel, AttackMode mode, double ringAngleOffset) {
        this(RKEntityRegistry.DARK_SABRE_PROJECTILE.get(), pLevel);
        this.mode = mode;
        this.ringAngleOffset = ringAngleOffset;
    }

    // new constructor for "spread" mode
    public DarkSabreProjectile(Level pLevel, AttackMode mode, Vec3 sphereOffset) {
        this(RKEntityRegistry.DARK_SABRE_PROJECTILE.get(), pLevel);
        this.mode = mode;
        this.sphereOffset = sphereOffset;
    }

    public int delay;
    public @Nullable Vec3 ownerTracking = null;
    public @Nullable UUID targetEntity = null;
    public @Nullable Entity cachedTarget = null;
    int age;
    float speed;

    public void setTarget(Entity target)
    {
        this.cachedTarget = target;
        this.targetEntity = target.getUUID();
    }

    public boolean isTrackingOwner()
    {
        return ownerTracking != null;
    }

    public boolean hasTarget()
    {
        return targetEntity != null;
    }

    //new method used in DarkSabreProjectileRenderer
    public int getAge() {
        return age;
    }

    public Entity getTargetEntity()
    {
        if (cachedTarget != null && cachedTarget.isAlive())
        {
            return cachedTarget;
        } else if (targetEntity != null && level() instanceof ServerLevel serverLevel)
        {
            this.cachedTarget = serverLevel.getEntity(targetEntity);
            if (cachedTarget == null)
            {
                this.targetEntity = null;
            }
            return cachedTarget;
        } else {
            return null;
        }
    }

    @Override
    public void trailParticles() {

    }

    @Override
    public void impactParticles(double x, double y, double z) {

    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);
        result.getEntity().invulnerableTime = 0;
        result.getEntity().hurt(new DamageSource(level().damageSources().damageTypes.getHolderOrThrow(DamageTypes.MAGIC), this, getOwner()), getDamage());
    }

    @Override
    protected void onHit(@NotNull HitResult hitresult) {
        super.onHit(hitresult);
        discardHelper(hitresult);
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    public float setSpeed(float speed) {
        return this.speed = speed;
    }

    public enum AttackMode {
        NORMAL, SURROUND, SPREAD
    }

    /*
    not really sure what NORMAL does it might not do anything right now
     */
    public AttackMode mode = AttackMode.NORMAL; // current default fallback
    public double ringAngleOffset; // SURROUND only: fixed angle slice around the ring
    public Vec3 sphereOffset = Vec3.ZERO; // SPREAD only: fixed offset on the Fibonacci sphere

    @Override
    public void tick() {
        if (age++ < delay)
        {
            switch (mode) {
                case NORMAL -> tickNormalMode();
                case SURROUND -> tickSurroundMode();
                case SPREAD -> tickSpreadMode();
            }
        } else
        {
            super.tick();
        }
    }

    /*
    moved the original contents/logic from inside tick() to its own dedicated method
     */
    private void tickNormalMode() {
        var owner = getOwner();
        float strength = 0.75F;

        if (owner != null && isTrackingOwner())
        {
            Vec3 ownerMotion = owner.position().subtract(owner.xOld, owner.yOld, owner.zOld);
            setPos(this.position().add(ownerMotion));
        }

        var target = getTargetEntity();
        if (target != null)
        {
            var targetPos = target.getBoundingBox().getCenter();
            Vec3 targetMotion = targetPos.subtract(this.position()).normalize().scale(this.getSpeed());
            Vec3 currentMotion = getDeltaMovement();
            deltaMovementOld = currentMotion;
            this.setDeltaMovement(currentMotion.add(targetMotion.subtract(currentMotion).scale(strength)));
            if (tickCount == 1)
            {
                deltaMovementOld = getDeltaMovement();
            }
        }
        if (age == delay)
        {
            var hits = level().getEntities(this, this.getBoundingBox().inflate(0.4F), this::canHitEntity);
            EntityHitResult hitResult = hits.isEmpty() ? null : new EntityHitResult(hits.getFirst());
            if (hitResult != null)
            {
                onHit(hitResult);
            }
        }
    }

    private void tickSurroundMode() {
        var target = getTargetEntity();
        if (target == null) {
            return;
        }

        if (age == delay) {

            //AbstractMagicProjectile already has a homing system, better to just use it instead of redefining
            if (target instanceof LivingEntity livingTarget) {
                Vec3 initialDirection = target.getBoundingBox().getCenter().subtract(this.position()).normalize();
                setDeltaMovement(initialDirection.scale(getSpeed()));
                setHomingTarget(livingTarget);
            }
            return;
        }

        double radius = 2.0;
        double rotationSpeed = 0.05;
        double currentAngle = ringAngleOffset + (age * rotationSpeed);

        double x = target.getX() + Math.cos(currentAngle) * radius;
        double y = target.getY() + (target.getEyeHeight() * 0.5);
        double z = target.getZ() + Math.sin(currentAngle) * radius;

        RKUtils.spawnTelegraphedParticleLine(target.getBoundingBox().getCenter(), new Vec3(x, y, z), 0.35F, this);

        moveTo(x, y, z);
        setDeltaMovement(Vec3.ZERO);
    }

    // holds position relative to its fixed point on the sphere (recomputed each
    // tick from the target's current position, so the sphere "follows" a moving
    // target without the points themselves changing), then strikes at age == delay
    private void tickSpreadMode() {
        var target = getTargetEntity();
        if (target == null)
        {
            return;
        }

        if (age == delay) {
            //sets a homing target entity via LivingEntity and gives it some motion as otherwise it wouldnt
            // move, even with a homing target "THONK emoji", same logic for tickSurroundMode()
            if (target instanceof LivingEntity livingTarget) {
                Vec3 initialDirection = target.getBoundingBox().getCenter().subtract(this.position()).normalize();
                setDeltaMovement(initialDirection.scale(getSpeed()));
                setHomingTarget(livingTarget);
            }
            return;
        }

        Vec3 spherePoint = target.getBoundingBox().getCenter().add(sphereOffset);

        RKUtils.spawnTelegraphedParticleLine(target.getBoundingBox().getCenter(), spherePoint, 0.35F, this);

        moveTo(spherePoint.x, spherePoint.y, spherePoint.z);
        setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public Optional<Holder<SoundEvent>> getImpactSound() {
        return Optional.empty();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        // Nothing in here CAUSE WE ARE NOT ANIMATED!!!
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object o) {
        return tickCount;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("delay", delay);
        if (ownerTracking != null) {
            tag.put("ownerTracking", NBT.writeVec3Pos(ownerTracking));
        }
        if (targetEntity != null) {
            tag.putUUID("target", targetEntity);
        }
        tag.putInt("Age", age);

        //storing angleOffset and  mode data, since we cant store enums as nbt,
        // convert to string than retrieve via readAdditionalSaveData()
        tag.putString("Mode", mode.name());
        tag.putDouble("RingAngleOffset", ringAngleOffset);
        tag.put("SphereOffset", NBT.writeVec3Pos(sphereOffset));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.delay = tag.getInt("delay");

        //TODO might want to switch tag.hasUUID() to tag.contains() since above we do
        //TODO tag.put("ownerTracking", NBT.writeVec3Pos(ownerTracking));
        //TODO but I'm not entirely sure if this is a problem or not
        if (tag.hasUUID("ownerTracking")) {
            this.ownerTracking = NBT.readVec3(tag.getCompound("ownerTracking"));
        }
        if (tag.hasUUID("target")) {
            this.targetEntity = tag.getUUID("target");
        }
        this.age = tag.getInt("Age");
        if (tag.contains("Mode")) {
            this.mode = AttackMode.valueOf(tag.getString("Mode"));
        }
        this.ringAngleOffset = tag.getDouble("RingAngleOffset");
        if (tag.contains("SphereOffset")) {
            this.sphereOffset = NBT.readVec3(tag.getCompound("SphereOffset"));
        }
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(this.delay);
        var tracking = ownerTracking != null;
        buffer.writeBoolean(tracking);
        if (tracking) {
            buffer.writeDouble(ownerTracking.x);
            buffer.writeDouble(ownerTracking.y);
            buffer.writeDouble(ownerTracking.z);
        }
        var target = cachedTarget != null;
        buffer.writeBoolean(target);
        if (target) {
            buffer.writeInt(cachedTarget.getId());
        }
        buffer.writeEnum(this.mode);
        buffer.writeDouble(this.ringAngleOffset);
        buffer.writeDouble(this.sphereOffset.x);
        buffer.writeDouble(this.sphereOffset.y);
        buffer.writeDouble(this.sphereOffset.z);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf buffer) {
        this.delay = buffer.readInt();
        if (buffer.readBoolean()) {
            this.ownerTracking = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        }
        if (buffer.readBoolean()) {
            this.cachedTarget = this.level().getEntity(buffer.readInt());
            if (this.cachedTarget != null) {
                this.targetEntity = cachedTarget.getUUID();
            }
        }
        this.mode = buffer.readEnum(AttackMode.class);
        this.ringAngleOffset = buffer.readDouble();
        this.sphereOffset = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    /*
    Spawn-site helpers - not yet wired into the boss AI / spell cast path.
    Both assume "delay" is in ticks
    */
    public static void spawnSurroundGroup(Level level, Entity target, Entity owner, int count, double radius, int delayTicks, float speed, float damage) {
        //int count = 5;
        //double radius = 2.0;
        //int delayTicks = 100; // 5 seconds

        for (int i = 0; i < count; i++) {
            double angle = (2 * Math.PI / count) * i;

            DarkSabreProjectile projectile = new DarkSabreProjectile(
                    //RKEntityRegistry.DARK_SABRE_PROJECTILE.get(),
                    level,
                    AttackMode.SURROUND,
                    angle
            );
            projectile.setOwner(owner);
            projectile.setSpeed(speed);
            projectile.setTarget(target);
            projectile.setDamage(damage);
            projectile.delay = delayTicks;

            double spawnX = target.getX() + Math.cos(angle) * radius;
            double spawnY = target.getY() + (target.getEyeHeight() * 0.5);
            double spawnZ = target.getZ() + Math.sin(angle) * radius;
            projectile.setPos(spawnX, spawnY, spawnZ);

            level.addFreshEntity(projectile);
        }
    }

    public static void spawnDelayedSurroundGroup(Level level, Entity target, Entity owner, int count, double radius, int delayTicks, float speed, float damage) {
        //int count = 5;
        //double radius = 2.0;
        //int delayTicks = 100; // 5 seconds

        for (int i = 0; i < count; i++) {
            double angle = (2 * Math.PI / count) * i;

            DarkSabreProjectile projectile = new DarkSabreProjectile(
                    //RKEntityRegistry.DARK_SABRE_PROJECTILE.get(),
                    level,
                    AttackMode.SURROUND,
                    angle
            );
            projectile.setOwner(owner);
            projectile.setSpeed(speed);
            projectile.setTarget(target);
            projectile.setDamage(damage);
            projectile.delay = delayTicks + i * 2;

            double spawnX = target.getX() + Math.cos(angle) * radius;
            double spawnY = target.getY() + (target.getEyeHeight() * 0.5);
            double spawnZ = target.getZ() + Math.sin(angle) * radius;
            projectile.setPos(spawnX, spawnY, spawnZ);

            level.addFreshEntity(projectile);
        }
    }

    // spawn in a sphere, then fire, no timing given
    public static void spawnSpreadGroup(Level level, Entity target, Entity owner, int count, double radius, int delayTicks, float speed, float damage) {
        //int count = 8;
        //double radius = 2.5;
        //int delayTicks = 100;

        for (int i = 0; i < count; i++) {
            // Fibonacci sphere distribution
            double y = 1 - (i / (double) (count - 1)) * 2; // from 1 to -1
            double radiusAtY = Math.sqrt(1 - y * y);
            double goldenAngle = Math.PI * (3 - Math.sqrt(5));
            double theta = goldenAngle * i;
            double x = Math.cos(theta) * radiusAtY;
            double z = Math.sin(theta) * radiusAtY;

            Vec3 offset = new Vec3(x * radius, y * radius, z * radius);

            DarkSabreProjectile projectile = new DarkSabreProjectile(
                    //RKEntityRegistry.DARK_SABRE_PROJECTILE.get(),
                    level,
                    AttackMode.SPREAD,
                    offset
            );
            projectile.setOwner(owner);
            projectile.setSpeed(speed);
            projectile.setTarget(target);
            projectile.setDamage(damage);
            projectile.delay = delayTicks;

            Vec3 spawnPos = target.getBoundingBox().getCenter().add(offset);
            projectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

            level.addFreshEntity(projectile);
        }
    }

    public static void spawnDelayedSpreadGroup(Level level, Entity target, Entity owner, int count, double radius, int delayTicks, float speed, float damage) {
        //int count = 8;
        //double radius = 2.5;
        //int delayTicks = 100;

        for (int i = 0; i < count; i++) {
            // Fibonacci sphere distribution
            double y = 1 - (i / (double) (count - 1)) * 2; // from 1 to -1
            double radiusAtY = Math.sqrt(1 - y * y);
            double goldenAngle = Math.PI * (3 - Math.sqrt(5));
            double theta = goldenAngle * i;
            double x = Math.cos(theta) * radiusAtY;
            double z = Math.sin(theta) * radiusAtY;

            Vec3 offset = new Vec3(x * radius, y * radius, z * radius);

            DarkSabreProjectile projectile = new DarkSabreProjectile(
                    //RKEntityRegistry.DARK_SABRE_PROJECTILE.get(),
                    level,
                    AttackMode.SPREAD,
                    offset
            );
            projectile.setOwner(owner);
            projectile.setSpeed(speed);
            projectile.setTarget(target);
            projectile.setDamage(damage);
            projectile.delay = delayTicks + i * 2;

            Vec3 spawnPos = target.getBoundingBox().getCenter().add(offset);
            projectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

            level.addFreshEntity(projectile);
        }
    }
}