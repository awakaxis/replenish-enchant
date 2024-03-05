package net.awakaxis.replenish.mixin;

import java.util.List;

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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(Block.class)
public abstract class BlockMixin {
    @Inject(method = "afterBreak", at = @At("HEAD"), cancellable = true)
    private void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack, CallbackInfo ci) {
        Block block = (Block)(Object)this;
        List<ItemStack> drops = null;
        boolean foundSeeds = false;
        player.incrementStat(Stats.MINED.getOrCreateStat(block));
        player.addExhaustion(0.005F);
        if (world instanceof ServerWorld) {
            drops = Block.getDroppedStacks(state, (ServerWorld) world, pos, blockEntity, player, stack);
        }
        if (block instanceof CropBlock) {
            CropBlock block2 = (CropBlock)(Object)this;
            if (EnchantmentHelper.getLevel(Replenish.REPLENISH, player.getStackInHand(Hand.MAIN_HAND)) >= 1) {
                if (drops != null) {
                    for (ItemStack drop : drops) {
                        if (drop.getItem() == ((CropBlockAccessor)block).invokeGetSeedsItem().asItem()) {
                            drop.decrement(1);
                            foundSeeds = true;
                            break;
                        }
                    }
                }
                if (!foundSeeds) {
                    ItemStack itemStack = ItemStack.EMPTY;
                    for (int i = 0; i < player.getInventory().size(); ++i) {
                        itemStack = player.getInventory().getStack(i);
                        if (itemStack.getItem() == ((CropBlockAccessor)block).invokeGetSeedsItem().asItem()) {
                            break;
                        }
                        itemStack = ItemStack.EMPTY;
                    }
                    if (itemStack.isEmpty()) return;
                    itemStack.decrement(1);
                }
                world.setBlockState(pos, block2.withAge(0));
            }
        }
        if (world instanceof ServerWorld) {
            drops.forEach(_stack -> Block.dropStack(world, pos, _stack));
            state.onStacksDropped((ServerWorld) world, pos, stack, true);
        }
        ci.cancel();
    }
}
