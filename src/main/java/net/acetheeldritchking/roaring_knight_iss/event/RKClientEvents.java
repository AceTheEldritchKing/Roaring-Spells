package net.acetheeldritchking.roaring_knight_iss.event;

import net.acetheeldritchking.roaring_knight_iss.TheRoaringSpellbooks;
import net.acetheeldritchking.roaring_knight_iss.entity.render.entity.bosses.black_executioner.BlackExecutionerRenderer;
import net.acetheeldritchking.roaring_knight_iss.entity.render.spells.dark_sabre_projectile.DarkSabreProjectileRenderer;
import net.acetheeldritchking.roaring_knight_iss.registries.RKEntityRegistry;
import net.acetheeldritchking.roaring_knight_iss.utils.RKRarities;
import net.acetheeldritchking.roaring_knight_iss.utils.UniqueRarityColorHelper;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = TheRoaringSpellbooks.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RKClientEvents {
    // Entity Rendering
    @SubscribeEvent
    public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(RKEntityRegistry.BLACK_EXECUTIONER_BOSS.get(), BlackExecutionerRenderer::new);
        event.registerEntityRenderer(RKEntityRegistry.DARK_SABRE_PROJECTILE.get(), DarkSabreProjectileRenderer::new);
    }

    // Item Rarity rendering
    @SubscribeEvent
    public static void onClientTickEvent(ClientTickEvent.Post event)
    {
        UniqueRarityColorHelper.clientTick++;
    }

    @SubscribeEvent
    public static void applyRarityGradientEvent(ItemTooltipEvent event)
    {
        ItemStack itemStack = event.getItemStack();

        if (!event.getToolTip().isEmpty() && itemStack.getRarity() == RKRarities.SHADED_RARITY_PROXY.getValue())
        {
            int COLOR_RED = 0xd8d6e9;
            int COLOR_DEEP_RED = 0x0e0917;
            int COLOR_BRIGHT_RED_PINK = 0xffffff;

            String rawName = itemStack.getHoverName().getString();
            event.getToolTip().set(0, UniqueRarityColorHelper.createWaveGradient(rawName, COLOR_RED, COLOR_DEEP_RED, COLOR_BRIGHT_RED_PINK));
        }
    }
}
