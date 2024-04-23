package com.teamabnormals.endergetic.common.entity.eetle.ai.charger;

import com.teamabnormals.blueprint.core.endimator.entity.EndimatedGoal;
import com.teamabnormals.endergetic.common.entity.eetle.ChargerEetle;
import com.teamabnormals.endergetic.core.registry.other.EEPlayableEndimations;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

public class EetleCatapultGoal extends EndimatedGoal<ChargerEetle> {
	private static final TargetingConditions PREDICATE = TargetingConditions.forCombat();
	private static final float MIN_DISTANCE = 2.0F;
	public int cooldown;

	public EetleCatapultGoal(ChargerEetle entity) {
		super(entity, EEPlayableEndimations.CHARGER_EETLE_CATAPULT);
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		if (this.cooldown > 0) {
			this.cooldown--;
		} else if (this.entity.getRandom().nextFloat() < 0.1F) {
			ChargerEetle chargerEetle = this.entity;
			LivingEntity attackTarget = chargerEetle.getTarget();
			if (attackTarget != null && attackTarget.isAlive() && !chargerEetle.isCatapultProjectile() && chargerEetle.onGround()) {
				Level world = chargerEetle.level();
				ChargerEetle closestCharger = world.getNearestEntity(world.getEntitiesOfClass(ChargerEetle.class, chargerEetle.getBoundingBox().inflate(2.5F), eetle -> {
					return eetle != chargerEetle && eetle.onGround() && !eetle.isBaby() && eetle.getTarget() == attackTarget && !eetle.isCatapulting() && attackTarget.distanceTo(eetle) >= MIN_DISTANCE;
				}), PREDICATE, null, chargerEetle.getX(), chargerEetle.getY(), chargerEetle.getZ());
				if (closestCharger != null) {
					chargerEetle.setCatapultingTarget(closestCharger);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean canContinueToUse() {
		ChargerEetle target = this.entity.getCatapultingTarget();
		if (target != null && target.isAlive()) {
			ChargerEetle charger = this.entity;
			LivingEntity attackTarget = charger.getTarget();
			return attackTarget != null && attackTarget.isAlive() && target.getTarget() == attackTarget && target.distanceTo(charger) <= charger.getAttributeValue(Attributes.FOLLOW_RANGE) && attackTarget.distanceTo(target) >= MIN_DISTANCE && charger.hasLineOfSight(target) && charger.onGround() && PREDICATE.test(charger, target) && PREDICATE.test(charger, attackTarget);
		}
		return false;
	}

	@Override
	public void tick() {
		ChargerEetle target = this.entity.getCatapultingTarget();
		if (target != null) {
			ChargerEetle charger = this.entity;
			charger.getLookControl().setLookAt(target, 30.0F, 30.0F);
			charger.getNavigation().moveTo(target, 1.5F);
			double distanceSq = target.distanceToSqr(charger.getX(), charger.getY(), charger.getZ());
			if (distanceSq <= charger.getBbWidth() * 2.0F * charger.getBbWidth() * 2.0F + target.getBbWidth()) {
				this.launchTarget();
				this.stop();
			}
		}
	}

	@Override
	public void stop() {
		this.resetCooldown();
		this.entity.setCatapultingTarget(null);
		this.entity.getNavigation().stop();
		this.entity.setAggressive(false);
		LivingEntity attackTarget = this.entity.getTarget();
		if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(attackTarget)) {
			this.entity.setTarget(null);
		}
	}

	public void resetCooldown() {
		this.cooldown = 200 + this.random.nextInt(61);
	}

	private void launchTarget() {
		ChargerEetle target = this.entity.getCatapultingTarget();
		if (target != null) {
			LivingEntity launchTo = target.getTarget();
			if (launchTo != null) {
				this.playEndimation();
				target.launchFromCatapult(launchTo);
			}
		}
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}
}
