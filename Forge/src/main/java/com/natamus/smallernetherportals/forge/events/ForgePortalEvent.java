package com.natamus.smallernetherportals.forge.events;

import com.natamus.smallernetherportals.events.PortalEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

import java.lang.invoke.MethodHandles;

public class ForgePortalEvent {
	public static void registerEventsInBus() {
		BusGroup.DEFAULT.register(MethodHandles.lookup(), ForgePortalEvent.class);
	}

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
		Player player = e.player;
		Level level = player.level();
		if (level.isClientSide) {
			return;
		}
		
		PortalEvent.onPlayerTick((ServerLevel)level, (ServerPlayer)player);
	}
}
