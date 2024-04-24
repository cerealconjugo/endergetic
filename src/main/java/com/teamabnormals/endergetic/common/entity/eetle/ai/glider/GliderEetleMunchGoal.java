package com.teamabnormals.endergetic.common.entity.eetle.ai.glider;

import com.teamabnormals.blueprint.core.endimator.entity.EndimatedGoal;
import com.teamabnormals.endergetic.common.entity.eetle.GliderEetle;
import com.teamabnormals.endergetic.core.registry.other.EEDamageTypes;
import com.teamabnormals.endergetic.core.registry.other.EEPlayableEndimations;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class GliderEetleMunchGoal extends EndimatedGoal<GliderEetle> {
	private int munchCooldown;

	public GliderEetleMunchGoal(GliderEetle glider) {
		super(glider, EEPlayableEndimations.GLIDER_EETLE_MUNCH);
	}

	@Override
	public boolean canUse() {
		if (this.munchCooldown > 0) {
			this.munchCooldown--;
		} else if (this.entity.getRandom().nextFloat() < 0.05F) {
			LivingEntity attackTarget = this.entity.getTarget();
			if (attackTarget != null) {
				return this.entity.getPassengers().contains(attackTarget) && this.entity.isFlying() && this.isNoEndimationPlaying();
			}
		}
		return false;
	}

	@Override
	public void start() {
		this.playEndimation();
	}

	@Override
	public boolean canContinueToUse() {
		return this.isEndimationPlaying() && this.entity.isFlying();
	}

	@Override
	public void tick() {
		LivingEntity attackTarget = this.entity.getTarget();
		if (attackTarget != null && this.entity.getPassengers().contains(attackTarget)) {
			if (this.isEndimationAtTick(8) || this.isEndimationAtTick(18)) {
				attackTarget.hurt(EEDamageTypes.munch(this.entity.level(), this.entity), (float) this.entity.getAttributeValue(Attributes.ATTACK_DAMAGE) + attackTarget.getRandom().nextInt(2));
			}
		}
	}

	@Override
	public void stop() {
		this.munchCooldown = this.entity.getRandom().nextInt(41) + 20;
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}
}
