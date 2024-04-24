package com.teamabnormals.endergetic.common.entity.eetle.ai.glider;

import com.teamabnormals.endergetic.common.entity.eetle.GliderEetle;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.Path;

import java.util.EnumSet;

/**
 * Makes the Glider Eetle hover nearby the target when the target is caught by another Glider Eetle.
 */
public class GliderEetleHoverNearTargetGoal extends Goal {
	private final GliderEetle glider;
	private Path path;
	private int delayCounter;

	public GliderEetleHoverNearTargetGoal(GliderEetle glider) {
		this.glider = glider;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		GliderEetle glider = this.glider;
		if (glider.isGrounded()) return false;
		LivingEntity attackTarget = glider.getTarget();
		if (attackTarget != null && attackTarget.isAlive() && attackTarget.getVehicle() instanceof GliderEetle && glider.getPassengers().isEmpty()) {
			this.path = this.glider.getNavigation().createPath(attackTarget, 5);
			return this.path != null;
		}
		return false;
	}

	@Override
	public void start() {
		GliderEetle glider = this.glider;
		if (!glider.isFlying()) {
			glider.setFlying(true);
		}
		glider.getNavigation().moveTo(this.path, 1.25F);
		glider.setAggressive(true);
	}

	@Override
	public boolean canContinueToUse() {
		if (this.glider.isGrounded()) return false;
		LivingEntity target = this.glider.getTarget();
		return target != null && target.getVehicle() instanceof GliderEetle && this.glider.getPassengers().isEmpty() && this.glider.getNavigation().isInProgress();
	}

	@Override
	public void tick() {
		this.delayCounter = Math.max(this.delayCounter - 1, 0);
		GliderEetle glider = this.glider;
		LivingEntity target = glider.getTarget();
		double distanceToTargetSq = glider.distanceToSqr(target);
		boolean canSeeTarget = glider.getSensing().hasLineOfSight(target);
		if (canSeeTarget && this.delayCounter <= 0 && glider.getRandom().nextFloat() < 0.05F) {
			this.delayCounter = 4 + glider.getRandom().nextInt(9);
			PathNavigation pathNavigator = glider.getNavigation();
			if (distanceToTargetSq > 1024.0D) {
				this.delayCounter += 10;
			} else if (distanceToTargetSq > 256.0D) {
				this.delayCounter += 5;
			}

			Path path = pathNavigator.createPath(GliderEetleGrabGoal.getAirPosAboveTarget(glider.level(), target), 5);
			if (path == null || !pathNavigator.moveTo(path, 1.25F)) {
				this.delayCounter += 15;
			}
		}
	}

	@Override
	public void stop() {
		GliderEetle glider = this.glider;
		LivingEntity livingentity = glider.getTarget();
		if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
			glider.setTarget(null);
		}
		glider.setAggressive(false);
		glider.getNavigation().stop();
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}
}
