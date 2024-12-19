package net.awakaxis.replenish.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.awakaxis.replenish.common.Replenish;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(FarmlandBlock.class)
public abstract class FarmlandBlockMixin {
    @Inject(method = "onLandedUpon", at = @At("HEAD"), cancellable = true)
    private void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci) {
        if (!world.isClient && entity instanceof LivingEntity mob && EnchantmentHelper.getEquipmentLevel(Replenish.GREEN_FOOT, mob) >= 1) {
            ci.cancel();
        }
    }
}
