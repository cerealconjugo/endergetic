package com.teamabnormals.endergetic.common.entity.booflo.ai;

import com.teamabnormals.endergetic.common.entity.booflo.Booflo;
import com.teamabnormals.endergetic.common.entity.booflo.BoofloBaby;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.List;

public class BabyFollowParentGoal extends Goal {
	private final BoofloBaby baby;
	private Booflo parent;
	private final double moveSpeed;
	private int delayCounter;

	public BabyFollowParentGoal(BoofloBaby booflo, double speed) {
		this.baby = booflo;
		this.moveSpeed = speed;
	}

	public boolean canUse() {
		List<Booflo> list = this.baby.level().getEntitiesOfClass(Booflo.class, this.baby.getBoundingBox().inflate(10.0D, 8.0D, 10.0D));
		Booflo booflo = null;
		double d0 = Double.MAX_VALUE;

		for (Booflo booflos : list) {
			double d1 = this.baby.distanceToSqr(booflos);
			if (!(d1 > d0)) {
				d0 = d1;
				booflo = booflos;
			}
		}

		if (booflo == null) {
			return false;
		} else if (d0 < 9.0D) {
			return false;
		} else {
			if (this.baby.getRandom().nextFloat() < 0.25F) {
				this.parent = booflo;
				return true;
			}
			return false;
		}
	}

	public boolean canContinueToUse() {
		if (!this.parent.isAlive()) {
			return false;
		} else {
			double d0 = this.baby.distanceToSqr(this.parent);
			return !(d0 < 9.0D) && !(d0 > 256.0D);
		}
	}

	public void start() {
		this.delayCounter = 0;
	}

	public void stop() {
		this.parent = null;
	}

	public void tick() {
		if (--this.delayCounter <= 0) {
			this.delayCounter = 10;
			this.baby.getNavigation().moveTo(this.parent, this.moveSpeed);
		}
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}
}