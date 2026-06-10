package net.acetheeldritchking.roaring_knight_iss.registries;

import com.mojang.serialization.MapCodec;
import net.acetheeldritchking.roaring_knight_iss.TheRoaringSpellbooks;
import net.acetheeldritchking.roaring_knight_iss.particle.RedCleaveParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class RKParticleRegistry {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, TheRoaringSpellbooks.MOD_ID);

    // Soul Fire Slash
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

    public static void register(IEventBus eventBus)
    {
        PARTICLE_TYPES.register(eventBus);
    }
}
