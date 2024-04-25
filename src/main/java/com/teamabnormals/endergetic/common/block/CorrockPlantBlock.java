package com.teamabnormals.endergetic.common.block;

import com.google.common.collect.Maps;
import com.teamabnormals.endergetic.core.registry.EEBlocks;
import com.teamabnormals.endergetic.core.other.EEEvents;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Supplier;

public class CorrockPlantBlock extends Block implements SimpleWaterloggedBlock {
	private static final Map<ResourceLocation, Supplier<Block>> CONVERSIONS = Util.make(Maps.newHashMap(), (conversions) -> {
		conversions.put(BuiltinDimensionTypes.OVERWORLD.location(), EEBlocks.OVERWORLD_CORROCK);
		conversions.put(BuiltinDimensionTypes.NETHER.location(), EEBlocks.NETHER_CORROCK);
		conversions.put(BuiltinDimensionTypes.END.location(), EEBlocks.END_CORROCK);
	});
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 15.0D, 14.0D);
	public final boolean petrified;

	public CorrockPlantBlock(Properties properties, boolean petrified) {
		super(properties);
		this.petrified = petrified;
		this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, Entity entity) {
		return SoundType.CORAL_BLOCK;
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		Block conversionBlock = this.getConversionBlock(level);
		if (!this.petrified && conversionBlock != this) {
			level.setBlockAndUpdate(pos, conversionBlock.defaultBlockState());
		}
	}

	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (facing == Direction.DOWN && !stateIn.canSurvive(worldIn, currentPos)) {
			return Blocks.AIR.defaultBlockState();
		} else {
			if (stateIn.getValue(WATERLOGGED)) {
				worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
				if (!this.petrified) {
					return EEEvents.convertCorrockBlock(stateIn);
				}
			}

			if (this.shouldConvert(worldIn)) {
				worldIn.scheduleTick(currentPos, this, 60 + worldIn.getRandom().nextInt(40));
			}
			return stateIn;
		}
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
		BlockPos blockpos = pos.below();
		return worldIn.getBlockState(blockpos).isFaceSturdy(worldIn, blockpos, Direction.UP);
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		if (this.shouldConvert(context.getLevel())) {
			context.getLevel().scheduleTick(context.getClickedPos(), this, 60 + context.getLevel().getRandom().nextInt(40));
		}
		FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
		return this.defaultBlockState().setValue(WATERLOGGED, fluidState.is(FluidTags.WATER) && fluidState.getAmount() >= 8);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
	}

	protected Block getConversionBlock(LevelAccessor level) {
		return CONVERSIONS.getOrDefault(level.registryAccess().registry(Registries.DIMENSION_TYPE).get().getKey(level.dimensionType()), EEBlocks.OVERWORLD_CORROCK).get();
	}

	private boolean shouldConvert(LevelAccessor level) {
		return !this.petrified && this.getConversionBlock(level) != this;
	}
}