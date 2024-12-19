package net.awakaxis.replenish.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.awakaxis.replenish.common.Replenish;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemStack;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    // currently unused
    private static final UUID GREEN_FOOT_MODIFIER_ID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    
    @Inject(method = "tick", at = @At("RETURN"))
    private void tick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity)(Object)this;
        if (!entity.getWorld().isClient()) {
            ItemStack stack = entity.getEquippedStack(EquipmentSlot.FEET);
            if (EnchantmentHelper.getLevel(Replenish.GREEN_FOOT, stack) >= 1 && stack.getItem() instanceof ArmorItem armor && armor.getMaterial().getDurability(EquipmentSlot.FEET) >= ArmorMaterials.DIAMOND.getDurability(EquipmentSlot.FEET)) {
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 1, 2, true, false, false));
            }
        }
    }

}
