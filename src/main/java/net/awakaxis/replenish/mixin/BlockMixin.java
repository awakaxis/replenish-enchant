package net.awakaxis.replenish.mixin;

import net.awakaxis.replenish.ReplenishEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Block.class)
public abstract class BlockMixin {

    @Inject(method = "playerDestroy", at = @At("HEAD"), cancellable = true)
    private void afterBreak(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack, CallbackInfo ci) {
        Block block = (Block) (Object) this;
        // if using a replenish hoe on a crop
        if (EnchantmentHelper.getItemEnchantmentLevel(ReplenishEnchantments.REPLENISH, stack) >= 1 && block instanceof CropBlock) {
            List<ItemStack> drops = null;
            boolean foundSeeds = false;
            CropBlock block2 = (CropBlock)(Object)this;
            player.awardStat(Stats.BLOCK_MINED.get(block));
            player.causeFoodExhaustion(0.005F);
            if (level instanceof ServerLevel serverLevel) {
                // get the drop with more luck
                LootParams.Builder builder = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                    .withParameter(LootContextParams.TOOL, stack)
                    .withOptionalParameter(LootContextParams.THIS_ENTITY, player)
                    .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity);
                drops = state.getDrops(builder);
                // remove one seed from the drops
                for (ItemStack drop : drops) {
                    if (drop.getItem() == ((CropBlockAccessor)block).invokeGetBaseSeedId().asItem()) {
                        drop.shrink(1);
                        foundSeeds = true;
                        break;
                    }
                }
                // if the hoe is not diamond or better, remove half of the drops
                if (stack.getItem() instanceof HoeItem hoe && hoe.getTier().getUses() < Tiers.DIAMOND.getUses()) {
                    for (ItemStack drop : drops) {
                        drop.shrink(drop.getCount() / 2);
                    }
                }
                // if there were no seeds in the loot, try to remove one from the player's inventory
                if (!foundSeeds) {
                    ItemStack itemStack = ItemStack.EMPTY;
                    for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
                        itemStack = player.getInventory().getItem(i);
                        if (itemStack.getItem() == ((CropBlockAccessor)block).invokeGetBaseSeedId().asItem()) {
                            break;
                        }
                        itemStack = ItemStack.EMPTY;
                    }
                    if (!itemStack.isEmpty()) foundSeeds = true;
                    itemStack.shrink(1);
                }
                if (foundSeeds) {
                    level.setBlockAndUpdate(pos, block2.getStateForAge(0));
                }
                drops.forEach(_stack -> Block.popResource(level, pos, _stack));
                state.spawnAfterBreak(serverLevel, pos, stack, true);
            }
            ci.cancel();
        }
    }
}
