package net.acetheeldritchking.roaring_knight_iss.registries;

import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.acetheeldritchking.aces_spell_utils.items.example.items.armor.ExampleWarlockArmorItem;
import net.acetheeldritchking.aces_spell_utils.utils.ASRarities;
import net.acetheeldritchking.roaring_knight_iss.TheRoaringSpellbooks;
import net.acetheeldritchking.roaring_knight_iss.items.armor.RoaringHarbingerArmorItem;
import net.acetheeldritchking.roaring_knight_iss.items.weapons.DarkSabreItem;
import net.acetheeldritchking.roaring_knight_iss.utils.RKRarities;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;

public class RKItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TheRoaringSpellbooks.MOD_ID);

    /***
     * Generic Items
     */
    // Shard of Malice
    public static final DeferredHolder<Item, Item> SHADOW_CRYSTAL = ITEMS.register("shadow_crystal",
            () -> new Item(ItemPropertiesHelper.material().stacksTo(8).rarity(RKRarities.SHADED_RARITY_PROXY.getValue())));

    public static final DeferredHolder<Item, Item> BLACK_SHARD = ITEMS.register("black_shard",
            () -> new Item(ItemPropertiesHelper.material().fireResistant().rarity(RKRarities.SHADED_RARITY_PROXY.getValue())));

    /***
     * Weapons
     */
    // Dark Sabre
    public static final DeferredHolder<Item, Item> DARK_SABRE = ITEMS.register("dark_sabre", DarkSabreItem::new);

    /***
     * Armor
     */
    public static final DeferredHolder<Item, Item> ROARING_HARBINGER_HELMET = ITEMS.register("roaring_harbinger_helmet", () -> new RoaringHarbingerArmorItem(ArmorItem.Type.HELMET, ItemPropertiesHelper.equipment(1).fireResistant().durability(ArmorItem.Type.HELMET.getDurability(40)).rarity(RKRarities.SHADED_RARITY_PROXY.getValue())));
    public static final DeferredHolder<Item, Item> ROARING_HARBINGER_CHESTPLATE = ITEMS.register("roaring_harbinger_chestplate", () -> new RoaringHarbingerArmorItem(ArmorItem.Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).fireResistant().durability(ArmorItem.Type.CHESTPLATE.getDurability(40)).rarity(RKRarities.SHADED_RARITY_PROXY.getValue())));
    public static final DeferredHolder<Item, Item> ROARING_HARBINGER_LEGGINGS = ITEMS.register("roaring_harbinger_leggings", () -> new RoaringHarbingerArmorItem(ArmorItem.Type.LEGGINGS, ItemPropertiesHelper.equipment(1).fireResistant().durability(ArmorItem.Type.LEGGINGS.getDurability(40)).rarity(RKRarities.SHADED_RARITY_PROXY.getValue())));
    public static final DeferredHolder<Item, Item> ROARING_HARBINGER_BOOTS = ITEMS.register("roaring_harbinger_boots", () -> new RoaringHarbingerArmorItem(ArmorItem.Type.BOOTS, ItemPropertiesHelper.equipment(1).fireResistant().durability(ArmorItem.Type.BOOTS.getDurability(40)).rarity(RKRarities.SHADED_RARITY_PROXY.getValue())));

    /***
     * Spawn Eggs
     */
    // Roaring Harbinger
    public static final DeferredItem<Item> BLACK_EXECUTIONER_SPAWN_EGG = ITEMS.register("black_executioner_spawn_egg",
            () -> new DeferredSpawnEggItem(RKEntityRegistry.BLACK_EXECUTIONER_BOSS, 1973277, 5147788,
                    new Item.Properties()));

    public static Collection<DeferredHolder<Item, ? extends Item>> getRKItems()
    {
        return ITEMS.getEntries();
    }

    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}
