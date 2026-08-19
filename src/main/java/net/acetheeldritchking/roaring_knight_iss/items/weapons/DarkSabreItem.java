package net.acetheeldritchking.roaring_knight_iss.items.weapons;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.util.RaycastBuilder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.acetheeldritchking.aces_spell_utils.items.weapons.ActiveAndPassiveAbilityMagicSwordItem;
import net.acetheeldritchking.aces_spell_utils.utils.ASRarities;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.acetheeldritchking.roaring_knight_iss.entity.spells.dark_fountain.DarkFountainSpireEntity;
import net.acetheeldritchking.roaring_knight_iss.registries.RKItemRegistry;
import net.acetheeldritchking.roaring_knight_iss.registries.RKSpellRegistries;
import net.acetheeldritchking.roaring_knight_iss.utils.RKRarities;
import net.acetheeldritchking.roaring_knight_iss.utils.RKUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class DarkSabreItem extends ActiveAndPassiveAbilityMagicSwordItem {
    public DarkSabreItem() {
        super(
                RKWeaponTier.DARK_SABRE,
                ItemPropertiesHelper.equipment(1).fireResistant().rarity(RKRarities.SHADED_RARITY_PROXY.getValue()).attributes(ExtendedSwordItem.createAttributes(RKWeaponTier.DARK_SABRE)),
                SpellDataRegistryHolder.of(
                        new SpellDataRegistryHolder(RKSpellRegistries.KNIGHTS_EDGE, 1))
        );
    }

    public static final int ACTIVE_COOLDOWN = 2 * 20;

    @Override
    protected int getActiveCooldownTicks() {
        return ACTIVE_COOLDOWN;
    }

    @Override
    protected int getPassiveCooldownTicks() {
        return 10 * 20;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPYGLASS;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 3 * 20;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        if (livingEntity instanceof Player player)
        {
            // I am looking at how the Incinerator is done because getting it to only consume soul fire stacks when completed is making me shit myself
            int ticks = getUseDuration(stack, player) - timeCharged;
            boolean success = false;

            Vec3 spawn = null;
            HitResult raycast = RaycastBuilder.begin(level, player)
                    .range(48)
                    .checkForBlocks(true)
                    .build();
            if (raycast.getType() == HitResult.Type.ENTITY) {
                spawn = ((EntityHitResult) raycast).getEntity().position();
            } else {
                spawn = Utils.moveToRelativeGroundLevel(level, raycast.getLocation().subtract(player.getForward().normalize()).add(0, 2, 0), 3, 18);
            }

            livingEntity.level().playLocalSound(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundRegistry.FLAMING_STRIKE_SWING.get(), SoundSource.PLAYERS, 1, 1, false);

            // Fountain
            DarkFountainSpireEntity darkFountainSpire = new DarkFountainSpireEntity(level, false);
            darkFountainSpire.setOwner(player);
            darkFountainSpire.moveTo(spawn);
            darkFountainSpire.setDamage(10F);

            level.addFreshEntity(darkFountainSpire);

            player.getCooldowns().addCooldown(RKItemRegistry.DARK_SABRE.get(), DarkSabreItem.ACTIVE_COOLDOWN);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack mainhandItem = player.getMainHandItem();

        player.startUsingItem(usedHand);
        return InteractionResultHolder.consume(mainhandItem);
    }
}
