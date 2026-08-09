package net.acetheeldritchking.roaring_knight_iss.entity.render.armor;

import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import net.acetheeldritchking.aces_spell_utils.entity.render.armor.GeoArmorGlowmaskLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.model.GeoModel;

public class RKCustomArmorRenderer<T extends Item & GeoItem> extends GenericCustomArmorRenderer<T> {
    public RKCustomArmorRenderer(GeoModel<T> model, ResourceLocation glowLayer, RenderType renderType) {
        super(model);
        this.addRenderLayer(new GeoArmorGlowmaskLayer(this, glowLayer, renderType));
        this.addRenderLayer(new GeoArmorAfterEffectLayer<>(this));
    }

    public RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(this.getTextureLocation(animatable));
    }
}
