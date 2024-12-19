package net.awakaxis.replenish;

import net.awakaxis.replenish.enchantment.GreenFootEnchantment;
import net.awakaxis.replenish.enchantment.ReplenishEnchantment;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public class ReplenishEnchantments {

    public static final Enchantment REPLENISH = Registry.register(BuiltInRegistries.ENCHANTMENT, Replenish.id("replenish"), new ReplenishEnchantment());
    public static final Enchantment GREEN_FOOT = Registry.register(BuiltInRegistries.ENCHANTMENT, Replenish.id("green_foot"), new GreenFootEnchantment());

    public static void register() {
        Replenish.LOGGER.info("Registered Enchantments");
    }
}
