package net.acetheeldritchking.roaring_knight_iss.event;

import net.acetheeldritchking.roaring_knight_iss.TheRoaringSpellbooks;
import net.acetheeldritchking.roaring_knight_iss.entity.bosses.roaring_harbinger.RoaringHarbingerBoss;
import net.acetheeldritchking.roaring_knight_iss.registries.RKEntityRegistry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = TheRoaringSpellbooks.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class RKCommonSetup {
    @SubscribeEvent
    public static void onAttributeCreateEvent(EntityAttributeCreationEvent event)
    {
        event.put(RKEntityRegistry.BLACK_EXECUTIONER_BOSS.get(), RoaringHarbingerBoss.createAttributes().build());
    }
}
