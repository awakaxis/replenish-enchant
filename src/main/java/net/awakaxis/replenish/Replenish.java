package net.awakaxis.replenish;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Replenish implements ModInitializer {
	public static final String MOD_ID = "replenish";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		ReplenishEnchantments.register();
		LOGGER.info("Hello fans of Whitepine!");
	}
}