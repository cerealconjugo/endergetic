package com.teamabnormals.endergetic.common.block.poise.boof;

import com.teamabnormals.endergetic.common.block.entity.boof.BoofBlockTileEntity;
import com.teamabnormals.endergetic.common.entity.BoofBlockEntity;
import com.teamabnormals.endergetic.core.registry.EEBlockEntityTypes;
import com.teamabnormals.endergetic.core.registry.EEBlocks;
import com.teamabnormals.endergetic.core.registry.EESoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.jetbrains.annotations.Nullable;

public class BoofBlock extends BaseEntityBlock {
	public static final BooleanProperty BOOFED = BooleanProperty.create("boofed");

	public BoofBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(BOOFED, false));
	}

	@Override
	@Nullable
	public BlockPathTypes getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
		return BlockPathTypes.DANGER_OTHER;
	}

	@Override
	public boolean isPossibleToRespawnInThis(BlockState state) {
		return false;
	}

	@Override
	public boolean isValidSpawn(BlockState state, BlockGetter level, BlockPos pos, Type type, EntityType<?> entityType) {
		return false;
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
		return blockState.getValue(BOOFED) ? 15 : super.getSignal(blockState, blockAccess, pos, side);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BOOFED);
	}

	@Override
	public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		if (entity.isSuppressingBounce()) {
			super.fallOn(level, state, pos, entity, fallDistance);
		} else {
			entity.causeFallDamage(fallDistance, 0.0F, level.damageSources().fall());
		}
	}

	@Override
	public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entity) {
		if (entity instanceof Player player) {
			player.fallDistance = 0;
		}
	}

	public static void doBoof(Level world, BlockPos pos) {
		if (!world.isClientSide) {
			BoofBlockEntity boofBlock = new BoofBlockEntity(world, pos);
			world.addFreshEntity(boofBlock);
			world.playSound(null, pos, EESoundEvents.BOOF_BLOCK_INFLATE.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
		}
		world.setBlockAndUpdate(pos, EEBlocks.BOOF_BLOCK.get().defaultBlockState().setValue(BOOFED, true));
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BoofBlockTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, EEBlockEntityTypes.BOOF_BLOCK.get(), BoofBlockTileEntity::tick);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	public static class BoofDispenseBehavior extends OptionalDispenseItemBehavior {

		@Override
		protected ItemStack execute(BlockSource source, ItemStack stack) {
			Level world = source.getLevel();
			Direction facing = source.getBlockState().getValue(DispenserBlock.FACING);
			BlockPos pos = source.getPos().relative(facing);
			if (world.getBlockState(pos).canBeReplaced()) {
				world.setBlockAndUpdate(pos, EEBlocks.DISPENSED_BOOF_BLOCK.get().defaultBlockState().setValue(DispensedBoofBlock.FACING, facing).setValue(DispensedBoofBlock.WATERLOGGED, world.getFluidState(pos).is(FluidTags.WATER)));
				world.playSound(null, pos, EESoundEvents.BOOF_BLOCK_INFLATE.get(), SoundSource.NEUTRAL, 0.85F, 0.9F + world.random.nextFloat() * 0.15F);
				this.setSuccess(true);
			}
			return stack;
		}

	}
}
