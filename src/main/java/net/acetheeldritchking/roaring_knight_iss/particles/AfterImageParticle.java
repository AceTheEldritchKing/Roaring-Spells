package net.acetheeldritchking.roaring_knight_iss.particles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import io.redspace.ironsspellbooks.render.RenderHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

//https://github.com/seymourimadeit/Piglin-Proliferation/blob/main/src/main/java/tallestred/piglinproliferation/client/particles/AfterImageParticle.java
//https://github.com/lender544/new1.20.1/blob/1.21/src/main/java/com/github/L_Ender/cataclysm/client/particle/AfterImageParticle.java
public class AfterImageParticle extends Particle {
    private final int entityId;
    private final boolean afterImage;

    public AfterImageParticle(ClientLevel world, double x, double y, double z, int r, int g, int b, int entityId, boolean afterImage, int lifetimes) {
        super(world, x, y, z);
        this.setSize(6.0F, 6.0F);
        this.x = x;
        this.y = y;
        this.z = z;
        this.rCol = r;
        this.gCol = g;
        this.bCol =  b;
        this.entityId = entityId;
        this.lifetime = lifetimes;
        this.afterImage = afterImage;
    }

    public @NotNull AABB getRenderBoundingBox(float partialTicks)
    {
        return getBoundingBox().inflate(0.0);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();

        }
        Entity from = this.getFromEntity();
        if(from == null){
            remove();
        }
    }

    public Entity getFromEntity() {
        return entityId == -1 ? null : level.getEntity(entityId);
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float v) {
        MultiBufferSource.BufferSource multiBufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        Vec3 cameraPos = camera.getPosition();
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();

        double lerpX = Mth.lerp(v, this.xo, this.x);
        double lerpY = Mth.lerp(v, this.yo, this.y);
        double lerpZ = Mth.lerp(v, this.zo, this.z);
        float colorR = this.rCol / 255.0F;
        float colorG = this.gCol / 255.0F;
        float colorB = this.bCol / 255.0F;
        float colorA = 0.5F / Math.abs((float) age + 1);

        PoseStack poseStack = new PoseStack();
        Entity entity = this.getFromEntity();

        if (entity instanceof LivingEntity livingEntity)
        {
            EntityRenderer<? super LivingEntity> renderRaw = dispatcher.getRenderer(livingEntity);

            Function<VertexConsumer, VertexConsumer> createTintedBuffer = (originalBuffer) -> new VertexConsumer()
            {
                @Override
                public VertexConsumer addVertex(float x, float y, float z) {
                    originalBuffer.addVertex(x, y, z);
                    return this;
                }

                @Override
                public VertexConsumer setColor(int r, int g, int b, int a) {
                    originalBuffer.setColor(
                            (int) (r * colorR),
                            (int) (g * colorG),
                            (int) (b * colorB),
                            (int) (a * colorA)
                    );
                    return this;
                }

                @Override
                public VertexConsumer setUv(float u, float v) {
                    originalBuffer.setUv(u, v);
                    return this;
                }

                @Override
                public VertexConsumer setUv1(int u, int v) {
                    originalBuffer.setUv1(u, v);
                    return this;
                }

                @Override
                public VertexConsumer setUv2(int u, int v) {
                    originalBuffer.setUv2(u, v);
                    return this;
                }

                @Override
                public VertexConsumer setNormal(float x, float y, float z) {
                    originalBuffer.setNormal(x, y, z);
                    return this;
                }
            };

            Minecraft minecraft = Minecraft.getInstance();
            boolean notInvis = !livingEntity.isInvisible();
            boolean notInvisToPlayer = !notInvis && !livingEntity.isInvisibleTo(minecraft.player);
            boolean shouldBeGlowing = minecraft.shouldEntityAppearGlowing(livingEntity);
            ResourceLocation texture = renderRaw.getTextureLocation(livingEntity);

            RenderType myDefaultType = RenderType.entityCutoutNoCull(texture);

            Map<RenderType, ResourceLocation> knownCutouts = collectKnownCutoutTypes(livingEntity, texture);

            MultiBufferSource tintedSource = (requestedType) ->
            {
                ResourceLocation matchedTexture = knownCutouts.get(requestedType);
                VertexConsumer originalBuffer;

                if (matchedTexture != null)
                {
                    RenderType ghostType = this.getRenderType(matchedTexture, notInvis, notInvisToPlayer, shouldBeGlowing);
                    originalBuffer = multiBufferSource.getBuffer(ghostType != null ? ghostType : requestedType);
                }
                else
                {
                    originalBuffer = multiBufferSource.getBuffer(requestedType);
                }

                return createTintedBuffer.apply(originalBuffer);
            };

            dispatcher.render(livingEntity,
                    lerpX - cameraPos.x(), lerpY - cameraPos.y(), lerpZ - cameraPos.z(),
                    livingEntity.getYRot(), v, poseStack, tintedSource, dispatcher.getPackedLightCoords(livingEntity, v));
        }
    }

    public RenderType getRenderType(ResourceLocation resourceLocation, boolean flag1, boolean flag2, boolean flag3)
    {
        if (flag2)
        {
            return RenderType.itemEntityTranslucentCull(resourceLocation);
        } else if (flag1)
        {
            return this.afterImage ? RenderHelper.CustomerRenderType.magic(resourceLocation) : RenderType.entityTranslucent(resourceLocation);
        } else
        {
            return flag3 ? RenderType.outline(resourceLocation) : null;
        }
    }

    private Map<RenderType, ResourceLocation> collectKnownCutoutTypes(LivingEntity livingEntity, ResourceLocation skinTexture) {
        Map<RenderType, ResourceLocation> known = new HashMap<>();
        known.put(RenderType.entityCutoutNoCull(skinTexture), skinTexture);

        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack stack = livingEntity.getItemBySlot(slot);
            if (stack.isEmpty()) continue;

            HumanoidModel<?> model = IClientItemExtensions.of(stack).getHumanoidArmorModel(livingEntity, stack, slot, null);

            if (model instanceof GeoArmorRenderer<?> armorRenderer) {
                try {
                    @SuppressWarnings({"unchecked", "rawtypes"})
                    ResourceLocation armorTexture = ((GeoArmorRenderer) armorRenderer).getTextureLocation((GeoAnimatable) stack.getItem());
                    known.put(RenderType.armorCutoutNoCull(armorTexture), armorTexture);
                } catch (Exception ignored) {
                    // nada here...
                }
            }
        }

        return known;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<AfterImageParticleOptions>
    {
        @Override
        public Particle createParticle(AfterImageParticleOptions data, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            AfterImageParticle particle;
            particle = new AfterImageParticle(level, x, y, z, data.r, data.g, data.b, data.entityId, data.afterImage, data.lifetime);
            return particle;
        }
    }
}
