package com.serilum.smallernetherportals.events;

import com.serilum.smallernetherportals.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PortalEvent {
	private static final Map<String, BlockPos> toposes = new HashMap<String, BlockPos>();
	
	public static void onFirePlaced(Level level, BlockPos firePos, BlockState fireState, BlockState oldState) {
		if (oldState.is(fireState.getBlock())) {
			return;
		}

		if (!level.getBlockState(firePos).is(fireState.getBlock())) {
			return;
		}

		Util.processSmallerPortal(level, firePos);
	}
	
	public static void onDimensionChange(ServerLevel level, ServerPlayer player) {
		BlockPos pos = player.blockPosition();
		Block block = level.getBlockState(pos).getBlock();
		
		if (block instanceof NetherPortalBlock) {
			return;
		}
		
		String playername = player.getName().getString();
		
		if (!toposes.containsKey(playername)) {
			BlockPos foundpos = Util.findPortalAround(level, pos);
			if (foundpos != null) {
				List<BlockPos> frontblocks = Util.getFrontBlocks(level, foundpos);
				Util.setObsidian(level, frontblocks);
				
				toposes.put(playername, frontblocks.get(frontblocks.size()-1).above().immutable());
			}
		}
	}
	
	public static void onPlayerTick(ServerLevel level, ServerPlayer player) {
		String playername = player.getName().getString();
		if (!toposes.containsKey(playername)) {
			return;
		}
		
		BlockPos topos = toposes.get(playername); 
		
		player.setPortalCooldown();
		player.teleportTo(((double)topos.getX())+0.5, topos.getY(), ((double)topos.getZ())+0.5);
		toposes.remove(playername);
	}
}
