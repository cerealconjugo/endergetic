package com.teamabnormals.endergetic.common.entity.booflo.ai;

import com.teamabnormals.blueprint.core.endimator.entity.EndimatedGoal;
import com.teamabnormals.endergetic.common.entity.booflo.BoofloAdolescent;
import com.teamabnormals.endergetic.core.registry.other.EEPlayableEndimations;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;

public class AdolescentEatGoal extends EndimatedGoal<BoofloAdolescent> {
	private int eatingTicks;

	public AdolescentEatGoal(BoofloAdolescent adolescent) {
		super(adolescent, EEPlayableEndimations.ADOLESCENT_BOOFLO_EATING);
	}

	@Override
	public boolean canUse() {
		if (this.entity.isPlayerNear()) {
			return false;
		}
		return this.entity.getRandom().nextInt(40) == 0 && this.entity.hasFruit() && this.isSafePos();
	}

	@Override
	public boolean canContinueToUse() {
		if (this.entity.isPlayerNear()) {
			return false;
		}

		if (this.entity.isDescenting()) {
			return this.isSafePos() && this.entity.hasFruit();
		} else if (this.entity.isEating()) {
			return this.entity.onGround() && this.entity.hasFruit() && this.eatingTicks < 61;
		}

		return false;
	}

	@Override
	public void start() {
		this.entity.setDescenting(true);
		this.entity.setSpeed(0.0F);
		this.entity.getNavigation().stop();
	}

	@Override
	public void stop() {
		if (this.entity.isDescenting()) {
			this.entity.setDescenting(false);
		}

		if (this.entity.isEating()) {
			this.entity.setEating(false);
			this.entity.dropFruit();
			this.entity.resetEndimation();
		}

		this.eatingTicks = 0;
	}

	@Override
	public void tick() {
		this.entity.setSpeed(0.0F);
		this.entity.getNavigation().stop();

		if (this.entity.isDescenting()) {
			if (this.entity.onGround()) {
				this.entity.setEating(true);
				this.entity.setDescenting(false);
			}
		} else if (this.entity.isEating()) {
			this.eatingTicks++;

			if (this.eatingTicks % 10 == 0) {
				this.playEndimation();
				if (this.eatingTicks < 60) {
					this.playEndimation();
				}
			}

			if (this.eatingTicks == 60) {
				this.entity.resetEndimation();
				this.entity.setHungry(false);
				this.entity.setHasFruit(false);
				this.entity.setEating(false);
				this.entity.setEaten(true);
			}
		}
	}

	private boolean isSafePos() {
		BlockPos pos = this.entity.blockPosition();
		for (int i = 0; i < 10; i++) {
			pos = pos.below(i);
			if (Block.canSupportRigidBlock(this.entity.level(), pos) && i >= 4) {
				if (this.entity.level().getBlockState(pos).getFluidState().isEmpty() && !this.entity.level().getBlockState(pos).isBurning(this.entity.level(), pos)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}
}