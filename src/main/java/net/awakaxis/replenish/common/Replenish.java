package net.awakaxis.replenish.common;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.awakaxis.replenish.common.enchantment.GreenFootEnchantment;
import net.awakaxis.replenish.common.enchantment.ReplenishEnchantment;
import net.awakaxis.replenish.common.enchantment.TelekenesisEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Replenish implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("Replenish");

    public static final Enchantment REPLENISH = registerEnchantment("replenish", new ReplenishEnchantment());
    public static final Enchantment GREEN_FOOT = registerEnchantment("green_foot", new GreenFootEnchantment());
    public static final Enchantment TELEKENESIS = registerEnchantment("telekenesis", new TelekenesisEnchantment());
    
    
    public static Identifier id(String name) {
        return new Identifier("replenish", name);
    }

    @Override
    public void onInitialize(ModContainer mod) {
        LOGGER.info("Replenish finished initializing!");
    }

    public static <T extends Enchantment> T registerEnchantment(String name, T enchantment) {
        Registry.register(Registry.ENCHANTMENT, id(name), enchantment);
        return enchantment;
    }
    
}