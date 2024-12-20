package net.awakaxis.replenish.mixin;

import net.awakaxis.replenish.ReplenishEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Inject(method="canAttackBlock", at=@At("HEAD"), cancellable = true)
    private void preventAttackBabyCrops(BlockState blockState, Level level, BlockPos blockPos, Player player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack itemStack = player.getMainHandItem();
        boolean bl = blockState.getBlock() instanceof CropBlock cropBlock && cropBlock.getAge(blockState) != cropBlock.getMaxAge();
        if (itemStack.getItem() instanceof HoeItem hoeItem && EnchantmentHelper.getItemEnchantmentLevel(ReplenishEnchantments.REPLENISH, itemStack) >= 1
                && hoeItem.getTier().getUses() >= Tiers.NETHERITE.getUses() && bl) {
            cir.setReturnValue(false);
        }
    }
}
