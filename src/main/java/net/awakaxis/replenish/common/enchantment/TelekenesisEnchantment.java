package net.awakaxis.replenish.common.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

public class TelekenesisEnchantment extends Enchantment {

    public TelekenesisEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentTarget.ARMOR_HEAD, new EquipmentSlot[] {EquipmentSlot.HEAD});
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem armor && armor.getSlotType() == EquipmentSlot.HEAD;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
}
