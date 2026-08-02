package net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.util.BossbarManager;
import io.redspace.ironsspellbooks.entity.mobs.dead_king_boss.DeadKingBoss;
import io.redspace.ironsspellbooks.entity.mobs.goals.MomentHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.SpellBarrageGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackAnimationData;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.ExtendedServerBossEvent;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.FireBossEntity;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.NotIdioticNavigation;
import io.redspace.ironsspellbooks.network.EntityEventPacket;
import net.acetheeldritchking.aces_spell_utils.entity.mobs.GenericBossEntity;
import net.acetheeldritchking.aces_spell_utils.entity.mobs.GenericUniqueBossEntity;
import net.acetheeldritchking.aces_spell_utils.entity.mobs.goals.WizardSpellComboGoal;
import net.acetheeldritchking.aces_spell_utils.registries.ASAttributeRegistry;
import net.acetheeldritchking.aces_spell_utils.utils.boss_music.UniqueBossMusicManager;
import net.acetheeldritchking.roaring_knight_iss.TheRoaringSpellbooks;
import net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.goals.*;
import net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.keyframes.RedCleaveKeyFrame;
import net.acetheeldritchking.roaring_knight_iss.entity.spells.star_projectile.DarkStarProjectileEntity;
import net.acetheeldritchking.roaring_knight_iss.registries.RKEntityRegistry;
import net.acetheeldritchking.roaring_knight_iss.registries.RKSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RoaringHarbingerBoss extends GenericUniqueBossEntity implements IEntityWithComplexSpawn {
    public RoaringHarbingerBoss(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        setPersistenceRequired();
        xpReward = 60;
        this.lookControl = createLookControl();
        this.moveControl = createMoveControl();
        createBossEvent();
    }

    public RoaringHarbingerBoss(Level level)
    {
        this(RKEntityRegistry.BLACK_EXECUTIONER_BOSS.get(), level);
        setPersistenceRequired();
    }

    // Boss Bar
    private static final BossbarManager.BossbarSprite BOSSBAR_SPRITE = new BossbarManager.BossbarSprite(TheRoaringSpellbooks.id("boss_bars/harbinger_of_roaring_boss_bar"), 192, 18, 3, -1);

    // These are used for doing boss bars, setting up the phase serializer for NBT, and stopping and starting music
    private ExtendedServerBossEvent bossEvent;
    private final static EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(RoaringHarbingerBoss.class, EntityDataSerializers.INT);
    private final static EntityDataAccessor<Boolean> IS_TITAN = SynchedEntityData.defineId(RoaringHarbingerBoss.class, EntityDataSerializers.BOOLEAN);
    public static final byte CLIENT_STOP_TRACKING = 0;
    public static final byte CLIENT_START_TRACKING = 1;
    public static final byte PROC_ROARING_TIMER = 2;
    public static final byte STOP_ROARING_TIMER = 3;
    public static final byte START_MUSIC = 4;
    public static final byte STOP_MUSIC = 5;

    // Boss music
    public static SoundEvent bossMusic = RKSoundEvents.BLACK_EXECUTIONER_THEME.get();
    public static SoundEvent bossTransitionMusic = RKSoundEvents.BLACK_EXECUTIONER_THEME.get();
    public static SoundEvent bossFinalMusic = RKSoundEvents.BLACK_EXECUTIONER_THEME.get();

    // Animation ticks
    public int transitionAnimationTime = 180;
    public int deathAnimationTime = 190;
    int spawnTimer;
    private static final int spawnAnimTime = (int) (7.59 * 20);
    private static final int spawnDelay = 20;

    // Roaring Ability
    boolean hasPerformedHalfHealthRoar;
    protected int halfHealthTimer;
    protected static final int ROARING_ANIM_DURATION = 269;
    protected static final int ROARING_STAR_SHOOT_TIMESTAMP = 20;
    protected static final int ROARING_PULL_IN_START_TIMESTAMP = 216;
    protected static final int DARK_STAR_COUNT = 10;
    protected static final int DARK_STAR_INTERVAL_TICKS = 5;
    protected static final int DARK_STAR_PER_WAVE = 10;
    protected double anglePerWave = 15.0;
    private int darkStarAttackTimer = 0;
    private int darkStarWavesSpawned = 0;

    // Loot
    SimpleContainer deathLoot = null;

    // Block Destroying
    private int destroyBlockDelay;
    private int stuckDetectorDelay;
    private int stuckDetector;
    private Vec3 lastStuck = Vec3.ZERO;

    // Difficulty & Loot
    private int playerScale;

    // Music
    @Override
    public boolean hasCustomMusic() {
        return true;
    }

    @Override
    public boolean changeMusicOnPhaseChange() {
        return false;
    }

    @Override
    public boolean hasTransitionPhase() {
        return false;
    }

    @Override
    public int usePhaseAsTransition() {
        return 2;
    }

    @Override
    public int usePhaseForMusicChange() {
        return 3;
    }

    @Override
    public SoundEvent getBossMusic() {
        return bossMusic;
    }

    @Override
    public SoundEvent getTransitionMusic() {
        return bossTransitionMusic;
    }

    @Override
    public SoundEvent getOtherPhaseMusic() {
        return bossFinalMusic;
    }

    // Helps handle the starting and stopping of boss music
    @Override
    public void handleClientEvent(byte eventId)
    {
        switch (eventId)
        {
            case CLIENT_STOP_TRACKING -> {
                BossbarManager.stopTracking(this.uuid);
                UniqueBossMusicManager.stop(this);
            }
            case CLIENT_START_TRACKING ->
            {
                BossbarManager.startTracking(this.uuid, BOSSBAR_SPRITE);
                UniqueBossMusicManager.createOrResumeInstance(this);
            }
            case PROC_ROARING_TIMER -> this.halfHealthTimer = ROARING_ANIM_DURATION;
            case STOP_ROARING_TIMER -> {
                this.halfHealthTimer = 0;
                this.playAnimation("idle");
            }
            case START_MUSIC -> UniqueBossMusicManager.createOrResumeInstance(this);
            case STOP_MUSIC -> UniqueBossMusicManager.stop(this);
        }
    }

    // These two methods add and remove the boss bar and music based on how far the player is/if it is seen by the boss
    @Override
    public void startSeenByPlayer(ServerPlayer serverPlayer) {
        super.startSeenByPlayer(serverPlayer);
        this.bossEvent.addPlayer(serverPlayer);
        PacketDistributor.sendToPlayer(serverPlayer, new EntityEventPacket<RoaringHarbingerBoss>(this, CLIENT_START_TRACKING));
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer serverPlayer) {
        super.stopSeenByPlayer(serverPlayer);
        this.bossEvent.removePlayer(serverPlayer);
        PacketDistributor.sendToPlayer(serverPlayer, new EntityEventPacket<RoaringHarbingerBoss>(this, CLIENT_STOP_TRACKING));
    }

    // These are for movement and looking controls for smoother movement (from Iron himself)
    protected LookControl createLookControl()
    {
        return new LookControl(this)
        {
            @Override
            protected float rotateTowards(float from, float to, float maxDelta) {
                return super.rotateTowards(from, to, maxDelta * 2.5F);
            }

            @Override
            protected boolean resetXRotOnTick() {
                return getTarget() == null;
            }
        };
    }

    protected MoveControl createMoveControl()
    {
        return new MoveControl(this)
        {
            @Override
            protected float rotlerp(float sourceAngle, float targetAngle, float maximumChange) {
                double x = this.wantedX - this.mob.getX();
                double z = this.wantedZ - this.mob.getZ();

                if (x * x + z * z < 0.5F)
                {
                    return sourceAngle;
                }
                else
                {
                    return super.rotlerp(sourceAngle, targetAngle, maximumChange * 0.25F);
                }
            }
        };
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new NotIdioticNavigation(this, level);
    }

    // Goals
    // Register the basic goals for the boss
    @Override
    protected void registerGoals() {
        firstPhaseGoals();
        this.targetSelector.addGoal(1, new MomentHurtByTargetGoal(this));

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, FireBossEntity.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, DeadKingBoss.class, true));
    }

    public RoaringHarbingerAttackGoal attackGoal = new RoaringHarbingerAttackGoal(this, 1.5F, 25, 40);

    // First Phase
    private void firstPhaseGoals()
    {
        this.goalSelector.getAvailableGoals().forEach(WrappedGoal::stop);
        this.goalSelector.removeAllGoals((x) -> true);

        this.goalSelector.addGoal(1, new FloatGoal(this));

        this.goalSelector.addGoal(4, new SpellBarrageGoal(this, SpellRegistry.ELDRITCH_BLAST_SPELL.get(), 1, 3, 80, 150, 3));
        this.goalSelector.addGoal(3, new SpellBarrageGoal(this, SpellRegistry.TELEPORT_SPELL.get(), 1, 3, 80, 150, 3));

        this.goalSelector.addGoal(2, new SwordSurroundAbilityGoal(this));
        this.goalSelector.addGoal(2, new SwordSpreadAbilityGoal(this));
        // Have this at 1 so we can keep up with the player or target
        this.goalSelector.addGoal(1, new PursuitAbilityGoal(this));

        this.attackGoal = (RoaringHarbingerAttackGoal) new RoaringHarbingerAttackGoal(this, 1.5F, 25, 40)
                .setMoveset(List.of(
                        new AttackAnimationData(42, "slash_1", 22),
                        new AttackAnimationData(51, "slash_2", 26)
                ))
                .setComboChance(0.8F)
                .setMeleeAttackInverval(45, 55)
                .setMeleeMovespeedModifier(1.5F)
                .setMeleeBias(0.75f, 1.0f)
                .setSpells(
                        // Attack
                        List.of(
                                SpellRegistry.MAGIC_MISSILE_SPELL.get()
                        ),
                        // Defense
                        List.of(
                                SpellRegistry.COUNTERSPELL_SPELL.get(),
                                SpellRegistry.EVASION_SPELL.get()
                        ),
                        // Movement
                        List.of(
                                SpellRegistry.TELEPORT_SPELL.get()
                        ),
                        // Support
                        List.of(
                                SpellRegistry.COUNTERSPELL_SPELL.get()
                        )
                ).setSingleUseSpell(SpellRegistry.SONIC_BOOM_SPELL.get(), 70, 100, 3, 5)
                .setSpellQuality(1.0f, 1.0f);

        this.goalSelector.addGoal(3, attackGoal);

        // Defensive combo
        this.goalSelector.addGoal(3, new WizardSpellComboGoal(this,
                List.of(
                        SpellRegistry.TELEPORT_SPELL.get(),
                        SpellRegistry.COUNTERSPELL_SPELL.get(),
                        SpellRegistry.EVASION_SPELL.get()
                ), 1.3f, 1.3f, 80, 150));

        this.goalSelector.addGoal(5, new PatrolNearLocationGoal(this, 32.0F, 0.9));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    // Second/Final Phase
    private void secondPhaseGoals()
    {
        this.goalSelector.getAvailableGoals().forEach(WrappedGoal::stop);
        this.goalSelector.removeAllGoals((x) -> true);

        this.goalSelector.addGoal(1, new FloatGoal(this));

        this.goalSelector.addGoal(2, new SpellBarrageGoal(this, SpellRegistry.ELDRITCH_BLAST_SPELL.get(), 1, 3, 80, 150, 3));
        this.goalSelector.addGoal(4, new SpellBarrageGoal(this, SpellRegistry.TELEPORT_SPELL.get(), 1, 3, 80, 150, 3));

        this.goalSelector.addGoal(2, new ExtremeSlashAbilityGoal(this));
        this.goalSelector.addGoal(2, new SwordSurroundAbilityGoal(this));
        this.goalSelector.addGoal(2, new SwordSpreadAbilityGoal(this));
        // Have this at 1 so we can keep up with the player or target
        this.goalSelector.addGoal(1, new PursuitAbilityGoal(this));

        this.attackGoal = (RoaringHarbingerAttackGoal) new RoaringHarbingerAttackGoal(this, 1.65F, 15, 30)
                .setMoveset(List.of(
                        new AttackAnimationData(42, "slash_1", 22),
                        new AttackAnimationData(51, "slash_2", 26),
                        AttackAnimationData.builder("consecutive_slash")
                                .length(120)
                                .area(0.25f)
                                .rangeMultiplier(4.5f)
                                .attacks(
                                        new RedCleaveKeyFrame(28, new Vec3(0, 0, 0), new Vec3(0, 0, 0), new RedCleaveKeyFrame.SwingData(false, false)),
                                        new RedCleaveKeyFrame(33, new Vec3(0, 0, 0), new Vec3(0, 0, 0), new RedCleaveKeyFrame.SwingData(false, true)),
                                        new RedCleaveKeyFrame(35, new Vec3(0, 0, 0), new Vec3(0, 0, 0), new RedCleaveKeyFrame.SwingData(false, false)),
                                        new RedCleaveKeyFrame(41, new Vec3(0, 0, 0), new Vec3(0, 0, 0), new RedCleaveKeyFrame.SwingData(false, true)),
                                        new RedCleaveKeyFrame(45, new Vec3(0, 0, 0), new Vec3(0, 0, 0), new RedCleaveKeyFrame.SwingData(false, false)),
                                        new RedCleaveKeyFrame(50, new Vec3(0, 0, 0), new Vec3(0, 0, 0), new RedCleaveKeyFrame.SwingData(false, true)),
                                        new RedCleaveKeyFrame(52, new Vec3(0, 0, 0), new Vec3(0, 0, 0), new RedCleaveKeyFrame.SwingData(false, false)),
                                        new RedCleaveKeyFrame(57, new Vec3(0, 0, 0), new Vec3(0, 0, 0), new RedCleaveKeyFrame.SwingData(false, true)),
                                        new RedCleaveKeyFrame(60, new Vec3(0, 0, 0), new Vec3(0, 0, 0), new RedCleaveKeyFrame.SwingData(false, false)),
                                        new RedCleaveKeyFrame(65, new Vec3(0, 0, 0), new Vec3(0, 0, 0), new RedCleaveKeyFrame.SwingData(false, true)),
                                        new RedCleaveKeyFrame(68, new Vec3(0, 0, 0), new Vec3(0, 0, 0), new RedCleaveKeyFrame.SwingData(false, false)),
                                        new RedCleaveKeyFrame(74, new Vec3(0, 0, 0), new Vec3(0, 0, 0), new RedCleaveKeyFrame.SwingData(false, true)),
                                        new RedCleaveKeyFrame(77, new Vec3(0, 0, 0), new Vec3(0, 0, 0), new RedCleaveKeyFrame.SwingData(false, false)),
                                        new RedCleaveKeyFrame(82, new Vec3(0, 0, 0), new Vec3(0, 0, 0), new RedCleaveKeyFrame.SwingData(false, true)),
                                        new RedCleaveKeyFrame(85, new Vec3(0, 0, 0), new Vec3(0, 0, 0), new RedCleaveKeyFrame.SwingData(false, false))
                                ).build()
                ))
                .setComboChance(1.0F)
                .setMeleeAttackInverval(35, 45)
                .setMeleeMovespeedModifier(1.75F)
                .setMeleeBias(0.75f, 1.0f)
                .setSpells(
                        // Attack
                        List.of(
                                SpellRegistry.MAGIC_MISSILE_SPELL.get()
                        ),
                        // Defense
                        List.of(
                                SpellRegistry.COUNTERSPELL_SPELL.get(),
                                SpellRegistry.EVASION_SPELL.get()
                        ),
                        // Movement
                        List.of(
                                SpellRegistry.TELEPORT_SPELL.get()
                        ),
                        // Support
                        List.of(
                                SpellRegistry.COUNTERSPELL_SPELL.get()
                        )
                ).setSingleUseSpell(SpellRegistry.SONIC_BOOM_SPELL.get(), 70, 100, 3, 5)
                .setSpellQuality(1.2f, 1.2f);

        this.goalSelector.addGoal(3, attackGoal);

        // Defensive combo
        this.goalSelector.addGoal(3, new WizardSpellComboGoal(this,
                List.of(
                        SpellRegistry.TELEPORT_SPELL.get(),
                        SpellRegistry.COUNTERSPELL_SPELL.get(),
                        SpellRegistry.EVASION_SPELL.get()
                ), 1.3f, 1.3f, 80, 150));

        this.goalSelector.addGoal(5, new PatrolNearLocationGoal(this, 32.0F, 0.9));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    public void triggerHalfHealthRoaring()
    {
        hasPerformedHalfHealthRoar = true;
        halfHealthTimer = ROARING_ANIM_DURATION;
        this.castComplete();
        this.attackGoal.stopMeleeAction();
        this.serverTriggerEvent(PROC_ROARING_TIMER);
        this.serverTriggerAnimation("roaring");
    }

    public void stopHalfHealthRoaring()
    {
        halfHealthTimer = 0;
        this.serverTriggerEvent(STOP_ROARING_TIMER);
    }

    public boolean isRoaring()
    {
        return halfHealthTimer > 0;
    }

    private void handleHalfHealthRoaring()
    {
        if (level().isClientSide)
        {
            return;
        }

        // So we stop moving while we do this cause I'm tired of this boss being pushed around
        this.getNavigation().stop();
        this.lerpMotion(0, 0, 0);

        targetSelector.tick();
        if (this.getTarget() != null)
        {
            this.lookControl.setLookAt(this.getTarget());
        }
        lookControl.tick();

        int tick = ROARING_ANIM_DURATION - halfHealthTimer;

        // Start shooting out shit
        if (tick == ROARING_STAR_SHOOT_TIMESTAMP)
        {
            this.darkStarAttackTimer = 0;
            this.darkStarWavesSpawned = 0;
        }

        if (tick == ROARING_PULL_IN_START_TIMESTAMP)
        {
            // Set phase
            setPhase(Phase.SecondPhase);
            // Set goals
            secondPhaseGoals();
            // Titan
            setTitan(true);
        }

        if (tick >= ROARING_STAR_SHOOT_TIMESTAMP && tick < ROARING_PULL_IN_START_TIMESTAMP)
        {
            this.darkStarAttackTimer++;

            if (this.darkStarWavesSpawned < DARK_STAR_COUNT && this.darkStarAttackTimer % DARK_STAR_INTERVAL_TICKS == 0)
            {
                spawnDarkStar(this.darkStarWavesSpawned);
                this.darkStarWavesSpawned++;
            }

            if (halfHealthTimer == 0 && this.darkStarWavesSpawned >= DARK_STAR_COUNT)
            {
                stopHalfHealthRoaring();
            }
        }
    }

    private void spawnDarkStar(int wave)
    {
        double baseAngle = Math.toRadians(wave * anglePerWave);

        for (int i = 0; i < DARK_STAR_PER_WAVE; i++)
        {
            double angle = baseAngle * i * (2 * Math.PI / DARK_STAR_PER_WAVE);

            DarkStarProjectileEntity darkStar = new DarkStarProjectileEntity(this.level(), angle, ROARING_STAR_SHOOT_TIMESTAMP, ROARING_PULL_IN_START_TIMESTAMP, this);
            darkStar.setOwner(this);
            darkStar.setPos(this.getX(), this.getEyeY(), this.getZ());
            darkStar.setDamage(25F);
            darkStar.setExplosionRadius(15.0F);

            this.level().addFreshEntity(darkStar);
        }
    }

    @Override
    public void tick() {
        super.tick();

        // These are used for getting health; very handy for doing phases based on health
        float health = this.getHealth();
        float MAX_HEALTH = this.getMaxHealth();

        float halfHealth = MAX_HEALTH/2;

        // Same as what Tyros has
        this.bossEvent.setProgress(health / MAX_HEALTH);

        if (isRoaring())
        {
            halfHealthTimer--;
            if (!level().isClientSide)
            {
                handleHalfHealthRoaring();
            }
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        float health = this.getHealth();
        float MAX_HEALTH = this.getMaxHealth();

        if (!hasPerformedHalfHealthRoar && health < MAX_HEALTH * 0.5F)
        {
            triggerHalfHealthRoaring();
        }
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || isRoaring();
    }

    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public void push(Entity entity) {
        if (!isRoaring())
        {
            super.push(entity);
        }
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void kill() {
        if (this.isDeadOrDying())
        {
            discard();
        }
        else {
            super.kill();
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isTitan())
        {
            amount *= 0.50f;
        }
        if (isRoaring())
        {
            // I don't want people nuking the boss while it's charging up
            amount *= 0.80f;
        }
        // damage limiter - yoinked from Tyros
        var limit = getMaxHealth() * 0.025f;
        if (amount > limit) {
            amount = limit + (amount - limit) * .3f; // damage about limit has .3x multiplier applied
        }

        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        if (this.isDeadOrDying() && !this.level().isClientSide)
        {
            this.stopHalfHealthRoaring();
            this.castComplete();
            this.attackGoal.stop();
            this.serverTriggerAnimation("death");
            this.serverTriggerEvent(STOP_MUSIC);
        }
    }

    @Override
    protected void tickDeath() {
        this.deathTime++;

        if (!level().isClientSide)
        {
            if (this.deathTime >= deathAnimationTime && !this.level().isClientSide() && !this.isRemoved())
            {
                if (this.deathLoot != null)
                {
                    deathLoot.getItems().forEach(this::spawnAtLocation);
                }
                this.remove(RemovalReason.KILLED);
            }
        }
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);

        // Up the difficulty based on players around the boss
        // Fuck the players, maaan
        List<? extends Player> nearbyPlayers = level.players().stream()
                .filter(player -> distanceToSqr(player) < 3600 && !player.isSpectator() && !player.isCreative())
                .toList();
        this.playerScale = nearbyPlayers.size();
        int extraPlayers = Math.max(0, playerScale - 1);
        double bonusHealthPerPlayer = extraPlayers * 0.4 + extraPlayers * extraPlayers * 0.10;

        if (bonusHealthPerPlayer != 0)
        {
            this.getAttribute(Attributes.ATTACK_DAMAGE).addPermanentModifier(new AttributeModifier(TheRoaringSpellbooks.id("player_scale"), bonusHealthPerPlayer, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
        this.setHealth(this.getMaxHealth());
        return spawnGroupData;
    }

    // Creates the entity attributes for the boss
    public static AttributeSupplier.Builder createAttributes()
    {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 20.0)
                .add(Attributes.ATTACK_KNOCKBACK, 0.8)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.MAX_HEALTH, 1000.0)
                .add(Attributes.ARMOR, 25)
                .add(Attributes.ARMOR_TOUGHNESS, 10)
                .add(Attributes.FOLLOW_RANGE, 85.0)
                .add(Attributes.ENTITY_INTERACTION_RANGE, 4.5)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(AttributeRegistry.SPELL_POWER, 1.6)
                .add(AttributeRegistry.SPELL_RESIST, 1.65)
                .add(AttributeRegistry.MAX_MANA, 1000)
                .add(ASAttributeRegistry.SPELL_RES_PENETRATION, 0.50)
                .add(ASAttributeRegistry.MANA_REND, 0.50)
                ;
    }

    // Setters & Getters
    // Phases
    public void setPhase(int phase) {
        this.entityData.set(PHASE, phase);
    }

    @Override
    public int getPhase() {
        return this.entityData.get(PHASE);
    }

    // Titan Mode
    public boolean isTitan() {
        return entityData.get(IS_TITAN);
    }

    public void setTitan(boolean titan) {
        entityData.set(IS_TITAN, titan);
    }

    // NBT
    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        // Phases
        pCompound.putInt("phase", getPhase());
        // Titan
        pCompound.putBoolean("titan", isTitan());
        // Half health roaring
        pCompound.putInt("halfHealthTimer", halfHealthTimer);
        pCompound.putBoolean("halfHealthAttack", hasPerformedHalfHealthRoar);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (this.hasCustomName())
        {
            this.bossEvent.setName(this.getDisplayName());
        }
        // Phases
        setPhase(pCompound.getInt("phase"));
        if (isPhase(Phase.SecondPhase))
        {
            secondPhaseGoals();
        }
        // Loot
        if (deathLoot != null)
        {
            pCompound.put("deathLootItems", deathLoot.createTag(this.registryAccess()));
        }
        if (pCompound.contains("deathLootItems", 9))
        {
            var tag = pCompound.getList("deathLootItems", 10);
            this.deathLoot = new SimpleContainer(tag.size());
            this.deathLoot.fromTag(tag, this.registryAccess());
        }

        // Roaring
        this.halfHealthTimer = pCompound.getInt("halfHealthTimer");
        this.hasPerformedHalfHealthRoar = pCompound.getBoolean("halfHealthAttack");

        // Titan
        setTitan(pCompound.getBoolean("titan"));

        // Boss Bar
        if (this.hasCustomName())
        {
            this.bossEvent.setName(this.getDisplayName());
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(PHASE, 0);
        pBuilder.define(IS_TITAN, false);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        if (!this.level().isClientSide)
        {
            createBossEvent();
        }
    }

    protected void createBossEvent()
    {
        this.bossEvent = (ExtendedServerBossEvent) (new ExtendedServerBossEvent(this.getUUID(), this.getDisplayName().copy().withStyle(ChatFormatting.WHITE/*, ChatFormatting.BOLD*/), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS)).setCreateWorldFog(false);
    }

    @Override
    public void calculateEntityAnimation(boolean includeHeight) {
        super.calculateEntityAnimation(false);
    }

    @Override
    public boolean bobBodyWhileWalking() {
        return !isAnimating();
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf registryFriendlyByteBuf) {

    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf registryFriendlyByteBuf) {

    }
}
