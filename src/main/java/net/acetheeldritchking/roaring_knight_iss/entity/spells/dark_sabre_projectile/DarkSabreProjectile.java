package net.acetheeldritchking.roaring_knight_iss.entity.spells.dark_sabre_projectile;

import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.util.NBT;
import net.acetheeldritchking.roaring_knight_iss.registries.RKEntityRegistry;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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

    public int delay;
    public @Nullable Vec3 ownerTracking = null;
    public @Nullable UUID targetEntity = null;
    public @Nullable Entity cachedTarget = null;
    int age;

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
        result.getEntity().hurt(new DamageSource(level().damageSources().damageTypes.getHolderOrThrow(ISSDamageTypes.ELDRITCH_MAGIC), this, getOwner()), getDamage());
        result.getEntity().invulnerableTime = 0;
    }

    @Override
    protected void onHit(@NotNull HitResult hitresult) {
        super.onHit(hitresult);
        discardHelper(hitresult);
    }

    @Override
    public float getSpeed() {
        return 2.5F;
    }

    @Override
    public void tick() {
        if (age++ < delay)
        {
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
        } else
        {
            super.tick();
        }
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
        if (ownerTracking != null)
        {
            tag.put("ownerTracking", NBT.writeVec3Pos(ownerTracking));
        }
        if (targetEntity != null)
        {
            tag.putUUID("target", targetEntity);
        }
        tag.putInt("Age", age);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.delay = tag.getInt("delay");
        if (tag.hasUUID("ownerTracking"))
        {
            this.ownerTracking = NBT.readVec3(tag.getCompound("ownerTracking"));
        }
        if (tag.hasUUID("target"))
        {
            this.targetEntity = tag.getUUID("target");
        }
        this.age = tag.getInt("Age");
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
    }
}
