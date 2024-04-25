package com.teamabnormals.endergetic.common.entity.booflo.ai;

import com.teamabnormals.blueprint.core.endimator.PlayableEndimation;
import com.teamabnormals.blueprint.core.endimator.entity.EndimatedGoal;
import com.teamabnormals.endergetic.common.entity.booflo.Booflo;
import com.teamabnormals.endergetic.core.other.EEPlayableEndimations;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class BoofloEatPuffBugGoal extends EndimatedGoal<Booflo> {
	private float originalYaw;
	private int soundDelay;

	public BoofloEatPuffBugGoal(Booflo booflo) {
		super(booflo, EEPlayableEndimations.BOOFLO_EAT);
	}

	@Override
	public boolean canUse() {
		if (this.entity.isPlayerNear(1.0F)) {
			if (!this.entity.isTamed()) {
				return false;
			}
		}
		return this.entity.isNoEndimationPlaying() && this.entity.hasCaughtPuffBug() && !this.entity.isBoofed() && this.entity.onGround() && !this.entity.isInLove();
	}

	@Override
	public boolean canContinueToUse() {
		boolean flag = true;
		if (!this.entity.hasCaughtPuffBug()) {
			if (this.entity.getAnimationTick() < 140) {
				flag = false;
			}
		}

		if (this.entity.isPlayerNear(0.6F)) {
			if (!this.entity.isTamed()) {
				this.entity.hopDelay = 0;
				for (Player players : this.entity.getNearbyPlayers(0.6F)) {
					if (!this.entity.hasAggressiveAttackTarget()) {
						this.entity.setBoofloAttackTargetId(players.getId());
					}
				}
				return false;
			}
		}
		return this.isEndimationPlaying() && flag && !this.entity.isBoofed() && this.entity.onGround();
	}

	@Override
	public void start() {
		this.playEndimation();
		this.originalYaw = this.entity.getYRot();
		this.entity.setLockedYaw(this.originalYaw);
	}

	@Override
	public void stop() {
		this.originalYaw = 0;
		this.playEndimation(PlayableEndimation.BLANK);
	}

	@Override
	public void tick() {
		if (this.soundDelay > 0) this.soundDelay--;

		this.entity.setYRot(this.originalYaw);
		this.entity.yRotO = this.originalYaw;

		if (this.entity.isPlayerNear(1.0F) && this.soundDelay == 0) {
			if (!this.entity.isTamed()) {
				this.entity.playSound(this.entity.getGrowlSound(), 0.75F, (float) Mth.clamp(this.entity.getRandom().nextFloat() * 1.0, 0.95F, 1.0F));
				this.soundDelay = 50;
			}
		}
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}
}