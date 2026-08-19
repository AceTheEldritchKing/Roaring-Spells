package net.acetheeldritchking.roaring_knight_iss.registries;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.acetheeldritchking.roaring_knight_iss.TheRoaringSpellbooks;
import net.acetheeldritchking.roaring_knight_iss.spells.eldritch.KnightsEdgeSpell;
import net.acetheeldritchking.roaring_knight_iss.spells.eldritch.StarStormSpell;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static io.redspace.ironsspellbooks.api.registry.SpellRegistry.SPELL_REGISTRY_KEY;

public class RKSpellRegistries {
    public static final DeferredRegister<AbstractSpell> SPELLS = DeferredRegister.create(SPELL_REGISTRY_KEY, TheRoaringSpellbooks.MOD_ID);

    public static Supplier<AbstractSpell> registerSpell(AbstractSpell spell) {
        return SPELLS.register(spell.getSpellName(), () -> spell);
    }

    // Shadow Splitter - Send out a blade of shadow that homes in on the nearest target, cutting through them repeatedly

    // Knight's Edge - Conjure a group of shadow blades with down upon a selected area. Blades cause hit entities to take more Eldritch damage
    public static final Supplier<AbstractSpell> KNIGHTS_EDGE = registerSpell(new KnightsEdgeSpell());

    // Star Storm ?

    // Star Shrapnel

    // Cutscene Merchant

    // Swoon


    public static void register(IEventBus eventBus)
    {
        SPELLS.register(eventBus);
    }
}
