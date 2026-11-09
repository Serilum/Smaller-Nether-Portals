package com.serilum.smallernetherportals.util;

import com.natamus.collective.config.DuskConfigModToggles;
import com.natamus.collective.services.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Util {
	public static void processSmallerPortal(Level level, BlockPos firePos) {
		BlockPos bottomPos = getBottomPortalPos(level, firePos);
		if (!isObsidian(level.getBlockState(bottomPos.below()))) {
			return;
		}

		for (Axis axis : List.of(Axis.X, Axis.Z)) {
			List<BlockPos> portalPositions = getSmallerPortalPositions(level, bottomPos, axis);
			if (isEnclosedByObsidian(level, portalPositions, axis)) {
				createPortal(level, portalPositions, axis);
				return;
			}
		}
	}

	public static BlockPos getBottomPortalPos(Level level, BlockPos firePos) {
		BlockPos bottomPos = firePos.immutable();
		BlockPos lowestPossiblePos = firePos.below(2);

		while (bottomPos.getY() > lowestPossiblePos.getY() && isAir(level.getBlockState(bottomPos.below()))) {
			bottomPos = bottomPos.below();
		}

		return bottomPos;
	}

	public static List<BlockPos> getSmallerPortalPositions(Level level, BlockPos bottomPos, Axis axis) {
		BlockPos bottomLeftPos = bottomPos;
		if (isAir(level.getBlockState(bottomPos.relative(axis, -1)))) {
			bottomLeftPos = bottomPos.relative(axis, -1);
		}

		int width = 1;
		if (isAir(level.getBlockState(bottomLeftPos.relative(axis, 1)))) {
			width = 2;
		}

		int height = 2;
		if (isAir(level.getBlockState(bottomLeftPos.above(2)))) {
			height = 3;
		}

		List<BlockPos> portalPositions = new ArrayList<>();
		for (int column = 0; column < width; column++) {
			for (int row = 0; row < height; row++) {
				portalPositions.add(bottomLeftPos.relative(axis, column).above(row));
			}
		}

		return portalPositions;
	}

	public static boolean isEnclosedByObsidian(Level level, List<BlockPos> portalPositions, Axis axis) {
		for (BlockPos portalPos : portalPositions) {
			if (!isAir(level.getBlockState(portalPos))) {
				return false;
			}

			for (BlockPos aroundPos : getBlocksAround(portalPos, axis)) {
				if (!portalPositions.contains(aroundPos) && !isObsidian(level.getBlockState(aroundPos))) {
					return false;
				}
			}
		}

		return true;
	}

	public static void createPortal(Level level, List<BlockPos> portalPositions, Axis axis) {
		BlockState portalState = Blocks.NETHER_PORTAL.defaultBlockState().setValue(NetherPortalBlock.AXIS, axis);
		for (BlockPos portalPos : portalPositions) {
			level.setBlock(portalPos, portalState, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
		}

		BlockPos bottomLeftPos = portalPositions.get(0);
		PortalShape portalShape = new PortalShape(level, bottomLeftPos, axis);

		Services.EVENTTRIGGER.triggerNetherPortalSpawnEvent(level, bottomLeftPos, portalShape);
	}

	public static List<BlockPos> getBlocksAround(BlockPos pos, Axis axis) {
		List<BlockPos> around = new ArrayList<>();
		around.add(pos.above());
		around.add(pos.below());
		around.add(pos.relative(axis, 1));
		around.add(pos.relative(axis, -1));

		return around;
	}
	
	public static List<BlockPos> getFrontBlocks(Level world, BlockPos portalblock) {
		List<BlockPos> returnblocks = new ArrayList<BlockPos>();
		
		boolean smallest = false;
		if (isObsidian(world.getBlockState(portalblock.east())) && isObsidian(world.getBlockState(portalblock.west()))) {
			smallest = true;
		}
		else if (isObsidian(world.getBlockState(portalblock.north())) && isObsidian(world.getBlockState(portalblock.south()))) {
			smallest = true;
		}
		else {
			if (isPortal(world.getBlockState(portalblock.north()))) {
				portalblock = portalblock.north().immutable();
			}
			else if (isPortal(world.getBlockState(portalblock.west()))) {
				portalblock = portalblock.west().immutable();
			}
		}
		
		if (!isPortalOrObsidian(world.getBlockState(portalblock.west()))) {
			returnblocks.add(portalblock.west().below().immutable());
			if (!smallest) {
				returnblocks.add(portalblock.west().south().below().immutable());
			}
		}
		else {
			returnblocks.add(portalblock.south().below().immutable());
			if (!smallest) {
				returnblocks.add(portalblock.south().east().below().immutable());
			}
		}
		
		return returnblocks;
	}
	
	public static BlockPos findPortalAround(Level world, BlockPos pos) {
		BlockPos portalpos = null;
		
		for (int i = 0; i < 10; i++) {
			BlockPos cpos = pos.above(i).immutable();
			Iterator<BlockPos> around = BlockPos.betweenClosedStream(cpos.getX()-1, cpos.getY(), cpos.getZ()-1, cpos.getX()+1, cpos.getY(), cpos.getZ()+1).iterator();
			while (around.hasNext()) {
				BlockPos ap = around.next();
				if (isPortal(world.getBlockState(ap))) {
					portalpos = ap.immutable();
					break;
				}
			}
			if (portalpos != null) {
				break;
			}
		}
		return portalpos;
	}
	
	public static void setObsidian(Level world, List<BlockPos> toblocks) {
		for (BlockPos tbs : toblocks) {
			if (shouldMakeFront(world.getBlockState(tbs))) {
				world.setBlockAndUpdate(tbs, Blocks.OBSIDIAN.defaultBlockState());
			}
			for (int i = 1; i < 3; i++) {
				BlockPos up = tbs.above(i);
				if (!isAir(world.getBlockState(up))) {
					world.setBlockAndUpdate(up, Blocks.AIR.defaultBlockState());
				}
			}
		}
	}
	
	public static Boolean shouldMakeFront(BlockState bs) {
		Block block = bs.getBlock();
		return isAir(bs) || block.equals(Blocks.NETHERRACK) || block.equals(Blocks.SOUL_SAND);
	}
	
	public static Boolean isObsidian(BlockState bs) {
		return isObsidian(bs.getBlock());
	}
	public static Boolean isObsidian(Block block) {
		return block.equals(Blocks.OBSIDIAN) || (block.equals(Blocks.CRYING_OBSIDIAN) && isCryingPortalsEnabled());
	}

	public static boolean isCryingPortalsEnabled() {
		return Services.MODLOADER.isModLoaded("cryingportals") && !DuskConfigModToggles.isDisabledSerilumMod("cryingportals");
	}
	
	public static Boolean isAir(BlockState bs) {
		return bs.isAir() || bs.getBlock() instanceof BaseFireBlock;
	}
	
	public static Boolean isPortalOrObsidian(BlockState bs) {
		Block block = bs.getBlock();
		return block.equals(Blocks.NETHER_PORTAL) || isObsidian(block);
	}
	
	public static Boolean isPortal(BlockState bs) {
		Block block = bs.getBlock();
		return block.equals(Blocks.NETHER_PORTAL);
	}
}
