package com.natamus.smallernetherportals;

import com.natamus.collective.check.RegisterMod;
import com.natamus.collective.fabric.callbacks.CollectiveBlockEvents;
import com.natamus.collective.fabric.callbacks.CollectivePlayerEvents;
import com.natamus.smallernetherportals.events.PortalEvent;
import com.natamus.smallernetherportals.util.Reference;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class ModFabric implements ModInitializer {
	
	@Override
	public void onInitialize() {
		setGlobalConstants();
		ModCommon.init();

		loadEvents();

		RegisterMod.register(Reference.NAME, Reference.MOD_ID, Reference.VERSION, Reference.ACCEPTED_VERSIONS);
	}

	private void loadEvents() {
		CollectivePlayerEvents.PLAYER_TICK.register((ServerLevel serverLevel, ServerPlayer serverPlayer) -> {
			PortalEvent.onPlayerTick(serverLevel, serverPlayer);
		});

		CollectivePlayerEvents.PLAYER_CHANGE_DIMENSION.register((ServerLevel serverLevel, ServerPlayer serverPlayer) -> {
			PortalEvent.onDimensionChange(serverLevel, serverPlayer);
		});

		CollectiveBlockEvents.BLOCK_RIGHT_CLICK.register((Level level, Player player, InteractionHand hand, BlockPos pos, BlockHitResult hitVec) -> {
			return PortalEvent.onClick(level, player, hand, pos, hitVec);
		});
	}

	private static void setGlobalConstants() {

	}
}
