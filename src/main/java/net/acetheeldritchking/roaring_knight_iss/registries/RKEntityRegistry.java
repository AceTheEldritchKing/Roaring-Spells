package net.acetheeldritchking.roaring_knight_iss.registries;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.spells.magma_ball.FireField;
import net.acetheeldritchking.roaring_knight_iss.TheRoaringSpellbooks;
import net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.RoaringHarbingerBoss;
import net.acetheeldritchking.roaring_knight_iss.entity.spells.dark_fountain.DarkFountainEntity;
import net.acetheeldritchking.roaring_knight_iss.entity.spells.dark_fountain.DarkFountainSpireEntity;
import net.acetheeldritchking.roaring_knight_iss.entity.spells.dark_sabre_projectile.DarkSabreProjectile;
import net.acetheeldritchking.roaring_knight_iss.entity.spells.star_projectile.DarkStarProjectileEntity;
import net.acetheeldritchking.roaring_knight_iss.entity.spells.star_shrapnel.DarkStarShrapnelProjectileEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RKEntityRegistry {
    private static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, TheRoaringSpellbooks.MOD_ID);

    // Roaring Harbinger Boss
    public static final DeferredHolder<EntityType<?>, EntityType<RoaringHarbingerBoss>> BLACK_EXECUTIONER_BOSS =
            ENTITIES.register("harbinger_of_roaring", () -> EntityType.Builder.<RoaringHarbingerBoss>of(RoaringHarbingerBoss::new, MobCategory.MONSTER)
                    .sized(1.6f, 4.8f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(TheRoaringSpellbooks.MOD_ID, "harbinger_of_roaring").toString())
            );

    // Dark Sabre Projectile
    public static final DeferredHolder<EntityType<?>, EntityType<DarkSabreProjectile>> DARK_SABRE_PROJECTILE =
            ENTITIES.register("dark_sabre_projectile", () -> EntityType.Builder.<DarkSabreProjectile>of(DarkSabreProjectile::new, MobCategory.MISC)
                    .sized(1.6f, 1.6f)
                    .clientTrackingRange(64)
                    .updateInterval(1)
                    .build(ResourceLocation.fromNamespaceAndPath(TheRoaringSpellbooks.MOD_ID, "dark_sabre_projectile").toString())
            );

    // Dark Star Projectile
    public static final DeferredHolder<EntityType<?>, EntityType<DarkStarProjectileEntity>> DARK_STAR_PROJECTILE =
            ENTITIES.register("dark_star", () -> EntityType.Builder.<DarkStarProjectileEntity>of(DarkStarProjectileEntity::new, MobCategory.MISC)
                    .sized(1.6f, 1.6f)
                    .clientTrackingRange(64)
                    .updateInterval(1)
                    .build(ResourceLocation.fromNamespaceAndPath(TheRoaringSpellbooks.MOD_ID, "dark_star").toString())
            );

    // Dark Star Shrapnel Projectile
    public static final DeferredHolder<EntityType<?>, EntityType<DarkStarShrapnelProjectileEntity>> DARK_STAR_SHRAPNEL =
            ENTITIES.register("dark_star_shrapnel", () -> EntityType.Builder.<DarkStarShrapnelProjectileEntity>of(DarkStarShrapnelProjectileEntity::new, MobCategory.MISC)
                    .sized(1.0f, 1.0f)
                    .clientTrackingRange(64)
                    .updateInterval(1)
                    .build(ResourceLocation.fromNamespaceAndPath(TheRoaringSpellbooks.MOD_ID, "dark_star_shrapnel").toString())
            );

    // Dark Fountain
    public static final DeferredHolder<EntityType<?>, EntityType<DarkFountainEntity>> DARK_FOUNTAIN =
            ENTITIES.register("dark_fountian", () -> EntityType.Builder.<DarkFountainEntity>of(DarkFountainEntity::new, MobCategory.MISC)
                    .sized(4f, 1.2f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(TheRoaringSpellbooks.MOD_ID, "dark_fountian").toString()));

    // Dark Tower
    public static final DeferredHolder<EntityType<?>, EntityType<DarkFountainSpireEntity>> DARK_FOUNTAIN_SPIRE =
            ENTITIES.register("dark_fountian_spire", () -> EntityType.Builder.<DarkFountainSpireEntity>of(DarkFountainSpireEntity::new, MobCategory.MISC)
                    .sized(1.5f, 14f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(TheRoaringSpellbooks.MOD_ID, "dark_fountian_spire").toString()));

    public static void register(IEventBus eventBus)
    {
        ENTITIES.register(eventBus);
    }
}
