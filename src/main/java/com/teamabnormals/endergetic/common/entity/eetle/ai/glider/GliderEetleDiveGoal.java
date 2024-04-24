package com.teamabnormals.endergetic.common.entity.eetle.ai.glider;

import com.teamabnormals.endergetic.common.entity.eetle.ChargerEetle;
import com.teamabnormals.endergetic.common.entity.eetle.GliderEetle;
import com.teamabnormals.endergetic.common.entity.eetle.flying.FlyingRotations;
import com.teamabnormals.endergetic.common.entity.eetle.flying.TargetFlyingRotations;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class GliderEetleDiveGoal extends Goal {
	private final GliderEetle glider;
	@Nullable
	private Vec3 divePos;
	@Nullable
	private Vec3 divingMotion;
	private float prevHealth;
	private int ticksGrabbed;
	private float targetYaw, targetPitch;
	private int ticksDiving;

	public GliderEetleDiveGoal(GliderEetle glider) {
		this.glider = glider;
		this.prevHealth = glider.getHealth();
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		GliderEetle glider = this.glider;
		LivingEntity attackTarget = glider.getTarget();
		if (attackTarget == null || !attackTarget.isAlive() || GliderEetle.isEntityLarge(attackTarget) || glider.isInWater() || !glider.isFlying() || !glider.isVehicle() || glider.getPassengers().get(0) != attackTarget || glider.getHealth() - this.prevHealth <= -3.0F) {
			this.ticksGrabbed = 0;
		} else {
			this.ticksGrabbed++;
		}
		this.prevHealth = glider.getHealth();
		if (this.ticksGrabbed >= 30) {
			Level world = glider.level();
			BlockPos pos = glider.blockPosition();
			int distanceFromGround = distanceFromGround(glider, world, pos.mutable());
			if (distanceFromGround > 3 && distanceFromGround < 11) {
				pos = pos.below(distanceFromGround);
				if (world.getEntitiesOfClass(ChargerEetle.class, new AABB(pos).inflate(4.0D)).size() < 3) {
					RandomSource random = glider.getRandom();
					for (int i = 0; i < 5; i++) {
						BlockPos offsetPos = pos.offset(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
						if (world.loadedAndEntityCanStandOn(offsetPos.below(), glider) && world.noCollision(new AABB(offsetPos))) {
							Vec3 gliderPos = new Vec3(glider.getX(), glider.getEyeY(), glider.getZ());
							if (world.clip(new ClipContext(gliderPos, Vec3.atLowerCornerOf(offsetPos), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, glider)).getType() == HitResult.Type.MISS) {
								this.divePos = Vec3.atLowerCornerOf(offsetPos);
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public void start() {
		GliderEetle glider = this.glider;
		glider.getNavigation().stop();
		Vec3 target = this.divePos;
		double xDif = (target.x() + 0.5F) - glider.getX();
		double yDif = target.y() - glider.getEyeY();
		double zDif = (target.z() + 0.5F) - glider.getZ();
		float magnitude = Mth.sqrt((float) (xDif * xDif + yDif * yDif + zDif * zDif));
		double toDeg = 180.0F / Math.PI;
		this.targetYaw = (float) (Mth.atan2(zDif, xDif) * toDeg) - 90.0F;
		this.targetPitch = (float) -(Mth.atan2(yDif, magnitude) * toDeg);
		this.divingMotion = new Vec3(xDif, yDif, zDif).normalize().multiply(1.0F, 1.3F, 1.0F);
	}

	@Override
	public boolean canContinueToUse() {
		GliderEetle glider = this.glider;
		LivingEntity attackTarget = glider.getTarget();
		return attackTarget != null && attackTarget.isAlive() && !GliderEetle.isEntityLarge(attackTarget) && glider.isFlying() && this.ticksDiving < 30;
	}

	@Override
	public void tick() {
		this.ticksDiving++;
		GliderEetle glider = this.glider;
		int ticksDiving = this.ticksDiving;
		if (ticksDiving == 5) {
			glider.setDeltaMovement(glider.getDeltaMovement().add(this.divingMotion));
		}
		glider.setDiving(true);
		glider.setMoving(true);
		glider.setTargetFlyingRotations(new TargetFlyingRotations(this.targetPitch, glider.getTargetFlyingRotations().getTargetFlyRoll()));
		glider.setYRot(FlyingRotations.clampedRotate(glider.getYRot(), this.targetYaw, 15.0F));
		glider.getLookControl().setLookAt(this.divePos);
		if (ticksDiving > 5 && (glider.onGround() || glider.horizontalCollision)) {
			LivingEntity attackTarget = glider.getTarget();
			if (attackTarget != null && glider.hasPassenger(attackTarget)) {
				glider.makeGrounded();
				glider.groundedAttacker = attackTarget;
				attackTarget.hurt(glider.damageSources().flyIntoWall(), glider.getRandom().nextInt(6) + 8);
			}
		}
	}

	@Override
	public void stop() {
		this.divePos = null;
		this.divingMotion = null;
		this.prevHealth = this.glider.getHealth();
		this.ticksGrabbed = this.ticksDiving = 0;
		this.targetPitch = this.targetYaw = 0.0F;
	}

	private static int distanceFromGround(GliderEetle glider, Level world, BlockPos.MutableBlockPos pos) {
		int y = pos.getY();
		for (int i = 0; i <= 11; i++) {
			pos.setY(y - i);
			if (world.loadedAndEntityCanStandOn(pos, glider)) {
				return i;
			}
		}
		return 11;
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}
}
