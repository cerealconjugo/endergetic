package com.teamabnormals.endergetic.common.entity.booflo.ai;

import com.teamabnormals.endergetic.common.entity.bolloom.BolloomFruit;
import com.teamabnormals.endergetic.common.entity.booflo.Booflo;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;

import java.util.EnumSet;

public class BoofloHuntFruitGoal extends Goal {
	private final Booflo booflo;
	protected int attackTick;
	private final double speedTowardsTarget;
	private Path path;
	private int delayCounter;
	private double targetX;
	private double targetY;
	private double targetZ;
	private long lastCanUseCheck;

	public BoofloHuntFruitGoal(Booflo booflo, double speed) {
		this.booflo = booflo;
		this.speedTowardsTarget = speed;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	public boolean canUse() {
		long i = this.booflo.level().getGameTime();
		if (i - this.lastCanUseCheck < 20L) {
			return false;
		} else {
			this.lastCanUseCheck = i;
			Entity target = this.booflo.getBoofloAttackTarget();
			if (target == null) {
				return false;
			} else if (!target.isAlive()) {
				return false;
			} else if (this.booflo.hasAggressiveAttackTarget()) {
				return false;
			} else if (!this.booflo.isBoofed()) {
				return false;
			} else {
				this.path = this.booflo.getNavigation().createPath(target.blockPosition(), 0);
				if (this.path != null) {
					return true;
				} else {
					return this.getAttackReachSqr(target) >= this.booflo.distanceToSqr(target.getX(), target.getBoundingBox().minY, target.getZ());
				}
			}
		}
	}

	@Override
	public boolean canContinueToUse() {
		Entity target = this.booflo.getBoofloAttackTarget();
		if (target == null) {
			return false;
		} else if (!target.isAlive()) {
			return false;
		} else if (!this.booflo.isBoofed()) {
			return false;
		} else if (!this.booflo.isWithinRestriction(target.blockPosition())) {
			return false;
		} else {
			return !(target instanceof Player) || !target.isSpectator() && !((Player) target).isCreative();
		}
	}

	public void start() {
		this.booflo.getNavigation().moveTo(this.path, 1.0F);
		this.booflo.setAggressive(true);
		this.delayCounter = 0;
	}

	public void stop() {
		Entity target = this.booflo.getBoofloAttackTarget();
		if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) {
			this.booflo.setBoofloAttackTargetId(0);
		}
		this.booflo.setAggressive(false);
		this.booflo.getNavigation().stop();
	}

	public void tick() {
		this.delayCounter--;
		Entity target = this.booflo.getBoofloAttackTarget();
		//TODO: Weird way of storing the target, needs rework
		if (target == null) return;

		double distToEnemySqr = this.booflo.distanceToSqr(target.getX(), target.getBoundingBox().minY, target.getZ());
		this.booflo.getLookControl().setLookAt(target.getX(), target.getY(), target.getZ(), 10.0F, 10.0F);

		if (this.delayCounter <= 0 || target.distanceToSqr(this.targetX, this.targetY, this.targetZ) >= 1.0D || this.booflo.getRandom().nextFloat() < 0.05F) {
			this.targetX = target.getX();
			this.targetY = target.getBoundingBox().minY;
			this.targetZ = target.getZ();

			this.delayCounter = 4 + this.booflo.getRandom().nextInt(7);

			if (distToEnemySqr > 1024.0D) {
				this.delayCounter += 5;
			} else if (distToEnemySqr > 256.0D) {
				this.delayCounter += 5;
			}

			if (!this.booflo.getNavigation().moveTo(target, this.speedTowardsTarget)) {
				this.delayCounter += 5;
			}
		}

		this.attackTick = Math.max(this.attackTick - 1, 0);
		this.tryToCapturePrey(target, distToEnemySqr);
	}

	protected void tryToCapturePrey(Entity prey, double distToEnemySqr) {
		double attackReach = this.getAttackReachSqr(prey);
		if (distToEnemySqr <= attackReach && this.attackTick <= 0) {
			this.attackTick = 20;
			if (prey instanceof BolloomFruit) {
				((BolloomFruit) prey).onBroken(false);
				this.booflo.setCaughtFruit(true);
				this.booflo.setHungry(false);
				prey.discard();
			}
		}
	}

	protected double getAttackReachSqr(Entity attackTarget) {
		return (this.booflo.getBbWidth() * 2.0F * this.booflo.getBbWidth() * 2.0F + attackTarget.getBbWidth()) * 0.85F;
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}
}