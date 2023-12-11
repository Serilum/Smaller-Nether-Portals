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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ForgePortalEvent {
	@SubscribeEvent
	public void onClick(PlayerInteractEvent.RightClickBlock e) {
		PortalEvent.onClick(e.getLevel(), e.getEntity(), e.getHand(), e.getPos(), e.getHitVec());
	}
	
	@SubscribeEvent
	public void onDimensionChange(PlayerChangedDimensionEvent e) {
		Player player = e.getEntity();
		Level level = player.level;
		if (level.isClientSide) {
			return;
		}
		
		PortalEvent.onDimensionChange((ServerLevel)level, (ServerPlayer)player);
	}
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent e) {
		Player player = e.player;
		Level level = player.getCommandSenderWorld();
		if (level.isClientSide || !e.phase.equals(TickEvent.Phase.START)) {
			return;
		}
		
		PortalEvent.onPlayerTick((ServerLevel)level, (ServerPlayer)player);
	}
}
