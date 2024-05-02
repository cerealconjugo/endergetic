package com.teamabnormals.endergetic.common.block;

import com.teamabnormals.endergetic.client.particle.data.CorrockCrownParticleData;
import com.teamabnormals.endergetic.common.block.entity.CorrockCrownTileEntity;
import com.teamabnormals.endergetic.core.registry.EEParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class CorrockCrownBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected final DimensionalType dimensionalType;
	public final boolean petrified;

	protected CorrockCrownBlock(Properties builder, DimensionalType dimensionalType, boolean petrified) {
		super(builder);
		this.dimensionalType = dimensionalType;
		this.petrified = petrified;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CorrockCrownTileEntity(pos, state);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	public enum DimensionalType {
		OVERWORLD(() -> EEParticleTypes.OVERWORLD_CROWN.get()),
		NETHER(() -> EEParticleTypes.NETHER_CROWN.get()),
		END(() -> EEParticleTypes.END_CROWN.get());

		final Supplier<ParticleType<CorrockCrownParticleData>> particle;

		DimensionalType(Supplier<ParticleType<CorrockCrownParticleData>> particle) {
			this.particle = particle;
		}
	}
}