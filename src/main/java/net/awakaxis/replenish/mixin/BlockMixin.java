package net.awakaxis.replenish.mixin;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.awakaxis.replenish.common.Replenish;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents.Replace;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(Block.class)
public abstract class BlockMixin {

    // @Inject(method = "onBreak", at = @At("RETURN"))
    // private void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) {
    //     if (EnchantmentHelper.getLevel(Replenish.TELEKENESIS, player.getEquippedStack(EquipmentSlot.HEAD)) >= 1) {
    //         MinecraftClient client = MinecraftClient.getInstance();
    //         client.getSoundManager().play(new PositionedSoundInstance(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1.0F, 1.0F, client.player.getRandom(), new BlockPos(pos)));
    //     }
    // }

    @Inject(method = "afterBreak", at = @At("HEAD"), cancellable = true)
    private void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack, CallbackInfo ci) {
        Block block = (Block)(Object)this;
        // if using a replenish hoe on a crop
        if (EnchantmentHelper.getLevel(Replenish.REPLENISH, stack) >= 1 && block instanceof CropBlock) {
            List<ItemStack> drops = null;
            boolean foundSeeds = false;
            CropBlock block2 = (CropBlock)(Object)this;
            player.incrementStat(Stats.MINED.getOrCreateStat(block));
            player.addExhaustion(0.005F);
            if (world instanceof ServerWorld) {
                // get the drop with more luck
                LootContext.Builder builder = new LootContext.Builder((ServerWorld) world)
                    .random(world.random)
                    .parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
                    .parameter(LootContextParameters.TOOL, stack)
                    .optionalParameter(LootContextParameters.THIS_ENTITY, player)
                    .optionalParameter(LootContextParameters.BLOCK_ENTITY, blockEntity);
                drops = state.getDroppedStacks(builder);
                // remove one seed from the drops
                for (ItemStack drop : drops) {
                    if (drop.getItem() == ((CropBlockAccessor)block).invokeGetSeedsItem().asItem()) {
                        drop.decrement(1);
                        foundSeeds = true;
                        break;
                    }
                }
                // if the hoe is not diamond or better, remove half of the drops
                if (stack.getItem() instanceof HoeItem hoe && hoe.getMaterial().getDurability() < ToolMaterials.DIAMOND.getDurability()) {
                    for (ItemStack drop : drops) {
                        drop.decrement(drop.getCount() / 2);
                    }
                }
                // if there were no seeds in the loot, try to remove one from the player's inventory
                if (!foundSeeds) {
                    ItemStack itemStack = ItemStack.EMPTY;
                    for (int i = 0; i < player.getInventory().size(); ++i) {
                        itemStack = player.getInventory().getStack(i);
                        if (itemStack.getItem() == ((CropBlockAccessor)block).invokeGetSeedsItem().asItem()) {
                            break;
                        }
                        itemStack = ItemStack.EMPTY;
                    }
                    if (!itemStack.isEmpty()) foundSeeds = true;
                    itemStack.decrement(1);
                }
                if (foundSeeds) {
                    world.setBlockState(pos, block2.withAge(0));
                }
                drops.forEach(_stack -> Block.dropStack(world, pos, _stack));
                state.onStacksDropped((ServerWorld) world, pos, stack, true);
            }
            ci.cancel();
        }
        if (EnchantmentHelper.getLevel(Replenish.TELEKENESIS, player.getEquippedStack(EquipmentSlot.HEAD)) >= 1) {
            int xpAmount = -1;
            List<ItemStack> drops = null;
            player.incrementStat(Stats.MINED.getOrCreateStat(block));
            player.addExhaustion(0.005F);
            if (world instanceof ServerWorld) {
                LootContext.Builder builder = new LootContext.Builder((ServerWorld) world)
                    .random(world.random)
                    .parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
                    .parameter(LootContextParameters.TOOL, stack)
                    .optionalParameter(LootContextParameters.THIS_ENTITY, player)
                    .optionalParameter(LootContextParameters.BLOCK_ENTITY, blockEntity);
                drops = state.getDroppedStacks(builder);

                player.sendMessage(Text.of(String.format("total experience: %d", player.totalExperience)), false);
                player.sendMessage(Text.of(String.format("total experience + xpAmount: %d", player.totalExperience + xpAmount)), false);
                if (player.totalExperience + xpAmount >= 0) {
                    drops.forEach(_stack -> {
                        if (!player.giveItemStack(_stack)) {
                            Block.dropStack(world, pos, _stack);
                        }
                    });
                    player.addExperience(xpAmount);
                    ((ServerPlayerEntity) player).networkHandler.sendPacket(new PlaySoundIdS2CPacket(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP.getId(), SoundCategory.BLOCKS, new Vec3d(pos.getX(), pos.getY(), pos.getZ()), 1.0F, 1.0F, world.getRandom().nextLong()));
                    player.sendMessage(Text.of("Telekenesis"), true);
                } else {
                    drops.forEach(_stack -> Block.dropStack(world, pos, _stack));
                }
                // drops.forEach(_stack -> {
                //     if (player.totalExperience + xpAmount >= 0 && player.giveItemStack(_stack)) {
                //         player.addExperience(xpAmount);
                //         ((ServerPlayerEntity) player).networkHandler.sendPacket(new PlaySoundIdS2CPacket(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP.getId(), SoundCategory.BLOCKS, new Vec3d(pos.getX(), pos.getY(), pos.getZ()), 1.0F, 1.0F, world.getRandom().nextLong()));
                //         player.sendMessage(Text.of("Telekenesis"), true);
                //     } else {
                //         Block.dropStack(world, pos, _stack);
                //     }
                // });
                state.onStacksDropped((ServerWorld) world, pos, stack, true);
            }
            ci.cancel();
        }
    }
}
