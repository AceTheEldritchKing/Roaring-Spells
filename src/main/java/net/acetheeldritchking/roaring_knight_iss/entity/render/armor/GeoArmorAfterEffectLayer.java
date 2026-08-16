package net.acetheeldritchking.roaring_knight_iss.entity.render.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import net.acetheeldritchking.aces_spell_utils.items.example.items.armor.ExtendedGeoArmorItem;
import net.acetheeldritchking.roaring_knight_iss.TheRoaringSpellbooks;
import net.acetheeldritchking.roaring_knight_iss.registries.RKItemRegistry;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.*;

// Renderer from BielGG
// Credit goes to him, thank you again man for allowing me to use this
// https://www.curseforge.com/minecraft/mc-mods/bielggs-spells-addon
public class GeoArmorAfterEffectLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {
    private static final Map<UUID, TrailState> TRAILS = new HashMap<>();
    private static final ResourceLocation ARMOR_TEXTURE = ResourceLocation.fromNamespaceAndPath(TheRoaringSpellbooks.MOD_ID, "textures/models/armor/roaring_harbinger_armor.png");
    private final GenericCustomArmorRenderer renderer;

    public GeoArmorAfterEffectLayer(GenericCustomArmorRenderer renderer) {
        super(renderer);
        this.renderer = renderer;
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        //super.render(poseStack, animatable, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        //(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, MultiBufferSource bufferSource, float partialTick)
        this.renderAfterImages(poseStack, animatable, bakedModel, bufferSource, partialTick);
    }

    public static boolean hasCorrectArmorOn(Entity entity)
    {
        if (entity instanceof LivingEntity livingEntity)
        {
            for(ItemStack armorStack : livingEntity.getArmorSlots()) {
                if(!(armorStack.getItem() instanceof ArmorItem)) {
                    return false;
                }
            }

            Item boots = (livingEntity.getItemBySlot(EquipmentSlot.FEET)).getItem();
            Item leggings = (livingEntity.getItemBySlot(EquipmentSlot.LEGS)).getItem();
            Item chestplate = (livingEntity.getItemBySlot(EquipmentSlot.CHEST)).getItem();
            Item helmet = (livingEntity.getItemBySlot(EquipmentSlot.HEAD)).getItem();

            return boots == RKItemRegistry.ROARING_HARBINGER_BOOTS.get() && leggings == RKItemRegistry.ROARING_HARBINGER_LEGGINGS.get()
                    && chestplate == RKItemRegistry.ROARING_HARBINGER_CHESTPLATE.get() && helmet == RKItemRegistry.ROARING_HARBINGER_HELMET.get();
        } else
        {
            return false;
        }
    }

    private static boolean isActuallyMoving(Entity entity)
    {
        if (entity != null)
        {
            double dx = entity.getX() - entity.xo;
            double dy = entity.getY() - entity.yo;
            double dz = entity.getZ() - entity.zo;

            return dx * dx + dy * dy + dz * dz > 2.25E-4;
        } else
        {
            return false;
        }
    }

    private static float getBodyYaw(Entity entity, float partialTick)
    {
        if (entity instanceof LivingEntity livingEntity)
        {
            return Mth.rotLerp(partialTick, livingEntity.yBodyRotO, livingEntity.yBodyRot);
        } else
        {
            return Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
        }
    }

    private static Vec3 toArmorLocalDelta(Vec3 worldDelta, Entity entity, float partialTick) {
        float bodyYaw = getBodyYaw(entity, partialTick);
        Vec3 horizontalDelta = worldDelta.yRot((bodyYaw - 180.0F) * ((float)Math.PI / 180F));
        return new Vec3(horizontalDelta.x, -horizontalDelta.y, horizontalDelta.z);
    }

    private static TrailState updateTrail(Entity entity, float partialTick) {
        TrailState trail = TRAILS.computeIfAbsent(entity.getUUID(), (ignored) -> new TrailState());
        if (trail.lastTick == entity.tickCount) {
            return trail;
        } else {
            Vec3 current = entity.position();
            trail.samples.removeIf((sample) -> (float)(entity.tickCount - sample.tick) > 10.0F);
            if (trail.lastTickPos != null && current.distanceTo(trail.lastTickPos) < 0.015) {
                trail.samples.clear();
                trail.lastSpawnPos = current;
                trail.lastTickPos = current;
                trail.lastTick = entity.tickCount;
                return trail;
            } else {
                if (trail.lastSpawnPos == null) {
                    trail.lastSpawnPos = current;
                } else if (current.distanceTo(trail.lastSpawnPos) >= 0.08) {
                    trail.samples.addLast(new TrailSample(current.add((double)0.0F, 0.02, (double)0.0F), entity.tickCount, getBodyYaw(entity, partialTick)));
                    trail.lastSpawnPos = current;

                    while(trail.samples.size() > 6) {
                        trail.samples.pollFirst();
                    }
                }

                trail.lastTickPos = current;
                trail.lastTick = entity.tickCount;
                TRAILS.entrySet().removeIf((entry) -> (entry.getValue()).lastTick + 80 < entity.tickCount);
                return trail;
            }
        }
    }

    private void renderAfterImages(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, MultiBufferSource bufferSource, float partialTick) {
        Entity entity = this.renderer.getCurrentEntity();
        if (entity != null && hasCorrectArmorOn(entity)) {
            if (!isActuallyMoving(entity)) {
                TRAILS.remove(entity.getUUID());
            } else {
                TrailState trail = updateTrail(entity, partialTick);
                if (!trail.samples.isEmpty()) {
                    Vec3 current = entity.getPosition(partialTick);
                    RenderType trailType = RenderType.entityTranslucentEmissive(ARMOR_TEXTURE);
                    VertexConsumer trailBuffer = bufferSource.getBuffer(trailType);
                    int rendered = 0;
                    Iterator<TrailSample> iterator = trail.samples.iterator();
                    float currentYaw = getBodyYaw(entity, partialTick);

                    while(iterator.hasNext()) {
                        TrailSample sample = iterator.next();
                        int age = entity.tickCount - sample.tick;
                        if (age > 0) {
                            if ((float)age > 10.0F) {
                                iterator.remove();
                            } else {
                                ++rendered;
                                float life = Mth.clamp((float)age / 10.0F, 0.0F, 1.0F);
                                float fade = 1.0F - life;
                                fade *= fade;
                                float alpha = 0.42F * fade;
                                if (!(alpha <= 0.1F)) {
                                    float scale = Math.max(0.8F, 1.0F - 0.04F * (float)rendered);
                                    float pulse = 0.85F + 0.1F * (float)Math.sin((((float)age + (float)rendered * 0.6F) * 3.5F));
                                    float brightness = Math.min(1.0F, pulse + 0.05F);
                                    float red = Mth.lerp(0.55F, brightness * 0.95F, 1.0F);
                                    float green = Mth.lerp(0.55F, brightness * 0.97F, 1.0F);
                                    float blue = Mth.lerp(0.55F, brightness * 1.0F, 1.0F);

                                    int packedColor = FastColor.ARGB32.color(
                                            (int) (alpha * 255.0F),
                                            (int) (red * 255.0F),
                                            (int) (green * 255.0F),
                                            (int) (blue * 255.0F)
                                    );

                                    Vec3 localDelta = toArmorLocalDelta(sample.pos.subtract(current), entity, partialTick);
                                    poseStack.pushPose();
                                    poseStack.translate(localDelta.x, localDelta.y, localDelta.z);
                                    poseStack.mulPose(Axis.YP.rotationDegrees(sample.bodyYaw - currentYaw));
                                    poseStack.scale(scale, scale, scale);
                                    this.getRenderer().reRender(bakedModel, poseStack, bufferSource, (T) animatable, trailType, trailBuffer, partialTick, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, packedColor);
                                    poseStack.popPose();
                                    if (rendered >= 6) {
                                        break;
                                    }
                                }
                            }
                        }
                    }

                }
            }
        } else {
            if (entity != null) {
                TRAILS.remove(entity.getUUID());
            }

        }
    }

    private static class TrailState
    {
        private final Deque<TrailSample> samples = new ArrayDeque();
        private Vec3 lastSpawnPos;
        private Vec3 lastTickPos;
        private int lastTick = -1;

        private TrailState() {}
    }

    private static record TrailSample(
            Vec3 pos,
            int tick,
            float bodyYaw
    ) {}
}
