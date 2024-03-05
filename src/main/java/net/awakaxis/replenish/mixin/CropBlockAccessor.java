package net.awakaxis.replenish.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.block.CropBlock;
import net.minecraft.item.ItemConvertible;

@Mixin(CropBlock.class)
public interface CropBlockAccessor {
    @Invoker("getSeedsItem")
    public ItemConvertible invokeGetSeedsItem();
}
