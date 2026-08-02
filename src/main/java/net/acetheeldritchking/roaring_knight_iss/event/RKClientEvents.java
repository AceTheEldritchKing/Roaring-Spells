package net.acetheeldritchking.roaring_knight_iss.event;

import net.acetheeldritchking.roaring_knight_iss.TheRoaringSpellbooks;
import net.acetheeldritchking.roaring_knight_iss.entity.render.entity.bosses.black_executioner.BlackExecutionerRenderer;
import net.acetheeldritchking.roaring_knight_iss.entity.render.spells.dark_fountain_spire.DarkFountainSpireRenderer;
import net.acetheeldritchking.roaring_knight_iss.entity.render.spells.dark_sabre_projectile.DarkSabreProjectileRenderer;
import net.acetheeldritchking.roaring_knight_iss.entity.render.spells.dark_star_projectiles.DarkStarProjectileRenderer;
import net.acetheeldritchking.roaring_knight_iss.entity.render.spells.dark_star_projectiles.DarkStarShrapnelProjectileRenderer;
import net.acetheeldritchking.roaring_knight_iss.particles.*;
import net.acetheeldritchking.roaring_knight_iss.registries.RKEntityRegistry;
import net.acetheeldritchking.roaring_knight_iss.registries.RKParticleRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = TheRoaringSpellbooks.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RKClientEvents {
    // Entity Rendering
    @SubscribeEvent
    public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(RKEntityRegistry.BLACK_EXECUTIONER_BOSS.get(), BlackExecutionerRenderer::new);
        event.registerEntityRenderer(RKEntityRegistry.DARK_SABRE_PROJECTILE.get(), DarkSabreProjectileRenderer::new);
        event.registerEntityRenderer(RKEntityRegistry.DARK_STAR_PROJECTILE.get(), DarkStarProjectileRenderer::new);
        event.registerEntityRenderer(RKEntityRegistry.DARK_STAR_SHRAPNEL.get(), DarkStarShrapnelProjectileRenderer::new);
        event.registerEntityRenderer(RKEntityRegistry.DARK_FOUNTAIN_SPIRE.get(), DarkFountainSpireRenderer::new);
    }

    // Item Rarity rendering
    /*@SubscribeEvent
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
    }*/

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event)
    {
        event.registerSpriteSet(RKParticleRegistry.RED_CLEAVE_PARTICLE.get(), RedCleaveParticle.Provider::new);
        event.registerSpriteSet(RKParticleRegistry.SWOON_PARTICLE.get(), SwoonParticle.Provider::new);
        event.registerSpriteSet(RKParticleRegistry.LARGE_DARK_BUBBLE_PARTICLE.get(), LargeDarkBubbleParticle.Provider::new);
        event.registerSpriteSet(RKParticleRegistry.DARK_BUBBLE_PARTICLE.get(), DarkBubbleParticle.Provider::new);
        event.registerSpriteSet(RKParticleRegistry.DARK_STAR_PARTICLE.get(), DarkStarParticle.Provider::new);
        event.registerSpriteSet(RKParticleRegistry.RED_SPARKLE_PARTICLE.get(), RedSparkleParticle.Provider::new);
        event.registerSpriteSet(RKParticleRegistry.STAR_SPARKLE_PARTICLE.get(), StarSparkleParticle.Provider::new);
    }
}
