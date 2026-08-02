package net.acetheeldritchking.roaring_knight_iss.items.armor;

import com.google.common.collect.ImmutableMap;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.acetheeldritchking.aces_spell_utils.entity.armor.ExampleWarlockMaskModel;
import net.acetheeldritchking.aces_spell_utils.entity.render.armor.EmissiveGenericCustomArmorRenderer;
import net.acetheeldritchking.roaring_knight_iss.TheRoaringSpellbooks;
import net.acetheeldritchking.roaring_knight_iss.entity.armor.RoaringHarbingerArmorModel;
import net.acetheeldritchking.roaring_knight_iss.registries.RKItemRegistry;
import net.acetheeldritchking.roaring_knight_iss.utils.RKUtils;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.List;
import java.util.Map;

public class RoaringHarbingerArmorItem extends ImbuableRKArmorItem {
    private static final Map<Holder<ArmorMaterial>, List<MobEffectInstance>> MATERIAL_TO_EFFECT_MAP =
            (new ImmutableMap.Builder<Holder<ArmorMaterial>, List<MobEffectInstance>>())
                    .put(RKArmorMaterialRegistry.ROARING_HARBINGER,
                            List.of(new MobEffectInstance(MobEffects.GLOWING, 200, 1, false, false)))
                    .build();

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

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if(entity instanceof Player player && !level.isClientSide() && hasFullArmorSetOn(player)) {
            evalArmorEffects(player);
        }
    }

    private void evalArmorEffects(Player player)
    {
        for(Map.Entry<Holder<ArmorMaterial>, List<MobEffectInstance>> entry : MATERIAL_TO_EFFECT_MAP.entrySet()) {
            Holder<ArmorMaterial> mapArmorMaterial = entry.getKey();
            List<MobEffectInstance> mapEffect = entry.getValue();

            if(hasCorrectArmorOn(mapArmorMaterial, player)) {
                RKUtils.spawnAfterImageParticle(-.5F, 255, 255, 255, player);
            }
        }
    }

    public boolean hasCorrectArmorOn(Holder<ArmorMaterial> armorMaterial, Player player)
    {
        for(ItemStack armorStack : player.getArmorSlots()) {
            if(!(armorStack.getItem() instanceof ArmorItem)) {
                return false;
            }
        }

        ArmorItem boots = ((ArmorItem) player.getInventory().getArmor(0).getItem());
        ArmorItem leggings = ((ArmorItem) player.getInventory().getArmor(1).getItem());
        ArmorItem chestplate = ((ArmorItem) player.getInventory().getArmor(2).getItem());
        ArmorItem helmet = ((ArmorItem) player.getInventory().getArmor(3).getItem());

        return boots.getMaterial() == armorMaterial && leggings.getMaterial() == armorMaterial
                && chestplate.getMaterial() == armorMaterial && helmet.getMaterial() == armorMaterial;
    }

    private boolean hasFullArmorSetOn(Player player)
    {
        ItemStack boots = player.getInventory().getArmor(0);
        ItemStack leggings = player.getInventory().getArmor(1);
        ItemStack chestplate = player.getInventory().getArmor(2);
        ItemStack helmet = player.getInventory().getArmor(3);

        return !boots.isEmpty() && !leggings.isEmpty() && !chestplate.isEmpty() && !helmet.isEmpty();
    }

    private static final ResourceLocation LAYER = ResourceLocation.fromNamespaceAndPath(
            TheRoaringSpellbooks.MOD_ID,
            "textures/models/armor/roaring_harbinger_armor_glowmask.png");

    @Override
    @OnlyIn(Dist.CLIENT)
    public GeoArmorRenderer<?> supplyRenderer() {
        RenderType GLOW_RENDER_TYPE = RenderType.breezeEyes(LAYER);

        return new EmissiveGenericCustomArmorRenderer<>(new RoaringHarbingerArmorModel(), LAYER, GLOW_RENDER_TYPE);
    }
}
