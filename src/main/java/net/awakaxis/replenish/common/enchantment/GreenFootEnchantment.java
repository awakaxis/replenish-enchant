package net.awakaxis.replenish.common.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

public class GreenFootEnchantment extends Enchantment {

    public GreenFootEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.ARMOR, new EquipmentSlot[] {EquipmentSlot.FEET});
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem armor && armor.getSlotType() == EquipmentSlot.FEET;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
    
}
