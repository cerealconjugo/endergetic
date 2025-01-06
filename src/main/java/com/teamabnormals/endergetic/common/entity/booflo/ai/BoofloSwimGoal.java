package com.teamabnormals.endergetic.common.entity.booflo.ai;

import com.teamabnormals.endergetic.common.entity.booflo.Booflo;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class BoofloSwimGoal extends RandomStrollGoal {

	public BoofloSwimGoal(PathfinderMob booflo, double p_i48937_2_, int p_i48937_4_) {
		super(booflo, p_i48937_2_, p_i48937_4_);
	}

	@Nullable
	protected Vec3 getPosition() {
		Vec3 vec3d = DefaultRandomPos.getPos(this.mob, 8, 2);

		for (int i = 0; vec3d != null && !this.mob.level().getBlockState(BlockPos.containing(vec3d)).isPathfindable(this.mob.level(), BlockPos.containing(vec3d), PathComputationType.AIR) && i++ < 8; vec3d = DefaultRandomPos.getPos(this.mob, 10, 2)) {
		}

		return vec3d;
	}

	@Override
	public boolean canUse() {
		if (!((Booflo) this.mob).isBoofed()) return false;
		if (this.mob.isVehicle()) {
			return false;
		} else {
			if (this.mob.getRandom().nextInt(this.interval) != 0) {
				return false;
			}
		}

		Vec3 vec3d = this.getPosition();
		if (vec3d == null) {
			return false;
		} else {
			this.wantedX = vec3d.x;
			this.wantedY = vec3d.y;
			this.wantedZ = vec3d.z;
			this.forceTrigger = false;
			return !this.mob.isInWater();
		}
	}

	@Override
	public boolean canContinueToUse() {
		return super.canContinueToUse() && !this.mob.isInWater();
	}

}