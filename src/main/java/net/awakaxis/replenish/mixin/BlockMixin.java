package net.awakaxis.replenish.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.awakaxis.replenish.common.Replenish;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(Block.class)
public abstract class BlockMixin {
    @Inject(method = "afterBreak", at = @At("RETURN"))
    private void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack, CallbackInfo ci) {
        Replenish.LOGGER.info("afterBreak mixin called");
        if (((Block)(Object)this) instanceof CropBlock) {
            CropBlock block = (CropBlock)(Object)this;
            if (EnchantmentHelper.getLevel(Replenish.REPLENISH, player.getStackInHand(Hand.MAIN_HAND)) >= 1) {
                Replenish.LOGGER.info("Replenish enchantment detected");
                ItemStack itemStack = ItemStack.EMPTY;
                for (int i = 0; i < player.getInventory().size(); ++i) {
                    itemStack = player.getInventory().getStack(i);
                    Replenish.LOGGER.info("stack name: " + itemStack.getItem().getName().getString() + " seed item name: " + ((CropBlockAccessor)block).invokeGetSeedsItem().asItem().getName().getString());
                    if (itemStack.getItem() == ((CropBlockAccessor)block).invokeGetSeedsItem().asItem()) {
                        break;
                    }
                    itemStack = ItemStack.EMPTY;
                }
                if (itemStack.isEmpty()) return;
                itemStack.decrement(1);
                world.setBlockState(pos, block.withAge(0));
            }
        }
    }
}
