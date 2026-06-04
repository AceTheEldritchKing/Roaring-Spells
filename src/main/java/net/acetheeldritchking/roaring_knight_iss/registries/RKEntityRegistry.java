package net.acetheeldritchking.roaring_knight_iss.registries;

import net.acetheeldritchking.roaring_knight_iss.TheRoaringSpellbooks;
import net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.RoaringHarbingerBoss;
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

    // Black Executioner Boss
    public static final DeferredHolder<EntityType<?>, EntityType<RoaringHarbingerBoss>> BLACK_EXECUTIONER_BOSS =
            ENTITIES.register("harbinger_of_roaring", () -> EntityType.Builder.<RoaringHarbingerBoss>of(RoaringHarbingerBoss::new, MobCategory.MONSTER)
                    .sized(1.6f, 4.8f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(TheRoaringSpellbooks.MOD_ID, "harbinger_of_roaring").toString())
            );

    public static void register(IEventBus eventBus)
    {
        ENTITIES.register(eventBus);
    }
}
