package com.teamabnormals.endergetic.common.entity.booflo;

import com.teamabnormals.blueprint.core.endimator.Endimatable;
import com.teamabnormals.blueprint.core.util.NetworkUtil;
import com.teamabnormals.endergetic.api.entity.pathfinding.EndergeticFlyingPathNavigator;
import com.teamabnormals.endergetic.api.entity.util.EntityItemStackHelper;
import com.teamabnormals.endergetic.common.entity.bolloom.BolloomFruit;
import com.teamabnormals.endergetic.common.entity.booflo.ai.AdolescentAttackGoal;
import com.teamabnormals.endergetic.common.entity.booflo.ai.AdolescentEatGoal;
import com.teamabnormals.endergetic.common.entity.booflo.ai.BoofloNearestAttackableTargetGoal;
import com.teamabnormals.endergetic.core.registry.EEEntityTypes;
import com.teamabnormals.endergetic.core.registry.EEItems;
import com.teamabnormals.endergetic.core.registry.other.EEPlayableEndimations;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * @author - SmellyModder(Luke Tonon)
 */
public class BoofloAdolescent extends PathfinderMob implements Endimatable {
	private static final EntityDataAccessor<Boolean> MOVING = SynchedEntityData.defineId(BoofloAdolescent.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> HAS_FRUIT = SynchedEntityData.defineId(BoofloAdolescent.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> DESCENTING = SynchedEntityData.defineId(BoofloAdolescent.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> EATING = SynchedEntityData.defineId(BoofloAdolescent.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> HUNGRY = SynchedEntityData.defineId(BoofloAdolescent.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> EATEN = SynchedEntityData.defineId(BoofloAdolescent.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> WANTS_TO_GROW = SynchedEntityData.defineId(BoofloAdolescent.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Float> FALL_SPEED = SynchedEntityData.defineId(BoofloAdolescent.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Integer> BOOF_BOOST_COOLDOWN = SynchedEntityData.defineId(BoofloAdolescent.class, EntityDataSerializers.INT);
	private Entity boofloAttackTarget;
	public int growingAge;
	public int forcedAge;
	public int forcedAgeTimer;
	private float prevTailAnimation;
	private float tailAnimation;
	private float tailSpeed;
	private float prevSwimmingAnimation;
	private float swimmingAnimation;
	private float swimmingAnimationSpeed;
	public boolean wasBred;

	public BoofloAdolescent(EntityType<? extends BoofloAdolescent> type, Level worldIn) {
		super(type, worldIn);
		this.moveControl = new BoofloAdolescent.BoofloAdolescentMoveController(this);
		this.tailAnimation = this.random.nextFloat();
		this.prevTailAnimation = this.tailAnimation;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(MOVING, false);
		this.entityData.define(HAS_FRUIT, false);
		this.entityData.define(DESCENTING, false);
		this.entityData.define(EATING, false);
		this.entityData.define(HUNGRY, true);
		this.entityData.define(EATEN, false);
		this.entityData.define(WANTS_TO_GROW, false);
		this.entityData.define(FALL_SPEED, 0.0F);
		this.entityData.define(BOOF_BOOST_COOLDOWN, 0);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new AdolescentEatGoal(this));
		this.goalSelector.addGoal(4, new AdolescentAttackGoal(this, 1.1D, true));
		this.goalSelector.addGoal(6, new BoofloAdolescent.RandomFlyingGoal(this, 1.1D, 5));

		this.targetSelector.addGoal(4, new BoofloNearestAttackableTargetGoal<>(this, BolloomFruit.class, true));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 10.0F)
				.add(Attributes.MOVEMENT_SPEED, 1.7F)
				.add(Attributes.FOLLOW_RANGE, 25.0F);
	}

	@Override
	public void travel(Vec3 vec3d) {
		if (this.isEffectiveAi() && !this.isInWater()) {
			this.moveRelative(0.015F, vec3d);
			this.move(MoverType.SELF, this.getDeltaMovement());
			this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
			this.setDeltaMovement(this.getDeltaMovement().subtract(0.0D, 0.005D * this.getFallSpeed(), 0.0D));
		} else {
			super.travel(vec3d);
		}
	}

	@Override
	protected PathNavigation createNavigation(Level worldIn) {
		return new EndergeticFlyingPathNavigator(this, worldIn) {

			@Override
			public boolean isStableDestination(BlockPos pos) {
				return !this.mob.onGround();
			}

		};
	}

	@Override
	protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
		return sizeIn.height * 0.65F;
	}

	@Override
	public void tick() {
		super.tick();

		if (this.isEndimationPlaying(EEPlayableEndimations.ADOLESCENT_BOOFLO_EATING) && this.getAnimationTick() == 9) {
			if (this.level() instanceof ServerLevel serverLevel) {
				serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(EEItems.BOLLOOM_FRUIT.get())), this.getX(), this.getY() + (double) this.getBbHeight() / 1.5D, this.getZ(), 10, (double) (this.getBbWidth() / 4.0F), (double) (this.getBbHeight() / 4.0F), (double) (this.getBbWidth() / 4.0F), 0.05D);
			}
			this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
		}
	}

	@Override
	public void aiStep() {
		super.aiStep();

		if (this.getVehicle() == null) this.setFallSpeed(this.getFallSpeed() + 0.1F);

		if (this.getRandom().nextInt(50000) < 10 && !this.isHungry() && !this.hasFruit()) {
			this.setHungry(true);
		}

		if (this.level().isClientSide) {
			this.prevTailAnimation = this.tailAnimation;
			this.prevSwimmingAnimation = this.swimmingAnimation;
			if (!this.isInWater()) {
				this.tailSpeed = 1.0F;
				this.swimmingAnimationSpeed = 1.0F;
			} else if (this.isMoving()) {
				if (this.tailSpeed < 0.5F) {
					this.tailSpeed = 1.0F;
				} else {
					this.tailSpeed += (0.25F - this.tailSpeed) * 0.1F;
				}
			} else {
				this.tailSpeed += (0.1875F - this.tailSpeed) * 0.1F;
			}
			if (this.getFallSpeed() > 0.0F) {
				if (this.swimmingAnimationSpeed < 0.5F) {
					this.swimmingAnimationSpeed = 1.0F;
				} else {
					this.swimmingAnimationSpeed += ((this.swimmingAnimationSpeed * 2.5F) - this.getFallSpeed()) * 0.1F;
				}
			} else {
				this.swimmingAnimationSpeed = 1.0F;
			}
			this.tailAnimation += this.tailSpeed;
			this.swimmingAnimation += this.swimmingAnimationSpeed;
		}

		if (this.getBoofBoostCooldown() > 0) {
			this.setBoofBoostCooldown(this.getBoofBoostCooldown() - 1);
		}

		if ((this.onGround() || this.isPassenger()) && this.doesWantToGrow() && this.level().noCollision(this.getBoundingBox().inflate(2.0F, 0.0F, 2.0F))) {
			this.growUp();
		}

		if (!this.level().isClientSide && ((!this.isDescenting() && !this.isEating()) && this.getBoofBoostCooldown() <= 0 && (this.onGround() || this.isEyeInFluid(FluidTags.WATER)))) {
			this.push(-Mth.sin((float) (this.getYRot() * Math.PI / 180.0F)) * (5 * (random.nextFloat() + 0.1F)) * 0.1F, (random.nextFloat() * 0.45F) + 0.65F, Mth.cos((float) (this.getYRot() * Math.PI / 180.0F)) * (5 * (random.nextFloat() + 0.1F)) * 0.1F);
			NetworkUtil.setPlayingAnimation(this, EEPlayableEndimations.ADOLESCENT_BOOFLO_BOOF);
			this.setFallSpeed(0.0F);
			//Fixes super boosting underwater
			if (this.wasEyeInWater) {
				this.setBoofBoostCooldown(20);
			}
		}

		//Helps them not fall off the edge
		if ((this.getBoofBoostCooldown() <= 0 && !this.onGround()) && this.level().dimension() == Level.END && !this.isSafePos(this.blockPosition(), 3)) {
			this.setBoofBoostCooldown(20);
			this.setFallSpeed(0.0F);

			if (this.getY() <= 50) {
				this.push(-Mth.sin((float) (this.getYRot() * Math.PI / 180.0F)) * (5 * (random.nextFloat() + 0.1F)) * 0.1F, (random.nextFloat() * 0.45F) + 0.65F, Mth.cos((float) (this.getYRot() * Math.PI / 180.0F)) * (5 * (random.nextFloat() + 0.1F)) * 0.1F);
				this.setPlayingEndimation(EEPlayableEndimations.ADOLESCENT_BOOFLO_BOOF);
			}
		}

		if (!this.onGround() && this.level().dimension() == Level.END && !this.isSafePos(this.blockPosition(), 3) && !this.level().isClientSide) {
			this.push(-Mth.sin((float) (this.getYRot() * Math.PI / 180.0F)) * 0.01F, 0, Mth.cos((float) (this.getYRot() * Math.PI / 180.0F)) * 0.01F);
		}

		if (this.getBoofloAttackTarget() != null && this.hasLineOfSight(this.getBoofloAttackTarget())) {
			this.setYRot(this.yHeadRot);
		}

		if (this.level().isClientSide) {
			if (this.forcedAgeTimer > 0) {
				if (this.forcedAgeTimer % 4 == 0) {
					this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getX() + (this.random.nextFloat() * this.getBbWidth() * 2.0F) - this.getBbWidth(), this.getY() + 0.5D + (this.random.nextFloat() * this.getBbHeight()), this.getZ() + (this.random.nextFloat() * this.getBbWidth() * 2.0F) - this.getBbWidth(), 0.0D, 0.0D, 0.0D);
				}

				this.forcedAgeTimer--;
			}
		} else if (this.isAlive() && this.hasEaten()) {
			int growingAge = this.getGrowingAge();
			if (growingAge < 0) {
				growingAge++;
				this.setGrowingAge(growingAge);
			} else if (growingAge > 0) {
				growingAge--;
				this.setGrowingAge(growingAge);
			}
		}
	}

	@Override
	public ItemStack getPickedResult(HitResult target) {
		return new ItemStack(EEItems.BOOFLO_SPAWN_EGG.get());
	}

	@Override
	public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
		return false;
	}

	@Override
	protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
	}

	@Override
	public void die(DamageSource cause) {
		if (this.hasFruit() && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
			this.dropFruit();
		}
		super.die(cause);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("Moving", this.isMoving());
		compound.putBoolean("HasFruit", this.hasFruit());
		compound.putBoolean("Descenting", this.isDescenting());
		compound.putBoolean("IsEating", this.isEating());
		compound.putBoolean("IsHungry", this.isHungry());
		compound.putBoolean("HasEaten", this.hasEaten());
		compound.putBoolean("WantsToGrow", this.doesWantToGrow());
		compound.putFloat("FallSpeed", this.getFallSpeed());
		compound.putInt("BoofBoostCooldown", this.getBoofBoostCooldown());
		compound.putInt("Age", this.getGrowingAge());
		compound.putInt("ForcedAge", this.forcedAge);
		compound.putBoolean("WasBred", this.wasBred);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.setMoving(compound.getBoolean("Moving"));
		this.setHasFruit(compound.getBoolean("HasFruit"));
		this.setDescenting(compound.getBoolean("Descenting"));
		this.setEating(compound.getBoolean("IsEating"));
		this.setHungry(compound.getBoolean("IsHungry"));
		this.setEaten(compound.getBoolean("HasEaten"));
		this.setWantsToGrow(compound.getBoolean("WantsToGrow"));
		this.setFallSpeed(compound.getFloat("FallSpeed"));
		this.setBoofBoostCooldown(compound.getInt("BoofBoostCooldown"));
		this.setGrowingAge(compound.getInt("Age"));
		this.forcedAge = compound.getInt("ForcedAge");
		this.wasBred = compound.getBoolean("WasBred");
	}

	public boolean isMoving() {
		return this.entityData.get(MOVING);
	}

	public void setMoving(boolean moving) {
		this.entityData.set(MOVING, moving);
	}

	public boolean hasFruit() {
		return this.entityData.get(HAS_FRUIT);
	}

	public void setHasFruit(boolean hasFruit) {
		this.entityData.set(HAS_FRUIT, hasFruit);
	}

	public boolean isDescenting() {
		return this.entityData.get(DESCENTING);
	}

	public void setDescenting(boolean descenting) {
		this.entityData.set(DESCENTING, descenting);
	}

	public boolean isEating() {
		return this.entityData.get(EATING);
	}

	public void setEating(boolean eating) {
		this.entityData.set(EATING, eating);
	}

	public boolean isHungry() {
		return this.entityData.get(HUNGRY);
	}

	public void setHungry(boolean hungry) {
		this.entityData.set(HUNGRY, hungry);
	}

	public boolean hasEaten() {
		return this.entityData.get(EATEN);
	}

	public void setEaten(boolean eaten) {
		this.entityData.set(EATEN, eaten);
	}

	public boolean doesWantToGrow() {
		return this.entityData.get(WANTS_TO_GROW);
	}

	public void setWantsToGrow(boolean wantsToGrow) {
		this.entityData.set(WANTS_TO_GROW, wantsToGrow);
	}

	public float getFallSpeed() {
		return this.entityData.get(FALL_SPEED);
	}

	public void setFallSpeed(float speed) {
		this.entityData.set(FALL_SPEED, speed);
	}

	public int getBoofBoostCooldown() {
		return this.entityData.get(BOOF_BOOST_COOLDOWN);
	}

	public void setBoofBoostCooldown(int ticks) {
		this.entityData.set(BOOF_BOOST_COOLDOWN, ticks);
	}

	@Nullable
	public Entity getBoofloAttackTarget() {
		return this.boofloAttackTarget;
	}

	public void setBoofloAttackTarget(@Nullable Entity entity) {
		this.boofloAttackTarget = entity;
	}

	public int getGrowingAge() {
		if (this.level().isClientSide) {
			return -1;
		} else {
			return this.growingAge;
		}
	}

	public void setGrowingAge(int age) {
		int oldAge = this.growingAge;
		this.growingAge = age;
		if ((oldAge < 0 && age >= 0 || oldAge > 0 && age < 0) && this.hasEaten()) {
			this.setWantsToGrow(true);
		}
	}

	public void addGrowth(int growth) {
		this.ageUp(growth, false);
	}

	public void ageUp(int growthSeconds, boolean updateForcedAge) {
		int growingAge = this.getGrowingAge();
		int j = growingAge;
		growingAge += growthSeconds * 20;
		if (growingAge > 0) {
			growingAge = 0;
		}

		int k = growingAge - j;

		this.setGrowingAge(growingAge);
		if (updateForcedAge) {
			this.forcedAge += k;
			if (this.forcedAgeTimer == 0) {
				this.forcedAgeTimer = 40;
			}
		}

		if (this.getGrowingAge() == 0) {
			this.setGrowingAge(this.forcedAge);
		}
	}

	public LivingEntity growUp() {
		if (this.isAlive()) {
			this.spawnAtLocation(EEItems.BOOFLO_HIDE.get(), 1);

			Booflo booflo = EEEntityTypes.BOOFLO.get().create(this.level());
			booflo.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());

			if (this.hasCustomName()) {
				booflo.setCustomName(this.getCustomName());
				booflo.setCustomNameVisible(this.isCustomNameVisible());
			}

			if (this.isLeashed()) {
				booflo.setLeashedTo(this.getLeashHolder(), true);
				this.dropLeash(true, false);
			}

			if (this.getVehicle() != null) {
				booflo.startRiding(this.getVehicle());
			}

			booflo.wasBred = this.wasBred;
			booflo.setHealth(booflo.getMaxHealth());
			this.level().addFreshEntity(booflo);
			this.discard();

			return booflo;
		}
		return this;
	}

	public LivingEntity growDown() {
		if (this.isAlive()) {
			BoofloBaby boofloBaby = EEEntityTypes.BOOFLO_BABY.get().create(this.level());
			boofloBaby.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());

			if (this.hasCustomName()) {
				boofloBaby.setCustomName(this.getCustomName());
				boofloBaby.setCustomNameVisible(this.isCustomNameVisible());
			}

			if (this.isLeashed()) {
				boofloBaby.setLeashedTo(this.getLeashHolder(), true);
				this.dropLeash(true, false);
			}

			if (this.getVehicle() != null) {
				boofloBaby.startRiding(this.getVehicle());
			}

			boofloBaby.wasBred = this.wasBred;
			boofloBaby.setHealth(boofloBaby.getMaxHealth());
			this.level().addFreshEntity(boofloBaby);
			this.discard();

			return boofloBaby;
		}
		return this;
	}

	@OnlyIn(Dist.CLIENT)
	public float getTailAnimation(float ptc) {
		return Mth.lerp(ptc, this.prevTailAnimation, this.tailAnimation);
	}

	@OnlyIn(Dist.CLIENT)
	public float getSwimmingAnimation(float ptc) {
		return Mth.lerp(ptc, this.prevSwimmingAnimation, this.swimmingAnimation);
	}

	public boolean isSafePos(BlockPos pos, int muliplier) {
		BlockPos newPos = pos;
		for (int y = 0; y < 10 * muliplier; y++) {
			newPos = newPos.below(y);
			BlockState state = this.level().getBlockState(newPos);
			if (state.canOcclude() || (!state.getFluidState().isEmpty() && !state.getFluidState().is(FluidTags.LAVA))) {
				return true;
			}
		}
		return false;
	}

	public void dropFruit() {
		if (!this.level().isClientSide) {
			this.spawnAtLocation(EEItems.BOLLOOM_FRUIT.get());
			this.setHasFruit(false);
		}
	}

	public boolean isPlayerNear() {
		return this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(2.0F), Booflo.IS_SCARED_BY).size() > 0;
	}

	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		return source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.FLY_INTO_WALL) || super.isInvulnerableTo(source);
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		Item item = itemstack.getItem();

		if (item instanceof SpawnEggItem && ((SpawnEggItem) item).spawnsEntity(itemstack.getTag(), EEEntityTypes.BOOFLO.get())) {
			if (!this.level().isClientSide) {
				BoofloBaby baby = EEEntityTypes.BOOFLO_BABY.get().create(this.level());
				baby.setGrowingAge(-24000);
				baby.moveTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
				this.level().addFreshEntity(baby);
				if (itemstack.hasCustomHoverName()) {
					baby.setCustomName(itemstack.getHoverName());
				}

				EntityItemStackHelper.consumeItemFromStack(player, itemstack);
			}
			return InteractionResult.PASS;
		} else if (item == EEItems.BOLLOOM_FRUIT.get()) {
			EntityItemStackHelper.consumeItemFromStack(player, itemstack);
			this.ageUp((int) ((-this.getGrowingAge() / 20) * 0.1F), true);
			this.setEaten(true);
			return InteractionResult.sidedSuccess(this.level().isClientSide);
		}
		return InteractionResult.PASS;
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
		this.setGrowingAge(-24000);
		return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return !this.wasBred;
	}

	static class RandomFlyingGoal extends RandomStrollGoal {

		public RandomFlyingGoal(PathfinderMob booflo, double speed, int chance) {
			super(booflo, speed, chance);
		}

		@Nullable
		protected Vec3 getPosition() {
			Vec3 view = this.mob.getViewVector(0.0F);
			double viewX = view.x;
			double viewZ = view.z;
			Vec3 vec3d = HoverRandomPos.getPos(this.mob, 10, 0, viewX, viewZ, ((float) Math.PI / 2F), 3, 1);

			for (int i = 0; vec3d != null && !this.mob.level().getBlockState(BlockPos.containing(vec3d)).isPathfindable(this.mob.level(), BlockPos.containing(vec3d), PathComputationType.AIR) && i++ < 10; vec3d = HoverRandomPos.getPos(this.mob, 10, 0, viewX, viewZ, ((float) Math.PI / 2F), 3, 1)) {
				;
			}

			return vec3d;
		}

		@Override
		public boolean canUse() {
			return super.canUse() && !this.mob.isInWater();
		}

		@Override
		public boolean canContinueToUse() {
			return super.canContinueToUse() && !this.mob.isInWater();
		}

	}

	static class BoofloAdolescentMoveController extends MoveControl {
		private final BoofloAdolescent booflo;

		BoofloAdolescentMoveController(BoofloAdolescent booflo) {
			super(booflo);
			this.booflo = booflo;
		}

		public void tick() {
			if (this.operation == MoveControl.Operation.MOVE_TO && !this.booflo.getNavigation().isDone()) {
				Position pos = this.booflo.getPos();
				Vec3 vec3d = new Vec3(this.wantedX - pos.x(), this.wantedY - pos.y(), this.wantedZ - pos.z());
				this.booflo.setYRot(this.rotlerp(this.booflo.getYRot(), (float) ((Mth.atan2(vec3d.z, vec3d.x) * (double) (180F / (float) Math.PI)) - 90F), 10.0F));
				this.booflo.yBodyRot = this.booflo.getYRot();
				this.booflo.yHeadRot = this.booflo.getYRot();

				float f1 = (float) (2 * this.booflo.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
				float f2 = Mth.lerp(0.125F, this.booflo.getSpeed(), f1);

				this.booflo.setSpeed(f2);

				this.booflo.setMoving(true);
			} else {
				this.booflo.setMoving(false);
			}
		}
	}
}
