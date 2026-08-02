package net.acetheeldritchking.roaring_knight_iss.utils;

import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class RKServerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // Sword Surround Speed
    private static final ModConfigSpec.ConfigValue<Double> SWORD_SURROUND_SPEED = BUILDER
            .comment("Defines the speed for the Roaring Knight's swords for its sword surround goal")
            .comment("Default is [5.5F]")
            .define("Sword Surround Speed", 0.8);

    // Sword Spread Speed
    private static final ModConfigSpec.ConfigValue<Double> SWORD_SPREAD_SPEED = BUILDER
            .comment("Defines the speed for the Roaring Knight's swords for its sword spread goal")
            .comment("Default is [5.5F]")
            .define("Sword Spread Speed", 0.7);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static double swordSurroundSpeed;
    public static double swordSpreadSpeed;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        swordSurroundSpeed = SWORD_SURROUND_SPEED.get();
        swordSpreadSpeed = SWORD_SPREAD_SPEED.get();
    }
}
