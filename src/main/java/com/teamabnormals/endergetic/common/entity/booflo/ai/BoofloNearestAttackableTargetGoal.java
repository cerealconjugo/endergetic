package com.teamabnormals.endergetic.common.entity.booflo.ai;

import com.teamabnormals.endergetic.common.entity.booflo.Booflo;
import com.teamabnormals.endergetic.common.entity.booflo.BoofloAdolescent;
import com.teamabnormals.endergetic.common.entity.puffbug.PuffBug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class BoofloNearestAttackableTargetGoal<E extends Entity> extends TargetGoal {
	protected final Class<E> targetClass;
	protected final int targetChance;
	protected Entity nearestTarget;
	protected TargetingConditions targetEntitySelector;

	public BoofloNearestAttackableTargetGoal(Mob attacker, Class<E> targetClass, boolean p_i50313_3_) {
		this(attacker, targetClass, p_i50313_3_, false);
	}

	public BoofloNearestAttackableTargetGoal(Mob attacker, Class<E> targetClass, boolean p_i50314_3_, boolean p_i50314_4_) {
		this(attacker, targetClass, 5, p_i50314_3_, p_i50314_4_);
	}

	public BoofloNearestAttackableTargetGoal(Mob p_i50315_1_, Class<E> p_i50315_2_, int p_i50315_3_, boolean p_i50315_4_, boolean p_i50315_5_) {
		super(p_i50315_1_, p_i50315_4_, p_i50315_5_);
		this.targetClass = p_i50315_2_;
		this.targetChance = p_i50315_3_;
		this.setFlags(EnumSet.of(Flag.TARGET));
		this.targetEntitySelector = TargetingConditions.DEFAULT.range(this.getFollowDistance());
	}

	public boolean canUse() {
		if (this.targetChance > 0 && this.mob.getRandom().nextInt(this.targetChance) != 0) {
			return false;
		} else if (this.mob instanceof BoofloAdolescent && !((BoofloAdolescent) this.mob).isHungry()) {
			return false;
		} else if (this.mob instanceof Booflo && (((Booflo) this.mob).getBoofloAttackTarget() != null || !((Booflo) this.mob).isBoofed() || !((Booflo) this.mob).isHungry())) {
			return false;
		} else {
			if (this.mob instanceof Booflo && (((Booflo) this.mob).isTamed() && this.targetClass == PuffBug.class)) {
				return false;
			}

			this.findNearestTarget();
			return this.nearestTarget != null;
		}
	}

	protected AABB getTargetableArea(double targetDistance) {
		return this.mob.getBoundingBox().inflate(targetDistance, 4.0D, targetDistance);
	}

	protected void findNearestTarget() {
		this.nearestTarget = this.findEntity(this.targetClass, this.targetEntitySelector, this.mob, this.mob.getX(), this.mob.getY() + (double) this.mob.getEyeHeight(), this.mob.getZ(), this.getTargetableArea(this.getFollowDistance()));
	}

	public void start() {
		if (this.mob instanceof Booflo) {
			((Booflo) this.mob).setBoofloAttackTargetId(this.nearestTarget.getId());
		} else {
			((BoofloAdolescent) this.mob).setBoofloAttackTarget(this.nearestTarget);
		}
		super.start();
	}

	@Nullable
	public E findEntity(Class<? extends E> target, TargetingConditions predicate, @Nullable LivingEntity attacker, double p_225318_4_, double p_225318_6_, double p_225318_8_, AABB bb) {
		return this.getClosestEntity(attacker.level().getEntitiesOfClass(target, bb, entity -> true), predicate, attacker, p_225318_4_, p_225318_6_, p_225318_8_);
	}

	@Nullable
	private E getClosestEntity(List<? extends E> p_217361_1_, TargetingConditions p_217361_2_, @Nullable LivingEntity attacker, double p_217361_4_, double p_217361_6_, double p_217361_8_) {
		double d0 = -1.0D;
		E e = null;

		for (E e1 : p_217361_1_) {
			if (this.canTarget(attacker, e1)) {
				double d1 = e1.distanceToSqr(p_217361_4_, p_217361_6_, p_217361_8_);
				if (d0 == -1.0D || d1 < d0) {
					d0 = d1;
					e = e1;
				}
			}
		}

		return e;
	}

	public boolean canTarget(@Nullable LivingEntity attacker, Entity target) {
		if (attacker == target) {
			return false;
		} else if (target.isSpectator()) {
			return false;
		} else if (!target.isAlive()) {
			return false;
		} else if (target.isInvulnerable()) {
			return false;
		} else {
			if (attacker != null) {
				if (!attacker.canAttackType(target.getType())) {
					return false;
				}
				if (attacker.isAlliedTo(target)) {
					return false;
				}

				if (this.getFollowDistance() > 0.0D) {
					double d1 = this.getFollowDistance();
					double d2 = attacker.distanceToSqr(target.getX(), target.getY(), target.getZ());
					if (d2 > d1 * d1) {
						return false;
					}
				}

				return !(attacker instanceof Mob) || ((Mob) attacker).getSensing().hasLineOfSight(target);
			}
			return true;
		}
	}
}