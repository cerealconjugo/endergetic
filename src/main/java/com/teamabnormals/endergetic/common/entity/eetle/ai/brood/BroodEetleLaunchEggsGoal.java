package com.teamabnormals.endergetic.common.entity.eetle.ai.brood;

import com.teamabnormals.blueprint.core.endimator.entity.EndimatedGoal;
import com.teamabnormals.endergetic.common.entity.eetle.AbstractEetle;
import com.teamabnormals.endergetic.common.entity.eetle.BroodEetle;
import com.teamabnormals.endergetic.common.entity.eetle.EetleEgg;
import com.teamabnormals.endergetic.core.registry.other.EEPlayableEndimations;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.sensing.Sensing;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class BroodEetleLaunchEggsGoal extends EndimatedGoal<BroodEetle> {
	private int ticksOffGround;
	private int shotsToFire;
	private int ticksPassed;

	public BroodEetleLaunchEggsGoal(BroodEetle entity) {
		super(entity, EEPlayableEndimations.BROOD_EETLE_LAUNCH);
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		BroodEetle broodEetle = this.entity;
		if ((broodEetle.canFireEggCannon() || broodEetle.getEggCannonCooldown() <= 200 && this.random.nextFloat() < 0.05F && BroodEetleDropEggsGoal.areFewEetlesNearby(broodEetle)) && broodEetle.hasWokenUp() && broodEetle.onGround() && broodEetle.isNoEndimationPlaying() && !broodEetle.isFlying()) {
			List<LivingEntity> aggressors = BroodEetleFlingGoal.searchForNearbyAggressors(broodEetle, broodEetle.getAttributeValue(Attributes.FOLLOW_RANGE) * 0.5D);
			return computeAverageAggressorDistanceSq(broodEetle.position(), aggressors) >= 42.25F || !aggressors.isEmpty() && this.random.nextFloat() < 0.01F;
		}
		return false;
	}

	@Override
	public boolean canContinueToUse() {
		if (this.entity.onGround()) {
			this.ticksOffGround = 0;
		}
		return this.shotsToFire > 0 && ++this.ticksOffGround < 10;
	}

	@Override
	public void start() {
		BroodEetle broodEetle = this.entity;
		broodEetle.setFiringCannon(true);
		this.shotsToFire = this.random.nextInt(3) + 5 - (int) Math.min(5.0F, (float) getNearbyEetleCount(broodEetle) * 0.625F);
	}

	@Override
	public void tick() {
		this.ticksPassed++;

		BroodEetle broodEetle = this.entity;
		if (broodEetle.isEggMouthOpen() && this.ticksPassed % 20 == 0) {
			this.playEndimation();
			Vec3 firingPos = new Vec3(-1.0D, 3.0D, 0.0D).yRot(-broodEetle.yBodyRot * ((float) Math.PI / 180F) - ((float) Math.PI / 2F));
			EetleEgg eetleEgg = new EetleEgg(broodEetle.level(), broodEetle.position().add(firingPos));
			RandomSource random = broodEetle.getRandom();
			eetleEgg.setEggSize(EetleEgg.EggSize.random(random, true));
			eetleEgg.setDeltaMovement(new Vec3((random.nextFloat() - random.nextFloat()) * 0.35F, 0.8F + random.nextFloat() * 0.1F, (random.nextFloat() - random.nextFloat()) * 0.35F));
			broodEetle.level().addFreshEntity(eetleEgg);
			this.shotsToFire--;
		}
	}

	@Override
	public void stop() {
		BroodEetle broodEetle = this.entity;
		broodEetle.setFiringCannon(false);
		broodEetle.resetEggCannonCooldown();
		this.ticksOffGround = 0;
		this.shotsToFire = 0;
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	private static double computeAverageAggressorDistanceSq(Vec3 pos, List<LivingEntity> aggressors) {
		double total = 0.0F;
		for (LivingEntity livingEntity : aggressors) {
			total += pos.distanceToSqr(livingEntity.position());
		}
		return total / aggressors.size();
	}

	public static int getNearbyEetleCount(BroodEetle broodEetle) {
		double followRange = broodEetle.getAttributeValue(Attributes.FOLLOW_RANGE);
		double posY = broodEetle.getY();
		Sensing senses = broodEetle.getSensing();
		return broodEetle.level().getEntitiesOfClass(AbstractEetle.class, broodEetle.getBoundingBox().inflate(followRange, followRange * 0.75D, followRange), eetle -> {
			if (eetle.getY() - posY >= 0.5F && !senses.hasLineOfSight(eetle)) {
				return false;
			}
			return eetle.isAlive() && (!eetle.isBaby() || eetle.getGrowingAge() >= -240);
		}).size();
	}
}
