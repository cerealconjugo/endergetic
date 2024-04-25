package com.teamabnormals.endergetic.common.entity.puffbug.ai;

import com.teamabnormals.blueprint.core.util.MathUtil;
import com.teamabnormals.blueprint.core.util.NetworkUtil;
import com.teamabnormals.endergetic.common.entity.puffbug.PuffBug;
import com.teamabnormals.endergetic.core.registry.other.EEPlayableEndimations;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class PuffBugRotateToFireGoal extends Goal {
	private final PuffBug puffbug;
	private final RandomSource random;
	private int ticksPassed;
	private int ticksToRotate;

	public PuffBugRotateToFireGoal(PuffBug puffbug) {
		this.puffbug = puffbug;
		this.random = puffbug.getRandom();
		this.setFlags(EnumSet.of(Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		return !this.puffbug.isPassenger() && !this.puffbug.isEndimationPlaying(EEPlayableEndimations.PUFF_BUG_TELEPORT_FROM) && this.puffbug.isInflated() && this.puffbug.getLaunchDirection() != null;
	}

	@Override
	public boolean canContinueToUse() {
		return this.canUse() && this.ticksPassed <= this.ticksToRotate;
	}

	@Override
	public void start() {
		this.ticksToRotate = this.random.nextInt(5) + 12;
	}

	@Override
	public void stop() {
		if (this.puffbug.getLaunchDirection() != null && this.ticksPassed >= this.ticksToRotate) {
			Vec3 launch = this.puffbug.getLaunchDirection();

			float yaw = (float) launch.y() - this.puffbug.getYRot();
			float pitch = Mth.degreesDifference((float) launch.x(), 0.0F);

			float x = -Mth.sin(yaw * ((float) Math.PI / 180F)) * Mth.cos(pitch * ((float) Math.PI / 180F));
			float y = -Mth.sin(pitch * ((float) Math.PI / 180F));
			float z = Mth.cos(yaw * ((float) Math.PI / 180F)) * Mth.cos(pitch * ((float) Math.PI / 180F));

			Vec3 motion = new Vec3(x, -y, z).normalize();

			this.puffbug.setDeltaMovement(this.puffbug.getDeltaMovement().add(motion.scale(1.0F)));

			this.puffbug.setFireDirection((float) launch.x(), (float) launch.y());
			this.puffbug.removeLaunchDirection();
			this.puffbug.setInflated(false);
			NetworkUtil.setPlayingAnimation(this.puffbug, EEPlayableEndimations.PUFF_BUG_FLY);

			Vec3 pos = this.puffbug.position();
			double posX = pos.x();
			double posY = pos.y();
			double posZ = pos.z();
			for (int i = 0; i < 3; i++) {
				float particleX = Mth.sin(yaw * ((float) Math.PI / 180F)) * Mth.cos(pitch * ((float) Math.PI / 180F));
				float particleY = -Mth.sin(pitch * ((float) Math.PI / 180F));
				float particleZ = -Mth.cos(yaw * ((float) Math.PI / 180F)) * Mth.cos(pitch * ((float) Math.PI / 180F));

				Vec3 particleMotion = new Vec3(particleX, particleY, particleZ).normalize().scale(0.5F);

				NetworkUtil.spawnParticle("endergetic:short_poise_bubble", posX, posY, posZ, particleMotion.x() + MathUtil.makeNegativeRandomly((this.random.nextFloat() * 0.25F), this.random), particleMotion.y() + (this.random.nextFloat() * 0.05F), MathUtil.makeNegativeRandomly(particleMotion.z() + (this.random.nextFloat() * 0.25F), this.random));
			}
		}
		this.ticksPassed = 0;
		this.ticksToRotate = 0;
	}

	@Override
	public void tick() {
		this.ticksPassed++;

		this.puffbug.getNavigation().stop();

		Vec3 launchDirection = this.puffbug.getLaunchDirection();

		this.puffbug.getRotationController().rotate((float) Mth.wrapDegrees(launchDirection.y() - this.puffbug.getY()), (float) launchDirection.x() + 90.0F, 0.0F, 10);
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}
}