package com.teamabnormals.endergetic.common.entity.eetle.ai.brood;

import com.teamabnormals.blueprint.core.util.NetworkUtil;
import com.teamabnormals.endergetic.common.entity.eetle.BroodEetle;
import com.teamabnormals.endergetic.common.entity.eetle.EetleEgg;
import com.teamabnormals.endergetic.core.other.EEPlayableEndimations;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class BroodEetleLastStageGoal extends Goal {
	private final BroodEetle broodEetle;
	private int cannonTicks;

	public BroodEetleLastStageGoal(BroodEetle broodEetle) {
		this.broodEetle = broodEetle;
		this.setFlags(EnumSet.allOf(Goal.Flag.class));
	}

	@Override
	public boolean canUse() {
		BroodEetle broodEetle = this.broodEetle;
		return broodEetle.isOnLastHealthStage() && broodEetle.isAlive() && !broodEetle.isFlying();
	}

	@Override
	public void start() {
		BroodEetle broodEetle = this.broodEetle;
		broodEetle.getNavigation().stop();
		NetworkUtil.setPlayingAnimation(broodEetle, EEPlayableEndimations.BROOD_EETLE_SLAM);
		broodEetle.setFiringCannon(true);
	}

	@Override
	public void tick() {
		BroodEetle broodEetle = this.broodEetle;
		if (broodEetle.isFiringCannon()) {
			this.cannonTicks++;

			if (broodEetle.isEndimationPlaying(EEPlayableEndimations.BROOD_EETLE_SLAM)) {
				if (broodEetle.getAnimationTick() == 14) {
					BroodEetleSlamGoal.slam(broodEetle, broodEetle.getRandom(), 1.25F);
				}
			} else if (broodEetle.isEggMouthOpen() && this.cannonTicks % 20 == 0) {
				RandomSource random = broodEetle.getRandom();
				if (BroodEetleLaunchEggsGoal.getNearbyEetleCount(broodEetle) <= 9 || this.cannonTicks <= 75 || random.nextFloat() <= 0.05F) {
					NetworkUtil.setPlayingAnimation(broodEetle, EEPlayableEndimations.BROOD_EETLE_LAUNCH);
					Vec3 firingPos = new Vec3(-1.0D, 3.0D, 0.0D).yRot(-broodEetle.yBodyRot * ((float) Math.PI / 180F) - ((float) Math.PI / 2F));
					EetleEgg eetleEgg = new EetleEgg(broodEetle.level(), broodEetle.position().add(firingPos));
					eetleEgg.setEggSize(EetleEgg.EggSize.random(random, false));
					eetleEgg.setDeltaMovement(new Vec3((random.nextFloat() - random.nextFloat()) * 0.35F, 0.8F + random.nextFloat() * 0.1F, (random.nextFloat() - random.nextFloat()) * 0.35F));
					broodEetle.level().addFreshEntity(eetleEgg);
				} else {
					broodEetle.heal(5.0F);
					broodEetle.level().broadcastEntityEvent(broodEetle, (byte) 60);
				}
			}
		}
	}

	@Override
	public boolean canContinueToUse() {
		BroodEetle broodEetle = this.broodEetle;
		return broodEetle.isAlive() && broodEetle.isOnLastHealthStage();
	}

	@Override
	public void stop() {
		BroodEetle broodEetle = this.broodEetle;
		broodEetle.setFiringCannon(false);
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}
}
