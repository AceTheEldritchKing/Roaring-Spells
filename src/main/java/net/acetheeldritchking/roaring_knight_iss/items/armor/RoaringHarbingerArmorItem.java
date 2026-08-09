package net.acetheeldritchking.roaring_knight_iss.items.armor;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.render.RenderHelper;
import net.acetheeldritchking.aces_spell_utils.entity.render.armor.EmissiveGenericCustomArmorRenderer;
import net.acetheeldritchking.roaring_knight_iss.TheRoaringSpellbooks;
import net.acetheeldritchking.roaring_knight_iss.entity.armor.RoaringHarbingerArmorModel;
import net.acetheeldritchking.roaring_knight_iss.entity.render.armor.RKCustomArmorRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class RoaringHarbingerArmorItem extends ImbuableRKArmorItem {

    public RoaringHarbingerArmorItem(Type type, Properties properties) {
        super(
                RKArmorMaterialRegistry.ROARING_HARBINGER,
                type,
                properties,
                schoolAttributesWithResistance(
                        AttributeRegistry.SPELL_POWER,
                        AttributeRegistry.SPELL_RESIST,
                        250,
                        0.20F,
                        0.05F,
                        0.10F
                )
        );
    }

    private static final ResourceLocation LAYER = ResourceLocation.fromNamespaceAndPath(
            TheRoaringSpellbooks.MOD_ID,
            "textures/models/armor/roaring_harbinger_armor_glowmask.png");

    @Override
    @OnlyIn(Dist.CLIENT)
    public GeoArmorRenderer<?> supplyRenderer() {
        RenderType GLOW_RENDER_TYPE = RenderType.breezeEyes(LAYER);

        //return new GenericCustomArmorRenderer<>(new RoaringHarbingerArmorModel());
        return new RKCustomArmorRenderer<>(new RoaringHarbingerArmorModel(), LAYER, GLOW_RENDER_TYPE);
    }
}
