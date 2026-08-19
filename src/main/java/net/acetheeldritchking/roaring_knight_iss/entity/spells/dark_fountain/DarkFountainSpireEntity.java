package net.acetheeldritchking.roaring_knight_iss.entity.spells.dark_fountain;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.FireBossEntity;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.RoaringHarbingerBoss;
import net.acetheeldritchking.roaring_knight_iss.particles.RKParticleHelper;
import net.acetheeldritchking.roaring_knight_iss.registries.RKEntityRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;

public class DarkFountainSpireEntity extends AoeEntity implements AntiMagicSusceptible {
    @Nullable LivingEntity target;
    public static boolean isFullFountain;
    public static final int WARMUP_TIME = isFullFountain ? 25 : 15;

    public DarkFountainSpireEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public DarkFountainSpireEntity(Level level, boolean isFullFountain) {
        this(RKEntityRegistry.DARK_FOUNTAIN_SPIRE.get(), level);
        this.isFullFountain = isFullFountain;
    }

    public DarkFountainSpireEntity(Level level, double x, double y, double z, boolean isFullFountain) {
        this(RKEntityRegistry.DARK_FOUNTAIN_SPIRE.get(), level);
        this.isFullFountain = isFullFountain;
        this.setPos(x, y, z);
    }

    @Override
    public void onAntiMagic(MagicData playerMagicData) {
        // Put smth interesting here when counterspelled?
    }

    @Override
    protected Vec3 getInflation() {
        Vec3 normal = new Vec3(2, 2, 2);
        Vec3 big = new Vec3(7.5, 7.5, 7.5);
        return isFullFountain ? big : normal;
    }

    @Override
    public void applyEffect(LivingEntity target) {
        // Fuck you and your iframes
        target.invulnerableTime = 0;
        DamageSources.applyDamage(target, getDamage(), SpellRegistry.SUNBEAM_SPELL.get().getDamageSource(this, getOwner()));
    }

    @Override
    public float getParticleCount() {
        return 0;
    }

    public void setTarget(@Nullable LivingEntity target) {
        this.target = target;
    }

    @Override
    protected boolean canHitTargetForGroundContext(LivingEntity target) {
        return true;
    }

    @Override
    public Optional<ParticleOptions> getParticle() {
        return Optional.empty();
    }

    @Override
    public void tick() {
        this.setOldPosAndRot();

        if (tickCount == WARMUP_TIME)
        {
            if (!level().isClientSide)
            {
                checkHits();
                MagicManager.spawnParticles(level(), RKParticleHelper.DARK_BUBBLE, getX() + 0.05, getY() + 0.06, getZ(), 50, getRadius() * .7f, .2f, getRadius() * .7f, 0.2f, true);
                MagicManager.spawnParticles(level(), new BlastwaveParticleOptions(ASUtils.rbgToVector3F(14, 13, 18), 7f), getX(), getY() + 0.06, getZ(), 1, 0, 0, 0, 0, true);
                level().playSound(null, this.blockPosition(), SoundRegistry.SUNBEAM_IMPACT.get(), SoundSource.NEUTRAL, 4.5f, Utils.random.nextIntBetweenInclusive(9, 11) * .1f);
            }
        }

        if (this.tickCount > WARMUP_TIME)
        {
            // Leave fountain here when done
            if (isFullFountain)
            {
                createDarkFountain();
                discard();
            }
            discard();
        }
    }

    private void createDarkFountain()
    {
        DarkFountainEntity darkFountain = new DarkFountainEntity(this.level());
        darkFountain.setOwner(this.getOwner());
        darkFountain.setPos(Utils.moveToRelativeGroundLevel(level(), this.position(), 3));
        darkFountain.setRadius(4);
        darkFountain.setCircular();
        darkFountain.setDamage(this.getDamage() * .5f);
        darkFountain.setDuration(20 * 15);
        darkFountain.setDelay(50);
        darkFountain.setRadiusPerTick(-darkFountain.getRadius() / darkFountain.getDuration());
        level().addFreshEntity(darkFountain);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("fullFountain", isFullFountain);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        isFullFountain = pCompound.getBoolean("fullFountain");
    }
}
