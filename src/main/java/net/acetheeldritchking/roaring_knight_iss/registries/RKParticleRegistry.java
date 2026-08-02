package net.acetheeldritchking.roaring_knight_iss.registries;

import com.mojang.serialization.MapCodec;
import net.acetheeldritchking.roaring_knight_iss.TheRoaringSpellbooks;
import net.acetheeldritchking.roaring_knight_iss.particles.AfterImageParticleOptions;
import net.acetheeldritchking.roaring_knight_iss.particles.RedCleaveParticleOptions;
import net.acetheeldritchking.roaring_knight_iss.particles.SwoonParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class RKParticleRegistry {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, TheRoaringSpellbooks.MOD_ID);

    // Red Cleave Slash
    public static final Supplier<ParticleType<RedCleaveParticleOptions>> RED_CLEAVE_PARTICLE = PARTICLE_TYPES.register("red_cleave",
            () -> new ParticleType<>(true) {
                @Override
                public MapCodec<RedCleaveParticleOptions> codec() {
                    return RedCleaveParticleOptions.MAP_CODEC;
                }

                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, RedCleaveParticleOptions> streamCodec() {
                    return RedCleaveParticleOptions.STREAM_CODEC;
                }
            });

    // Swoon
    public static final Supplier<ParticleType<SwoonParticleOptions>> SWOON_PARTICLE = PARTICLE_TYPES.register("swoon",
            () -> new ParticleType<>(true) {
                @Override
                public MapCodec<SwoonParticleOptions> codec() {
                    return SwoonParticleOptions.MAP_CODEC;
                }

                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, SwoonParticleOptions> streamCodec() {
                    return SwoonParticleOptions.STREAM_CODEC;
                }
            });

    // Large Dark Bubble
    public static final Supplier<SimpleParticleType> LARGE_DARK_BUBBLE_PARTICLE = PARTICLE_TYPES.register("large_dark_bubble", () -> new SimpleParticleType(false));

    // Dark Bubble
    public static final Supplier<SimpleParticleType> DARK_BUBBLE_PARTICLE = PARTICLE_TYPES.register("dark_bubble", () -> new SimpleParticleType(false));

    // Dark Star
    public static final Supplier<SimpleParticleType> DARK_STAR_PARTICLE = PARTICLE_TYPES.register("dark_star", () -> new SimpleParticleType(false));

    // Red Sparkle
    public static final Supplier<SimpleParticleType> RED_SPARKLE_PARTICLE = PARTICLE_TYPES.register("red_sparkle", () -> new SimpleParticleType(false));

    // Star Sparkle
    public static final Supplier<SimpleParticleType> STAR_SPARKLE_PARTICLE = PARTICLE_TYPES.register("star_sparkle", () -> new SimpleParticleType(false));

    public static final DeferredHolder<ParticleType<?>, ParticleType<AfterImageParticleOptions>> AFTER_IMAGE = PARTICLE_TYPES.register("after_image", () -> new ParticleType<>(false)  {
        @Override
        public MapCodec<AfterImageParticleOptions> codec() {
            return AfterImageParticleOptions.MAP_CODEC;
        }
        public StreamCodec<? super RegistryFriendlyByteBuf, AfterImageParticleOptions> streamCodec() {
            return AfterImageParticleOptions.STREAM_CODEC;
        }
    });

    public static void register(IEventBus eventBus)
    {
        PARTICLE_TYPES.register(eventBus);
    }
}
