package net.awakaxis.replenish.mixin;

import java.util.UUID;

import net.awakaxis.replenish.ReplenishEnchantments;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    // currently unused
    private static final UUID GREEN_FOOT_MODIFIER_ID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    
    @Inject(method = "tick", at = @At("RETURN"))
    private void tick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity)(Object)this;
        if (!entity.level().isClientSide) {
            ItemStack stack = entity.getItemBySlot(EquipmentSlot.FEET);
            if (EnchantmentHelper.getItemEnchantmentLevel(ReplenishEnchantments.GREEN_FOOT, stack) >= 1 && stack.getItem() instanceof ArmorItem armor && armor.getMaterial().getDurabilityForType(ArmorItem.Type.BOOTS) >= ArmorMaterials.DIAMOND.getDurabilityForType(ArmorItem.Type.BOOTS)) {
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1, 2, true, false, false));
            }
        }
    }

}
