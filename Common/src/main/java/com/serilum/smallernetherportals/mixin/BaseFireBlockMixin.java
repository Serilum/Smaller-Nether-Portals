package com.serilum.smallernetherportals.mixin;

import com.serilum.smallernetherportals.events.PortalEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BaseFireBlock.class, priority = 1001)
public class BaseFireBlockMixin {
	@Inject(method = "onPlace(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)V", at = @At(value = "TAIL"))
	private void BaseFireBlock_onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState oldBlockState, boolean movedByPiston, CallbackInfo ci) {
		PortalEvent.onFirePlaced(level, blockPos, blockState, oldBlockState);
	}
}
