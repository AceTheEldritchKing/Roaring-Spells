package net.acetheeldritchking.roaring_knight_iss.event;

import net.acetheeldritchking.roaring_knight_iss.TheRoaringSpellbooks;
import net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.RoaringHarbingerBoss;
import net.acetheeldritchking.roaring_knight_iss.registries.RKEntityRegistry;
import net.acetheeldritchking.roaring_knight_iss.registries.RKItemRegistry;
import net.acetheeldritchking.roaring_knight_iss.utils.RKUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = TheRoaringSpellbooks.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class RKCommonSetup {
    @SubscribeEvent
    public static void onAttributeCreateEvent(EntityAttributeCreationEvent event)
    {
        event.put(RKEntityRegistry.BLACK_EXECUTIONER_BOSS.get(), RoaringHarbingerBoss.createAttributes().build());
    }

    @SubscribeEvent
    public static void onPlayerTickEvent(PlayerTickEvent.Pre event)
    {
        Player player = event.getEntity();

        /*if (hasCorrectArmorOn(player))
        {
            RKUtils.spawnAfterImageParticle(-.5F, 255, 255, 255, player);
        }*/
    }

    public static boolean hasCorrectArmorOn(Player player)
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

        return boots == RKItemRegistry.ROARING_HARBINGER_BOOTS.get() && leggings == RKItemRegistry.ROARING_HARBINGER_LEGGINGS.get()
                && chestplate == RKItemRegistry.ROARING_HARBINGER_CHESTPLATE.get() && helmet == RKItemRegistry.ROARING_HARBINGER_HELMET.get();
    }
}
