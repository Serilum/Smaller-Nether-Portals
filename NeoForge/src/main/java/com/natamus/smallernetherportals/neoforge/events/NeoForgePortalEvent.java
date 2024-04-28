package com.natamus.smallernetherportals.neoforge.events;

import com.natamus.smallernetherportals.events.PortalEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public class NeoForgePortalEvent {
	@SubscribeEvent
	public static void onClick(PlayerInteractEvent.RightClickBlock e) {
		PortalEvent.onClick(e.getLevel(), e.getEntity(), e.getHand(), e.getPos(), e.getHitVec());
	}
	
	@SubscribeEvent
	public static void onDimensionChange(PlayerChangedDimensionEvent e) {
		Player player = e.getEntity();
		Level level = player.level();
		if (level.isClientSide) {
			return;
		}
		
		PortalEvent.onDimensionChange((ServerLevel)level, (ServerPlayer)player);
	}
	
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Pre e) {
		Player player = e.getEntity();
		Level level = player.level();
		if (level.isClientSide) {
			return;
		}
		
		PortalEvent.onPlayerTick((ServerLevel)level, (ServerPlayer)player);
	}
}
