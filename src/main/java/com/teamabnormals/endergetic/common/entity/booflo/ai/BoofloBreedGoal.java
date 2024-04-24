package com.teamabnormals.endergetic.common.entity.booflo.ai;

import com.teamabnormals.blueprint.core.util.NetworkUtil;
import com.teamabnormals.endergetic.common.advancement.EECriteriaTriggers;
import com.teamabnormals.endergetic.common.entity.booflo.Booflo;
import com.teamabnormals.endergetic.common.entity.booflo.Booflo.GroundMoveHelperController;
import com.teamabnormals.endergetic.core.registry.other.EEPlayableEndimations;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class BoofloBreedGoal extends Goal {
	private static final TargetingConditions MATE_CHECKER = (TargetingConditions.forNonCombat()).range(16.0D).ignoreLineOfSight();
	protected final Booflo booflo;
	protected Booflo mate;
	private int impregnateDelay;

	public BoofloBreedGoal(Booflo booflo) {
		this.booflo = booflo;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		if (this.booflo.isBoofed() || (!this.booflo.onGround() && this.booflo.getVehicle() == null) || !this.booflo.isInLove() || this.booflo.isPregnant()) {
			return false;
		} else {
			this.mate = this.getNearbyMate();
			return this.mate != null && !this.mate.isPregnant();
		}
	}

	@Override
	public boolean canContinueToUse() {
		return !this.booflo.isBoofed() && this.mate.isAlive() && this.mate.isInLove() && this.impregnateDelay < 100;
	}

	public void stop() {
		this.mate = null;
		this.impregnateDelay = 0;
	}

	public void tick() {
		if (this.booflo.hopDelay == 0 && this.booflo.isNoEndimationPlaying() && !this.isBeingRidenOrRiding()) {
			NetworkUtil.setPlayingAnimation(this.booflo, EEPlayableEndimations.BOOFLO_HOP);
		}

		if (this.booflo.getMoveControl() instanceof GroundMoveHelperController && !this.isBeingRidenOrRiding()) {
			((GroundMoveHelperController) this.booflo.getMoveControl()).setSpeed(0.1D);
		}

		double dx = this.mate.getX() - this.booflo.getX();
		double dz = this.mate.getZ() - this.booflo.getZ();

		float angle = (float) (Mth.atan2(dz, dx) * (180F / Math.PI)) - 90.0F;

		if (this.booflo.getMoveControl() instanceof GroundMoveHelperController && !this.isBeingRidenOrRiding()) {
			((GroundMoveHelperController) this.booflo.getMoveControl()).setDirection(angle, false);
		}

		this.booflo.getNavigation().moveTo(this.mate, 1.0D);

		this.impregnateDelay++;
		if (this.impregnateDelay >= 60 && this.booflo.distanceToSqr(this.mate) < 10.0D) {
			this.impregnateBooflo();
		}
	}

	protected void impregnateBooflo() {
		final BabyEntitySpawnEvent event = new net.minecraftforge.event.entity.living.BabyEntitySpawnEvent(this.booflo, this.mate, null);
		final boolean cancelled = MinecraftForge.EVENT_BUS.post(event);
		if (cancelled) {
			this.booflo.resetInLove();
			this.mate.resetInLove();
			return;
		}

		ServerPlayer serverplayerentity = this.booflo.getLoveCause();
		if (serverplayerentity == null && this.mate.getLoveCause() != null) {
			serverplayerentity = this.mate.getLoveCause();
		}

		if (serverplayerentity != null) {
			serverplayerentity.awardStat(Stats.ANIMALS_BRED);
			EECriteriaTriggers.BRED_BOOFLO.trigger(serverplayerentity);
		}

		if (!this.mate.isPregnant()) {
			this.booflo.babiesToBirth = 3;
		}

		this.booflo.resetInLove();
		this.mate.resetInLove();
		this.booflo.breedDelay = 1400;
		this.mate.breedDelay = 1400;

		this.booflo.level().broadcastEntityEvent(this.booflo, (byte) 18);
		if (this.booflo.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
			this.booflo.level().addFreshEntity(new ExperienceOrb(this.booflo.level(), this.booflo.getX(), this.booflo.getY(), this.booflo.getZ(), this.booflo.getRandom().nextInt(7) + 1));
		}
	}

	@Nullable
	private Booflo getNearbyMate() {
		List<Booflo> list = this.booflo.level().getNearbyEntities(Booflo.class, MATE_CHECKER, this.booflo, this.booflo.getBoundingBox().inflate(16.0D));
		double d0 = Double.MAX_VALUE;
		Booflo booflo = null;

		for (Booflo booflos : list) {
			if (this.booflo.canMateWith(booflos) && this.booflo.distanceToSqr(booflos) < d0) {
				booflo = booflos;
				d0 = this.booflo.distanceToSqr(booflos);
			}
		}

		return booflo;
	}

	private boolean isBeingRidenOrRiding() {
		return this.booflo.isPassenger() || this.booflo.isVehicle();
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}
}