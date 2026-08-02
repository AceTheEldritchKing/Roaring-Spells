package net.acetheeldritchking.roaring_knight_iss.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.acetheeldritchking.roaring_knight_iss.registries.RKParticleRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.StreamCodec;

//https://github.com/lender544/new1.20.1/blob/1.21/src/main/java/com/github/L_Ender/cataclysm/client/particle/Options/AfterImageParticleOptions.java
public class AfterImageParticleOptions implements ParticleOptions {
    int entityId;
    int r;
    int g;
    int b;
    boolean afterImage;
    int lifetime;

    public AfterImageParticleOptions(int entityId, int r, int g, int b, boolean afterImage, int lifetime) {
        this.entityId = entityId;
        this.r = r;
        this.g = g;
        this.b = b;
        this.afterImage = afterImage;
        this.lifetime = lifetime;
    }

    //For networking. Encoder/Decoder functions very intuitive
    public static StreamCodec<? super ByteBuf, AfterImageParticleOptions> STREAM_CODEC = StreamCodec.of(
            (buf, option) -> {
                buf.writeInt(option.entityId);
                buf.writeInt(option.r);
                buf.writeInt(option.g);
                buf.writeInt(option.b);
                buf.writeBoolean(option.afterImage);
                buf.writeInt(option.lifetime);
            },
            (buf) -> {
                return new AfterImageParticleOptions(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(),buf.readBoolean(), buf.readInt());
            }
    );

    //For command only?
    public static MapCodec<AfterImageParticleOptions> MAP_CODEC = RecordCodecBuilder.mapCodec(object ->
            object.group(
                    Codec.INT.fieldOf("entityid").forGetter(p -> (p).entityId),
                    Codec.INT.fieldOf("r").forGetter(p -> (p).r),
                    Codec.INT.fieldOf("g").forGetter(p -> (p).g),
                    Codec.INT.fieldOf("b").forGetter(p -> (p).b),
                    Codec.BOOL.fieldOf("afterimage").forGetter(p -> (p).afterImage),
                    Codec.INT.fieldOf("lifetime").forGetter(p -> (p).lifetime)
            ).apply(object, AfterImageParticleOptions::new
            ));

    @Override
    public ParticleType<?> getType() {
        return RKParticleRegistry.AFTER_IMAGE.get();
    }
}
