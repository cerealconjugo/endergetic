package com.minecraftabnormals.endergetic.common.entities.eetle.ai.brood;

import com.minecraftabnormals.endergetic.common.entities.eetle.BroodEetleEntity;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class BroodEetleFlyNearPosGoal extends Goal {
	private final BroodEetleEntity broodEetle;
	private double x;
	private double y;
	private double z;

	public BroodEetleFlyNearPosGoal(BroodEetleEntity broodEetle) {
		this.broodEetle = broodEetle;
		this.setFlags(EnumSet.of(Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		BroodEetleEntity broodEetle = this.broodEetle;
		if (broodEetle.isFlying() && broodEetle.hasWokenUp() && broodEetle.getRandom().nextFloat() < 0.1F) {
			BlockPos takeoffPos = broodEetle.takeoffPos;
			if (takeoffPos != null) {
				Vec3 vector3d = findPos(broodEetle, Vec3.atCenterOf(broodEetle.takeoffPos));
				if (vector3d == null) {
					return false;
				} else {
					this.x = vector3d.x;
					this.y = vector3d.y;
					this.z = vector3d.z;
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void start() {
		this.broodEetle.getNavigation().moveTo(this.x, this.y, this.z, 1.0F);
	}

	public boolean canContinueToUse() {
		return !this.broodEetle.getNavigation().isDone();
	}

	@Override
	public void stop() {
		this.broodEetle.getNavigation().stop();
	}

	@Nullable
	private static Vec3 findPos(BroodEetleEntity broodEetle, Vec3 takeoffPos) {
		Vec3 differenceNormalized = takeoffPos.subtract(broodEetle.position()).normalize();
		boolean verticalFarAway = Math.abs(broodEetle.getY() - takeoffPos.y()) >= 6;
		return RandomPos.generateRandomPos(broodEetle, 8, verticalFarAway ? 2 : 1, verticalFarAway ? -1 : 6, differenceNormalized, false, ((float)Math.PI / 2.0F), broodEetle::getWalkTargetValue, true, 2, 1, true);
	}
}