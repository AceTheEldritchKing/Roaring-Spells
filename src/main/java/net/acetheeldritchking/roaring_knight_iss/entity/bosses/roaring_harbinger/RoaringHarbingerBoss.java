package net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.util.BossbarManager;
import io.redspace.ironsspellbooks.entity.mobs.dead_king_boss.DeadKingBoss;
import io.redspace.ironsspellbooks.entity.mobs.goals.MomentHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.SpellBarrageGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackAnimationData;
import io.redspace.ironsspellbooks.entity.mobs.keeper.KeeperEntity;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.ExtendedServerBossEvent;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.FireBossEntity;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.NotIdioticNavigation;
import io.redspace.ironsspellbooks.entity.mobs.wizards.priest.PriestEntity;
import io.redspace.ironsspellbooks.network.EntityEventPacket;
import net.acetheeldritchking.aces_spell_utils.entity.mobs.GenericUniqueBossEntity;
import net.acetheeldritchking.aces_spell_utils.entity.mobs.goals.WizardSpellComboGoal;
import net.acetheeldritchking.aces_spell_utils.registries.ASAttributeRegistry;
import net.acetheeldritchking.aces_spell_utils.utils.boss_music.UniqueBossMusicManager;
import net.acetheeldritchking.roaring_knight_iss.TheRoaringSpellbooks;
import net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.goals.RoaringHarbingerAttackGoal;
import net.acetheeldritchking.roaring_knight_iss.registries.RKEntityRegistry;
import net.acetheeldritchking.roaring_knight_iss.registries.RKSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
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
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class RoaringHarbingerBoss extends GenericUniqueBossEntity {
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
    private static final BossbarManager.BossbarSprite BOSSBAR_SPRITE = new BossbarManager.BossbarSprite(TheRoaringSpellbooks.id("boss_bars/black_executioner_boss_bar"), 192, 18, 3, -1);

    // These are used for doing boss bars, setting up the phase serializer for NBT, and stopping and starting music
    private ExtendedServerBossEvent bossEvent;
    private final static EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(RoaringHarbingerBoss.class, EntityDataSerializers.INT);
    private final static EntityDataAccessor<Integer> RAGE_METER = SynchedEntityData.defineId(RoaringHarbingerBoss.class, EntityDataSerializers.INT);
    private final static EntityDataAccessor<Boolean> ENRAGED = SynchedEntityData.defineId(RoaringHarbingerBoss.class, EntityDataSerializers.BOOLEAN);
    private final static EntityDataAccessor<Boolean> TORMENT_MODE = SynchedEntityData.defineId(RoaringHarbingerBoss.class, EntityDataSerializers.BOOLEAN);
    public static final byte CLIENT_STOP_TRACKING = 0;
    public static final byte CLIENT_START_TRACKING = 1;
    public static final byte PROC_PARRY = 2;
    public static final byte PROC_RAGE_QUIT = 3;
    public static final byte START_MUSIC = 4;
    public static final byte STOP_MUSIC = 5;

    // Boss music
    public static SoundEvent bossMusic = RKSoundEvents.BLACK_EXECUTIONER_THEME.get();
    public static SoundEvent bossTransitionMusic = RKSoundEvents.BLACK_EXECUTIONER_THEME.get();
    public static SoundEvent bossFinalMusic = RKSoundEvents.BLACK_EXECUTIONER_THEME.get();

    // Animation ticks
    public int transitionAnimationTime = 180;
    public int deathAnimationTime = 360;
    int spawnTimer;
    private static final int spawnAnimTime = (int) (7.59 * 20);
    private static final int spawnDelay = 20;
    int parryTime;
    int parryCooldown;
    // The amount of time it takes to build up rage again
    int rageTime;
    int rageCooldown;
    public float animDampener;

    // Loot
    SimpleContainer deathLoot = null;

    // Block Destroying
    private int destroyBlockDelay;
    private int stuckDetectorDelay;
    private int stuckDetector;
    private Vec3 lastStuck = Vec3.ZERO;

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
                /*if (!isSpawning())
                {
                    BossMusicManager.createOrResumeInstance(this);
                }*/
            }
            //case PROC_PARRY -> procParry();
            //case PROC_RAGE_QUIT -> procEnraged();
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
        // She HATE these guys
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, KeeperEntity.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PriestEntity.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, FireBossEntity.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, DeadKingBoss.class, true));
    }

    RoaringHarbingerAttackGoal attackGoal = new RoaringHarbingerAttackGoal(this, 1.5F, 25, 40);

    // First Phase
    private void firstPhaseGoals()
    {
        this.goalSelector.getAvailableGoals().forEach(WrappedGoal::stop);
        this.goalSelector.removeAllGoals((x) -> true);

        this.goalSelector.addGoal(1, new FloatGoal(this));

        this.goalSelector.addGoal(2, new SpellBarrageGoal(this, SpellRegistry.ELDRITCH_BLAST_SPELL.get(), 1, 3, 80, 150, 3));
        this.goalSelector.addGoal(4, new SpellBarrageGoal(this, SpellRegistry.TELEPORT_SPELL.get(), 1, 3, 80, 150, 3));

        this.attackGoal = (RoaringHarbingerAttackGoal) new RoaringHarbingerAttackGoal(this, 1.5F, 25, 40)
                .setMoveset(List.of(
                        new AttackAnimationData(42, "slash_1", 22),
                        new AttackAnimationData(51, "slash_2", 26)
                ))
                .setComboChance(0.8F)
                .setMeleeAttackInverval(50, 80)
                .setMeleeMovespeedModifier(1.5F)
                .setMeleeBias(0.0f, 0.0f)
                .setSpells(
                        // Attack
                        List.of(
                                SpellRegistry.SHADOW_SLASH.get()
                        ),
                        // Defense
                        List.of(
                                SpellRegistry.COUNTERSPELL_SPELL.get(),
                                SpellRegistry.CHARGE_SPELL.get(),
                                SpellRegistry.ABYSSAL_SHROUD_SPELL.get()
                        ),
                        // Movement
                        List.of(
                                SpellRegistry.SHADOW_SLASH.get()
                        ),
                        // Support
                        List.of(
                                SpellRegistry.COUNTERSPELL_SPELL.get()
                        )
                ).setSingleUseSpell(SpellRegistry.SONIC_BOOM_SPELL.get(), 70, 100, 3, 5)
                .setSpellQuality(1.0f, 1.0f);

        this.goalSelector.addGoal(3, attackGoal);

        this.goalSelector.addGoal(3, new WizardSpellComboGoal(this,
                List.of(
                        SpellRegistry.TELEPORT_SPELL.get(),
                        SpellRegistry.COUNTERSPELL_SPELL.get(),
                        SpellRegistry.SHADOW_SLASH.get()
                ), 1.3f, 1.3f, 80, 150));

        this.goalSelector.addGoal(5, new PatrolNearLocationGoal(this, 32.0F, 0.9));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
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
    public void kill() {
        if (this.isDeadOrDying())
        {
            discard();
        }
        else {
            super.kill();
        }
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
    @Override
    public void setPhase(int phase) {
        this.entityData.set(PHASE, phase);
    }

    @Override
    public int getPhase() {
        return this.entityData.get(PHASE);
    }

    // NBT
    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        // Phases
        pCompound.putInt("phase", getPhase());
        // Rage
        //pCompound.putInt("rage_meter", getRageMeter());
        //pCompound.putBoolean("is_enraged", getEnraged());
        // Torment Mode
        //pCompound.putBoolean("is_torment_mode", getTormentMode());

        if (rageTime > 0)
        {
            pCompound.putInt("rage_timer", rageTime);
        }
        if (rageCooldown > 0)
        {
            pCompound.putInt("rage_cooldown", rageCooldown);
        }
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
        /*if (isPhase(GenericBossEntity.Phase.SecondPhase))
        {
            secondPhaseGoals();
        }
        if (isPhase(GenericBossEntity.Phase.FourthPhase))
        {
            finalPhaseGoals();
        }*/
        // Loot
        if (deathLoot != null)
        {
            pCompound.put("deathLootItems", deathLoot.createTag(this.registryAccess()));
        }
        // Loot
        if (pCompound.contains("deathLootItems", 9))
        {
            var tag = pCompound.getList("deathLootItems", 10);
            this.deathLoot = new SimpleContainer(tag.size());
            this.deathLoot.fromTag(tag, this.registryAccess());
        }

        // Boss Bar
        if (this.hasCustomName())
        {
            this.bossEvent.setName(this.getDisplayName());
        }

        // Rage
        /*setRageMeter(pCompound.getInt("rage_meter"));
        setEnraged(pCompound.getBoolean("is_enraged"));

        int rageTime = pCompound.getInt("rage_timer");
        if (rageTime > 0)
        {
            this.rageTime = rageTime;
        }

        int rageCooldown = pCompound.getInt("rage_cooldown");
        if (rageCooldown > 0)
        {
            this.rageCooldown = rageCooldown;
        }

        // Torment Mode
        setTormentMode(pCompound.getBoolean("is_torment_mode"));*/
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(PHASE, 0);
        pBuilder.define(ENRAGED, false);
        pBuilder.define(RAGE_METER, 0);
        pBuilder.define(TORMENT_MODE, false);
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
        this.bossEvent = (ExtendedServerBossEvent) (new ExtendedServerBossEvent(this.getUUID(), this.getDisplayName().copy().withStyle(ChatFormatting.DARK_AQUA/*, ChatFormatting.BOLD*/), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS)).setCreateWorldFog(false);
    }

    @Override
    public void calculateEntityAnimation(boolean includeHeight) {
        super.calculateEntityAnimation(false);
    }

    @Override
    public boolean bobBodyWhileWalking() {
        return !isAnimating();
    }
}
