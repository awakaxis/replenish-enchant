package net.awakaxis.replenish.common.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;

public class ReplenishEnchantment extends Enchantment {

    public ReplenishEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentTarget.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof HoeItem;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
    
}
