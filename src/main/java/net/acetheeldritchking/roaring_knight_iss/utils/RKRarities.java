package net.acetheeldritchking.roaring_knight_iss.utils;

import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.function.UnaryOperator;

public class RKRarities {
    // Shaded Rarity
    public static final EnumProxy<Rarity> SHADED_RARITY_PROXY = new EnumProxy<>(Rarity.class,
            -1,
            "roaring_knight_iss:shaded",
            (UnaryOperator<Style>) ((style) -> style.withColor(
                    UniqueRarityColorHelper.getPulsingColor(
                            5000,
                            0xd8d6e9,
                            0x130b21
                    )))
    );
}
