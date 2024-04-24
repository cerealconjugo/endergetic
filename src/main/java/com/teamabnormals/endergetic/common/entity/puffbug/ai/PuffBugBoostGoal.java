package com.teamabnormals.endergetic.common.entity.puffbug.ai;

import com.teamabnormals.endergetic.common.entity.puffbug.PuffBug;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class PuffBugBoostGoal extends RandomStrollGoal {

	public PuffBugBoostGoal(PuffBug puffbug) {
		super(puffbug, 1.0F, 15);
	}

	public boolean canUse() {
		if (this.mob.isVehicle()) {
			return false;
		} else {
			if (!this.forceTrigger) {
				if (this.mob.getRandom().nextInt(this.interval) != 0) {
					return false;
				}
			}

			Vec3 destination = this.getPosition();
			if (destination == null) {
				return false;
			} else {
				this.wantedX = destination.x;
				this.wantedY = destination.y;
				this.wantedZ = destination.z;
				this.forceTrigger = false;
				return true;
			}
		}
	}

	@Nullable
	protected Vec3 getPosition() {
		Vec3 view = this.mob.getViewVector(0.0F);
		double viewX = view.x;
		double viewZ = view.z;
		Vec3 vec3d = HoverRandomPos.getPos(this.mob, 8, 5, viewX, viewZ, ((float) Math.PI / 2F), 3, 1);

		for (int i = 0; vec3d != null && !this.mob.level().getBlockState(BlockPos.containing(vec3d)).isPathfindable(this.mob.level(), BlockPos.containing(vec3d), PathComputationType.AIR) && i++ < 10; vec3d = HoverRandomPos.getPos(this.mob, 8, 5, viewX, viewZ, ((float) Math.PI / 2F), 3, 1)) {
			;
		}

		return vec3d;
	}

}