package net.acetheeldritchking.roaring_knight_iss.entity.spells.star_projectile;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.acetheeldritchking.roaring_knight_iss.entity.spells.star_shrapnel.DarkStarShrapnelProjectileEntity;
import net.acetheeldritchking.roaring_knight_iss.particles.RKParticleHelper;
import net.acetheeldritchking.roaring_knight_iss.registries.RKEntityRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;

public class DarkStarProjectileEntity extends AbstractMagicProjectile implements GeoAnimatable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Stuff for the actual movement and explarding
    private static final double PULL_IN_RADIUS = 2.0;

    private double initialAngle;
    private double angularSpeed = Math.toRadians(6.0);
    private double ringRadius   = 20.0;
    private double baseHeight   = 1.2;

    private int expandTicks = 26;
    private int holdTicks   = 120;
    private int pullTicks   = 42;

    private double bobAmplitude, bobPeriod, phase;
    private double slalomRadial, slalomLateral, slalomPeriod;

    private boolean fragmentsOnImplode;
    private Vec3 origin = Vec3.ZERO;
    private long spawnGameTime;

    public AttackMode mode = AttackMode.STANDARD;

    public enum AttackMode
    {
        STANDARD,
        HALF_HEALTH
    }

    public DarkStarProjectileEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public DarkStarProjectileEntity(Level level, LivingEntity owner, AttackMode mode) {
        this(RKEntityRegistry.DARK_STAR_PROJECTILE.get(), level);
        this.setNoGravity(true);
        this.noPhysics = true;
        this.setOwner(owner);
        this.origin = owner.position();
        this.spawnGameTime = level.getGameTime();
        this.mode = mode;
    }

    public DarkStarProjectileEntity configure(Vec3 origin, double initialAngle, double angularSpeed,
                                              double ringRadius, double baseHeight,
                                              int expandTicks, int holdTicks, int pullTicks) {
        this.origin = origin;
        this.initialAngle = initialAngle;
        this.angularSpeed = angularSpeed;
        this.ringRadius = ringRadius;
        this.baseHeight = baseHeight;
        this.expandTicks = expandTicks;
        this.holdTicks = holdTicks;
        this.pullTicks = pullTicks;
        Vec3 start = spiralPos(0);
        this.moveTo(start.x, start.y, start.z, getYRot(), getXRot());
        return this;
    }

    public DarkStarProjectileEntity withWave(double bobAmplitude, double bobPeriod, double phase,
                                             double slalomRadial, double slalomLateral,
                                             double slalomPeriod) {
        this.bobAmplitude = bobAmplitude;
        this.bobPeriod = bobPeriod;
        this.phase = phase;
        this.slalomRadial = slalomRadial;
        this.slalomLateral = slalomLateral;
        this.slalomPeriod = slalomPeriod;
        return this;
    }

    public DarkStarProjectileEntity fragmentsOnImplode(boolean b) {
        this.fragmentsOnImplode = b;
        return this;
    }

    @Override
    public void trailParticles() {
        var vec = getDeltaMovement();
        var length = vec.length();
        int count = (int) Math.min(20, Math.round(length) * 3) + 1;
        float f = (float) length / count;
        for (int i = 0; i < count; i++) {
            Vec3 random = Utils.getRandomVec3(0.02);
            Vec3 p = vec.scale(f * i);
            level().addParticle(RKParticleHelper.DARK_STAR, this.getX() + random.x + p.x, this.getY() + random.y + p.y, this.getZ() + random.z + p.z, random.x, random.y, random.z);
        }
    }

    @Override
    public void impactParticles(double x, double y, double z) {

    }

    @Override
    public float getSpeed() {
        return 0.0F;
    }

    @Override
    public Optional<Holder<SoundEvent>> getImpactSound() {
        return Optional.empty();
    }

    private int age() {
        return (int) Math.max(0, level().getGameTime() - spawnGameTime);
    }

    private Vec3 spiralPos(int age) {
        double radius;
        double pull = 0.0;

        if (age <= expandTicks) {
            double t = (double) age / expandTicks;
            radius = ringRadius * (1.0 - (1.0 - t) * (1.0 - t));
        } else if (age <= expandTicks + holdTicks) {
            radius = ringRadius;
        } else {
            pull = Math.min(1.0, (double)(age - expandTicks - holdTicks) / pullTicks);
            radius = Mth.lerp(pull * pull * pull, ringRadius, PULL_IN_RADIUS);
        }

        double angle = initialAngle + age * angularSpeed;

        double mix = 1.0 - pull;

        angle  += Math.sin(age * slalomPeriod + phase) * slalomLateral * mix;
        radius += Math.sin(age * slalomPeriod * 0.61 + phase) * slalomRadial * mix;

        double y = baseHeight + Math.sin(age * bobPeriod + phase) * bobAmplitude * mix;
        y = Mth.lerp(pull, y, 1.3);   // everything converges at chest height in the center

        return new Vec3(origin.x + Math.cos(angle) * radius,
                origin.y + y,
                origin.z + Math.sin(angle) * radius);
    }

    @Override
    public void tick() {
        switch (mode)
        {
            case HALF_HEALTH -> halfHealthTick();
        }

        super.tick();
    }

    protected void standardTick()
    {
        //
    }

    protected void halfHealthTick()
    {
        int age = age();

        if (age >= expandTicks + holdTicks + pullTicks) {
            if (!level().isClientSide) implode();
            return;
        }

        Vec3 from = this.position();

        Vec3 required = spiralPos(age).subtract(from);
        this.setDeltaMovement(required);
        this.hasImpulse = true;
    }

    @Override
    protected void onHit(@NotNull HitResult hitresult) {
        if (!this.level().isClientSide)
        {
            float explosionRadius = getExplosionRadius();
            var explosionRadiusSqr = explosionRadius * explosionRadius;
            var entities = level().getEntities(this, this.getBoundingBox().inflate(explosionRadius));
            Vec3 losPoint = Utils.raycastForBlock(level(), this.position(), this.position().add(0, 2, 0), ClipContext.Fluid.NONE).getLocation();
            for (Entity entity : entities) {
                double distanceSqr = entity.distanceToSqr(hitresult.getLocation());
                if (distanceSqr < explosionRadiusSqr && canHitEntity(entity) && Utils.hasLineOfSight(level(), losPoint, entity.getBoundingBox().getCenter(), true)) {
                    double p = (1 - distanceSqr / explosionRadiusSqr);
                    float damage = (float) (this.damage * p);
                    var damageSource = new DamageSource(DamageSources.getHolderFromResource(entity, DamageTypes.MAGIC), this, getOwner());
                    DamageSources.applyDamage(entity, damage, damageSource);
                    if (entity instanceof LivingEntity livingEntity)
                    {
                        // Fuck your iframes
                        livingEntity.invulnerableTime = 0;
                    }
                }
            }
            implode();
            this.discardHelper(hitresult);
        }
    }

    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
        // empty
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (entity == getOwner()) return false;
        if (entity instanceof DarkStarProjectileEntity
                || entity instanceof DarkStarShrapnelProjectileEntity) return false;
        return super.canHitEntity(entity);
    }

    @Override
    public boolean hurt(DamageSource s, float a)
    {
        return false;
    }
    @Override
    public boolean isPickable()
    {
        return false;
    }

    private void implode() {
        if (level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.EXPLOSION, getX(), getY(), getZ(), 1, 0, 0, 0, 0);
        }
        //level().playSound(null, blockPosition(), SoundRegistry.ARCANE_IMPACT.get(),
                //SoundSource.HOSTILE, 0.6F, 1F);

        if (fragmentsOnImplode) {
            double baseAngle = this.random.nextDouble() * Math.PI * 2;
            for (int i = 0; i < 2; i++) {
                double a = baseAngle + i * Math.PI;
                var frag = new DarkStarShrapnelProjectileEntity(level());
                frag.setOwner(getOwner());          // was setOwner(this) — kept damage attribution off the boss
                frag.setDamage(15.0F);
                frag.setExplosionRadius(3.0F);
                frag.setPos(getX(), getY(), getZ());
                frag.setDeltaMovement(Math.cos(a) * 0.35, 0.05, Math.sin(a) * 0.35);
                level().addFreshEntity(frag);
            }
        }
        discard();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        // Nothing here
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
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        // If I touch this, the Roaring starts
        super.defineSynchedData(pBuilder);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putDouble("InitialAngle", this.initialAngle);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.initialAngle = tag.getDouble("InitialAngle");
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeDouble(origin.x); buffer.writeDouble(origin.y); buffer.writeDouble(origin.z);
        buffer.writeDouble(initialAngle); buffer.writeDouble(angularSpeed);
        buffer.writeDouble(ringRadius);   buffer.writeDouble(baseHeight);
        buffer.writeVarInt(expandTicks);  buffer.writeVarInt(holdTicks); buffer.writeVarInt(pullTicks);
        buffer.writeDouble(bobAmplitude); buffer.writeDouble(bobPeriod); buffer.writeDouble(phase);
        buffer.writeDouble(slalomRadial); buffer.writeDouble(slalomLateral); buffer.writeDouble(slalomPeriod);
        buffer.writeLong(spawnGameTime);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        origin = new Vec3(additionalData.readDouble(), additionalData.readDouble(), additionalData.readDouble());
        initialAngle = additionalData.readDouble(); angularSpeed = additionalData.readDouble();
        ringRadius = additionalData.readDouble();   baseHeight = additionalData.readDouble();
        expandTicks = additionalData.readVarInt();  holdTicks = additionalData.readVarInt(); pullTicks = additionalData.readVarInt();
        bobAmplitude = additionalData.readDouble(); bobPeriod = additionalData.readDouble(); phase = additionalData.readDouble();
        slalomRadial = additionalData.readDouble(); slalomLateral = additionalData.readDouble(); slalomPeriod = additionalData.readDouble();
        spawnGameTime = additionalData.readLong();
    }
}
