package com.teamabnormals.endergetic.common.block;

import com.teamabnormals.endergetic.core.other.tags.EEBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class EnderFireBlock extends BaseFireBlock {

	public EnderFireBlock(Properties builder) {
		super(builder, 3.0F);
	}

	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
		return this.canSurvive(stateIn, worldIn, currentPos) ? this.defaultBlockState() : Blocks.AIR.defaultBlockState();
	}

	public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
		return isEnderFireBase(worldIn.getBlockState(pos.below()));
	}

	public static boolean isEnderFireBase(BlockState state) {
		return state.is(EEBlockTags.ENDER_FIRE_BASE_BLOCKS);
	}

	protected boolean canBurn(BlockState stateIn) {
		return true;
	}

}