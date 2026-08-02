package net.acetheeldritchking.roaring_knight_iss.registries;

import io.redspace.ironsspellbooks.item.FurledMapItem;
import net.acetheeldritchking.roaring_knight_iss.TheRoaringSpellbooks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class RKCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TheRoaringSpellbooks.MOD_ID);

    public static final Supplier<CreativeModeTab> RK_ITEMS_TAB = CREATIVE_MODE_TAB.register("rk_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(RKItemRegistry.DARK_SABRE.get()))
                    .title(Component.translatable("creative_tab.roaring_knight_iss.items"))
                    .displayItems((itemDisplayParameters, output) -> {
                        // Materials
                        output.accept(RKItemRegistry.SHADOW_CRYSTAL.get());
                        output.accept(RKItemRegistry.BLACK_SHARD.get());
                        // Map Items
                        // Curios
                        // Sheaths
                        // Treasure
                        // Lootbags
                        // Spellbooks
                        // Weapons
                        output.accept(RKItemRegistry.DARK_SABRE.get());
                        // Staves
                        // Armor
                        output.accept(RKItemRegistry.ROARING_HARBINGER_HELMET.get());
                        output.accept(RKItemRegistry.ROARING_HARBINGER_CHESTPLATE.get());
                        output.accept(RKItemRegistry.ROARING_HARBINGER_LEGGINGS.get());
                        output.accept(RKItemRegistry.ROARING_HARBINGER_BOOTS.get());
                        // Spawn Eggs
                        output.accept(RKItemRegistry.BLACK_EXECUTIONER_SPAWN_EGG.get());

                        // Compat
                    }).build());

    public static void register(IEventBus eventBus)
    {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
