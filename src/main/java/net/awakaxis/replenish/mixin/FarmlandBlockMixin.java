package net.awakaxis.replenish.mixin;

import net.awakaxis.replenish.Replenish;
import net.awakaxis.replenish.ReplenishEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmBlock.class)
public abstract class FarmlandBlockMixin {
    @Inject(method = "fallOn", at = @At("HEAD"), cancellable = true)
    private void onLandedUpon(Level world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci) {
        if (!world.isClientSide && entity instanceof LivingEntity mob && EnchantmentHelper.getEnchantmentLevel(ReplenishEnchantments.GREEN_FOOT, mob) >= 1) {
            ci.cancel();
        }
    }
}
