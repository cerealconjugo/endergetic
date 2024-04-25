package com.teamabnormals.endergetic.common.entity.booflo;

import com.teamabnormals.blueprint.core.endimator.Endimatable;
import com.teamabnormals.blueprint.core.endimator.PlayableEndimation;
import com.teamabnormals.blueprint.core.endimator.TimedEndimation;
import com.teamabnormals.blueprint.core.util.MathUtil;
import com.teamabnormals.blueprint.core.util.NetworkUtil;
import com.teamabnormals.endergetic.api.entity.pathfinding.EndergeticFlyingPathNavigator;
import com.teamabnormals.endergetic.api.entity.util.DetectionHelper;
import com.teamabnormals.endergetic.api.entity.util.EntityItemStackHelper;
import com.teamabnormals.endergetic.api.entity.util.RayTraceHelper;
import com.teamabnormals.endergetic.common.advancement.EECriteriaTriggers;
import com.teamabnormals.endergetic.common.entity.bolloom.BolloomFruit;
import com.teamabnormals.endergetic.common.entity.booflo.ai.*;
import com.teamabnormals.endergetic.common.entity.puffbug.PuffBug;
import com.teamabnormals.endergetic.core.other.EEPlayableEndimations;
import com.teamabnormals.endergetic.core.registry.*;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class Booflo extends PathfinderMob implements Endimatable {
	public static final Predicate<Entity> IS_SCARED_BY = (entity) -> {
		if (entity instanceof Player) {
			return !entity.isSpectator() && !((Player) entity).isCreative();
		}
		return false;
	};
	private static final int BOOST_POWER_INCREMENT = 10;
	private static final int MAX_BOOST_POWER = 182;
	private static final int HALF_BOOST_POWER = 91;
	private static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(Booflo.class, EntityDataSerializers.OPTIONAL_UUID);
	private static final EntityDataAccessor<Optional<UUID>> LAST_FED_UNIQUE_ID = SynchedEntityData.defineId(Booflo.class, EntityDataSerializers.OPTIONAL_UUID);
	private static final EntityDataAccessor<Boolean> ON_GROUND = SynchedEntityData.defineId(Booflo.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> TAMED = SynchedEntityData.defineId(Booflo.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> MOVING_IN_AIR = SynchedEntityData.defineId(Booflo.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> BOOFED = SynchedEntityData.defineId(Booflo.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> HUNGRY = SynchedEntityData.defineId(Booflo.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> HAS_FRUIT = SynchedEntityData.defineId(Booflo.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> FRUITS_NEEDED = SynchedEntityData.defineId(Booflo.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Byte> BOOST_STATUS = SynchedEntityData.defineId(Booflo.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Integer> BOOST_POWER = SynchedEntityData.defineId(Booflo.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> LOVE_TICKS = SynchedEntityData.defineId(Booflo.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> ATTACK_TARGET = SynchedEntityData.defineId(Booflo.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> BRACELETS_COLOR = SynchedEntityData.defineId(Booflo.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Float> LOCKED_YAW = SynchedEntityData.defineId(Booflo.class, EntityDataSerializers.FLOAT);

	private static final EntityDimensions BOOFED_SIZE = EntityDimensions.fixed(2.0F, 1.5F);
	public final TimedEndimation OPEN_JAW = new TimedEndimation(25, 0);

	private final EndergeticFlyingPathNavigator attackingNavigator;
	private UUID playerInLove;
	public int hopDelay;
	public int breedDelay;
	private int croakDelay;
	private int deflateDelay;
	public int babiesToBirth;
	public boolean wasBred;
	private boolean shouldPlayLandSound;
	private boolean wasOnGround;

	public Booflo(EntityType<? extends Booflo> type, Level world) {
		super(type, world);
		this.attackingNavigator = new EndergeticFlyingPathNavigator(this, this.level());
		this.moveControl = new GroundMoveHelperController(this);
		this.hopDelay = this.getDefaultGroundHopDelay();
		this.setMaxUpStep(1.0F);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(OWNER_UNIQUE_ID, Optional.empty());
		this.entityData.define(LAST_FED_UNIQUE_ID, Optional.empty());
		this.entityData.define(ON_GROUND, false);
		this.entityData.define(TAMED, false);
		this.entityData.define(MOVING_IN_AIR, false);
		this.entityData.define(BOOFED, false);
		this.entityData.define(HUNGRY, this.getRandom().nextFloat() < 0.6F);
		this.entityData.define(HAS_FRUIT, false);
		this.entityData.define(BOOST_STATUS, (byte) 0);
		this.entityData.define(BOOST_POWER, 0);
		this.entityData.define(FRUITS_NEEDED, this.getRandom().nextInt(3) + 2);
		this.entityData.define(LOVE_TICKS, 0);
		this.entityData.define(ATTACK_TARGET, 0);
		this.entityData.define(BRACELETS_COLOR, DyeColor.YELLOW.getId());
		this.entityData.define(LOCKED_YAW, 0.0F);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new BoofloGiveBirthGoal(this));
		this.goalSelector.addGoal(0, new BoofloEatPuffBugGoal(this));
		this.goalSelector.addGoal(1, new BoofloBoofGoal(this));
		this.goalSelector.addGoal(1, new BoofloSlamGoal(this));
		this.goalSelector.addGoal(1, new BoofloBreedGoal(this));
		this.goalSelector.addGoal(2, new BoofloEatFruitGoal(this));
		this.goalSelector.addGoal(3, new BoofloSinkGoal(this));
		this.goalSelector.addGoal(5, new BoofloTemptGoal(this));
		this.goalSelector.addGoal(6, new BoofloHuntPuffBugGoal(this));
		this.goalSelector.addGoal(6, new BoofloAttackGoal(this));
		this.goalSelector.addGoal(7, new BoofloHuntFruitGoal(this, 1.0F));
		this.goalSelector.addGoal(8, new BoofloSwimGoal(this, 1.0F, 15));
		this.goalSelector.addGoal(9, new BoofloFaceRandomGoal(this));
		this.goalSelector.addGoal(10, new BoofloGroundHopGoal(this));

		this.targetSelector.addGoal(1, new BoofloNearestAttackableTargetGoal<>(this, PuffBug.class, 175, true, false));
		this.targetSelector.addGoal(2, new BoofloNearestAttackableTargetGoal<>(this, BolloomFruit.class, true));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes().add(Attributes.ATTACK_DAMAGE, 7.0F).add(Attributes.MAX_HEALTH, 40.0F).add(Attributes.MOVEMENT_SPEED, 1.05F).add(Attributes.ARMOR, 4.0F).add(Attributes.FOLLOW_RANGE, 22.0F).add(Attributes.KNOCKBACK_RESISTANCE, 0.6F);
	}

	@Override
	public void tick() {
		super.tick();

		if (this.breedDelay > 0) this.breedDelay--;
		if (this.deflateDelay > 0) this.deflateDelay--;
		if (this.croakDelay > 0) this.croakDelay--;

		if (this.isBoofed()) {
			if (this.hasAggressiveAttackTarget()) {
				this.navigation = this.attackingNavigator;
			} else {
				if (this.navigation instanceof EndergeticFlyingPathNavigator) {
					this.navigation = new FlyingPathNavigation(this, this.level()) {

						@Override
						public boolean isStableDestination(BlockPos pos) {
							return this.level.isEmptyBlock(pos);
						}

					};
				}

				if (this.getBoofloAttackTarget() == null && this.isPathFinding() && this.getDeltaMovement().length() < 0.25F && RayTraceHelper.rayTrace(this, 2.0D, 1.0F).getType() == Type.BLOCK) {
					this.getNavigation().stop();
				}
			}
		}

		if (!this.level().isClientSide) {
			if (this.isEndimationPlaying(EEPlayableEndimations.BOOFLO_CHARGE) && this.getAnimationTick() >= 15) {
				this.push(0.0F, -0.225F, 0.0F);
			}

			this.setOnGround(!this.level().noCollision(DetectionHelper.checkOnGround(this.getBoundingBox(), 0.07F)));

			int power = this.getBoostPower();
			if (power > 0 && !this.isBoostExpanding()) {
				this.setBoostPower(Math.max(0, power - (this.onGround() ? 3 : 2)));
				if (this.getBoostPower() <= 0) {
					this.setBoostLocked(false);
				}
			} else if (this.isBoostExpanding()) {
				if (power < MAX_BOOST_POWER) {
					if (this.isBoostLocked()) {
						int incremented = power + BOOST_POWER_INCREMENT;
						this.setBoostPower(Math.min(HALF_BOOST_POWER, incremented));
						if (incremented >= HALF_BOOST_POWER) {
							this.setBoostExpanding(false);
						}
					} else {
						this.setBoostPower(Math.min(MAX_BOOST_POWER, power + BOOST_POWER_INCREMENT));
					}
				} else {
					if (!this.isBoostLocked() && this.getControllingPassenger() instanceof Player) {
						NetworkUtil.setPlayingAnimation(this, EEPlayableEndimations.BOOFLO_INFLATE);
						this.playSound(this.getInflateSound(), 0.75F, 1.0F);
					}
					this.setBoostExpanding(false);
				}
			}

			if (this.onGround() && !this.isBoofed() && this.isBoostExpanding()) {
				this.setBoostExpanding(false);
				this.setBoostLocked(false);
			}

			/*
			 * Resends data to clients
			 */
			if (this.isBoofed() && !this.onGround()) {
				this.setBoofed(true);
			}

			if (this.isBoofed() && this.isNoEndimationPlaying() && this.isMovingInAir()) {
				if (RayTraceHelper.rayTrace(this, 2.0D, 1.0F).getType() != Type.BLOCK) {
					NetworkUtil.setPlayingAnimation(this, EEPlayableEndimations.BOOFLO_SWIM);
				}
			}

			if (this.isEndimationPlaying(EEPlayableEndimations.BOOFLO_SWIM) && this.getAnimationTick() <= 15) {
				this.setMovingInAir(true);
			}

			if (this.isEndimationPlaying(EEPlayableEndimations.BOOFLO_EAT)) {
				if ((this.getAnimationTick() > 20 && this.getAnimationTick() <= 140)) {
					if (this.getAnimationTick() % 20 == 0) {
						if (this.level() instanceof ServerLevel && this.hasCaughtFruit()) {
							((ServerLevel) this.level()).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(EEItems.BOLLOOM_FRUIT.get())), this.getX(), this.getY() + (double) this.getBbHeight() / 1.5D, this.getZ(), 10, (double) (this.getBbWidth() / 4.0F), (double) (this.getBbHeight() / 4.0F), (double) (this.getBbWidth() / 4.0F), 0.05D);
						}

						if (this.hasCaughtPuffBug()) {
							this.getPassengers().get(0).hurt(this.level().damageSources().mobAttack(this), 0.0F);
						}

						this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
					}
					if (this.getAnimationTick() == 140) {
						this.setCaughtFruit(false);
						this.heal(5.0F);

						if (this.hasCaughtPuffBug()) {
							this.playSound(SoundEvents.PLAYER_BURP, 1.0F, 0.75F);
							this.getPassengers().get(0).discard();
						}
					}
				}
			}

			if (this.isEndimationPlaying(EEPlayableEndimations.BOOFLO_HOP)) {
				if (this.getAnimationTick() == 10) {
					this.playSound(this.getHopSound(false), 0.95F, this.getVoicePitch());
					this.shouldPlayLandSound = true;
				}
			}

			if (this.shouldPlayLandSound && this.onGround() && !this.wasOnGround) {
				this.playSound(this.getHopSound(true), 0.95F, this.getVoicePitch());
				this.shouldPlayLandSound = false;
			}
		}

		if (this.isEndimationPlaying(EEPlayableEndimations.BOOFLO_INFLATE) && this.getAnimationTick() == 2) {
			this.boof(1.0F, 1.0F, false);
		}

		if (!this.level().isClientSide && this.isEndimationPlaying(EEPlayableEndimations.BOOFLO_GROWL)) {
			if (this.getAnimationTick() == 10) {
				this.playSound(this.getGrowlSound(), 0.75F, this.getVoicePitch());
			}

			if (this.getAnimationTick() >= 20) {
				for (Player players : this.getNearbyPlayers(0.4F)) {
					if (!this.hasAggressiveAttackTarget()) {
						this.setBoofloAttackTargetId(players.getId());
					}
				}
			}
		}

		if (this.isEndimationPlaying(EEPlayableEndimations.BOOFLO_SLAM) && this.getAnimationTick() == 3) {
			this.boof(1.2F, 2.2F, true);
			this.playSound(this.getSlamSound(), 0.75F, 1.0F);
		}

		if (this.isInWater()) {
			if (!this.isBoofed()) {
				this.setBoofed(true);
			} else if (this.random.nextFloat() < 0.7F) {
				this.push(0.0F, 0.05F, 0.0F);
			}
		}

		if (this.onGround() && this.isBoofed()) {
			if (this.hasAggressiveAttackTarget() && !this.hasCaughtPuffBug()) {
				if (!this.level().isClientSide) {
					if (this.isNoEndimationPlaying()) {
						NetworkUtil.setPlayingAnimation(this, EEPlayableEndimations.BOOFLO_INFLATE);
					} else if (this.isEndimationPlaying(EEPlayableEndimations.BOOFLO_CHARGE)) {
						NetworkUtil.setPlayingAnimation(this, EEPlayableEndimations.BOOFLO_SLAM);
					}
				}
			} else {
				if (this.isVehicle() && this.isEndimationPlaying(EEPlayableEndimations.BOOFLO_CHARGE)) {
					NetworkUtil.setPlayingAnimation(this, EEPlayableEndimations.BOOFLO_SLAM);
				} else {
					if (this.deflateDelay <= 0 && (!this.isEndimationPlaying(EEPlayableEndimations.BOOFLO_SLAM) && !this.isInWater())) {
						this.setBoofed(false);
					}
				}
			}
		}

		if (this.getRandom().nextInt(40000) < 10 && !this.hasCaughtFruit() && !this.hasCaughtPuffBug()) {
			this.setHungry(true);
		}

		if (this.level().isClientSide) {
			if (this.isBoofed()) {
				this.OPEN_JAW.setDecrementing(this.getBoofloAttackTarget() == null || this.hasCaughtPuffBug() || (this.hasAggressiveAttackTarget() && !(this.getBoofloAttackTarget() instanceof PuffBug)));

				this.OPEN_JAW.tick();
			}
		}

		this.wasOnGround = this.onGround();

		if (this.isEndimationPlaying(EEPlayableEndimations.BOOFLO_EAT) && !this.hasCaughtFruit()) {
			this.setYRot(this.yHeadRot = this.yBodyRot = this.getLockedYaw());
		}
	}

	@Override
	public void aiStep() {
		super.aiStep();

		if (this.hopDelay > 0) this.hopDelay--;

		if (this.getInLoveTicks() > 0) {
			this.setInLove(this.getInLoveTicks() - 1);
			if (this.getInLoveTicks() % 10 == 0) {
				double d0 = this.random.nextGaussian() * 0.02D;
				double d1 = this.random.nextGaussian() * 0.02D;
				double d2 = this.random.nextGaussian() * 0.02D;
				this.level().addParticle(ParticleTypes.HEART, this.getX() + (this.random.nextFloat() * this.getBbWidth() * 2.0F) - this.getBbWidth(), this.getY() + 0.5D + (this.random.nextFloat() * this.getBbHeight()), this.getZ() + (this.random.nextFloat() * this.getBbWidth() * 2.0F) - this.getBbWidth(), d0, d1, d2);
			}
		}

		if (!this.level().isClientSide && this.croakDelay == 0 && !this.isTempted() && this.isAlive() && this.onGround() && !this.isBoofed() && this.random.nextInt(1000) < this.ambientSoundTime++ && this.isNoEndimationPlaying() && this.getPassengers().isEmpty()) {
			this.ambientSoundTime = -this.getAmbientSoundInterval();
			NetworkUtil.setPlayingAnimation(this, EEPlayableEndimations.BOOFLO_CROAK);
		}

		if (this.isEndimationPlaying(EEPlayableEndimations.BOOFLO_CROAK) && this.getAnimationTick() == 5 && !this.level().isClientSide) {
			this.playSound(this.getAmbientSound(), 1.25F, this.getVoicePitch());
		}

		if (this.hasAggressiveAttackTarget()) {
			this.setYRot(this.yHeadRot);
			Entity attackTarget = this.getBoofloAttackTarget();
			if (!this.level().isClientSide && (this.distanceToSqr(attackTarget) > 1152.0D || attackTarget.isInvisible() || (attackTarget instanceof PuffBug && attackTarget.isPassenger()))) {
				this.setBoofloAttackTargetId(0);
			}
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("IsMovingInAir", this.isMovingInAir());
		compound.putBoolean("IsBoofed", this.isBoofed());
		compound.putBoolean("IsHungry", this.isHungry());
		compound.putBoolean("HasFruit", this.hasCaughtFruit());
		compound.putBoolean("WasBred", this.wasBred);
		compound.putInt("FruitsNeededTillTamed", this.getFruitsNeededTillTamed());
		compound.putInt("InLove", this.getInLoveTicks());
		compound.putInt("BoofloTargetId", this.getBoofloAttackTargetId());
		compound.putInt("BabiesToBirth", this.babiesToBirth);
		compound.putByte("BraceletsColor", (byte) this.getBraceletsColor().getId());
		compound.putFloat("BirthYaw", this.getLockedYaw());

		if (this.playerInLove != null) {
			compound.putUUID("LoveCause", this.playerInLove);
		}

		if (this.getOwnerId() != null) {
			compound.putString("Owner", this.getOwnerId().toString());
		}

		if (this.getLastFedId() != null) {
			compound.putString("LastFed", this.getLastFedId().toString());
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.setMovingInAir(compound.getBoolean("IsMovingInAir"));
		this.setBoofed(compound.getBoolean("IsBoofed"));
		this.setHungry(compound.getBoolean("IsHungry"));
		this.setCaughtFruit(compound.getBoolean("HasFruit"));
		this.setInLove(compound.getInt("InLove"));
		this.setBoofloAttackTargetId(compound.getInt("BoofloTargetId"));
		this.babiesToBirth = compound.getInt("BabiesToBirth");
		this.setLockedYaw(compound.getFloat("BirthYaw"));
		this.playerInLove = compound.hasUUID("LoveCause") ? compound.getUUID("LoveCause") : null;
		this.wasBred = compound.getBoolean("WasBred");

		if (compound.contains("BraceletsColor", 99)) {
			this.setBraceletsColor(DyeColor.byId(compound.getInt("BraceletsColor")));
		}

		if (compound.contains("FruitsNeededTillTamed")) {
			this.setFruitsNeeded(compound.getInt("FruitsNeededTillTamed"));
		}

		UUID ownerUUID = compound.hasUUID("Owner") ? compound.getUUID("Owner") : OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), compound.getString("Owner"));
		UUID lastFedUUID = compound.hasUUID("LastFed") ? compound.getUUID("LastFed") : OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), compound.getString("LastFed"));

		if (ownerUUID != null) {
			try {
				this.setOwnerId(ownerUUID);
				this.setTamed(true);
			} catch (Throwable throwable) {
				this.setTamed(false);
			}
		}

		if (lastFedUUID != null) {
			try {
				this.setLastFedId(lastFedUUID);
			} catch (Throwable exception) {
			}
		}
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		if (BOOFED.equals(key)) {
			this.refreshDimensions();
			if (this.isBoofed()) {
				this.navigation = new FlyingPathNavigation(this, this.level()) {

					@Override
					public boolean isStableDestination(BlockPos pos) {
						return this.level.isEmptyBlock(pos);
					}

				};
				this.moveControl = new FlyingMoveController(this);
				this.lookControl = new FlyingLookController(this, 10);

				if (!this.level().isClientSide && this.tickCount > 5) {
					this.playSound(this.getInflateSound(), this.getSoundVolume(), this.getVoicePitch());
				}

				this.deflateDelay = 10;
			} else {
				this.navigation = this.createNavigation(this.level());
				this.moveControl = new GroundMoveHelperController(this);
				this.lookControl = new LookControl(this);

				if (!this.level().isClientSide && this.tickCount > 5) {
					this.playSound(this.getDeflateSound(), this.getSoundVolume(), this.getVoicePitch());
				}

				if (this.level().isClientSide) {
					this.OPEN_JAW.setTick(0);
					this.setBoofloAttackTargetId(0);
				}
			}
		}
	}


	@Override
	public void tickRidden(Player rider, Vec3 vec3d) {
		this.setYRot(rider.getYRot());
		this.yRotO = this.getYRot();
		this.setXRot(0.0F);
		this.setRot(this.getYRot(), this.getXRot());
		this.yBodyRot = this.getYRot();
		this.yHeadRot = this.getYRot();

		float playerMoveFoward = rider.zza;
		if (!this.level().isClientSide() && playerMoveFoward > 0.0F) {
			if (this.onGround() && this.isNoEndimationPlaying() && !this.isBoofed()) {
				NetworkUtil.setPlayingAnimation(this, EEPlayableEndimations.BOOFLO_HOP);
			} else if (!this.onGround() && this.isNoEndimationPlaying() && this.isBoofed()) {
				NetworkUtil.setPlayingAnimation(this, EEPlayableEndimations.BOOFLO_SWIM);
			}
		}

		if (this.isBoofed()) {
			float gravity = this.getBoostPower() > 0 ? 0.01F : 0.035F;

			if (this.isPathFinding()) {
				this.getNavigation().stop();
			}

			if (this.getBoofloAttackTarget() != null) {
				this.setBoofloAttackTargetId(0);
			}

			this.move(MoverType.SELF, this.getDeltaMovement());
			this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
			if (!this.isInWater()) {
				this.setDeltaMovement(this.getDeltaMovement().subtract(0, gravity, 0));
			}
		} else {
			if (this.onGround() && this.isEndimationPlaying(EEPlayableEndimations.BOOFLO_HOP) && this.getAnimationTick() == 10) {
				Vec3 motion = this.getDeltaMovement();
				MobEffectInstance jumpBoost = this.getEffect(MobEffects.JUMP);
				float boostPower = jumpBoost == null ? 1.0F : (float) (jumpBoost.getAmplifier() + 1);

				this.setDeltaMovement(motion.x, 0.55F * boostPower, motion.z);
				this.hasImpulse = true;

				float xMotion = -Mth.sin(this.getYRot() * ((float) Math.PI / 180F));
				float zMotion = Mth.cos(this.getYRot() * ((float) Math.PI / 180F));

				float multiplier = 0.35F + (float) this.getAttribute(Attributes.MOVEMENT_SPEED).getValue();

				this.setDeltaMovement(this.getDeltaMovement().add(xMotion * multiplier, 0.0F, zMotion * multiplier));
			}

			if (this.isControlledByLocalInstance()) {
				super.tickRidden(rider, new Vec3(0.0F, vec3d.y, 0.0F));
			} else {
				this.setDeltaMovement(Vec3.ZERO);
			}
		}
	}

	@Override
	public void travel(Vec3 vec3d) {
		if (this.isEffectiveAi() && this.isBoofed()) {
			this.moveRelative(0.0F, vec3d);
			this.move(MoverType.SELF, this.getDeltaMovement());
			this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
			if (!this.isMovingInAir()) {
				this.setDeltaMovement(this.getDeltaMovement().subtract(0, 0.01D, 0));
			}
		} else {
			super.travel(vec3d);
		}
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
		if (reason == MobSpawnType.NATURAL) {
			if (worldIn.getRandom().nextFloat() < 0.2F) {
				this.babiesToBirth = 3;
			}
			this.setFruitsNeeded(worldIn.getRandom().nextInt(3) + 2);
		}
		return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	@Override
	protected void removePassenger(Entity passenger) {
		if (!this.level().isClientSide && this.isBoostExpanding() && !this.isBoostLocked() && passenger instanceof Player && this.getControllingPassenger() == passenger) {
			this.setBoostExpanding(false);
		}
		super.removePassenger(passenger);
	}

	@Nullable
	public UUID getOwnerId() {
		return this.entityData.get(OWNER_UNIQUE_ID).orElse(null);
	}

	public void setOwnerId(@Nullable UUID ownerId) {
		this.entityData.set(OWNER_UNIQUE_ID, Optional.ofNullable(ownerId));
	}

	@Nullable
	public UUID getLastFedId() {
		return this.entityData.get(LAST_FED_UNIQUE_ID).orElse(null);
	}

	public void setLastFedId(@Nullable UUID ownerId) {
		this.entityData.set(LAST_FED_UNIQUE_ID, Optional.ofNullable(ownerId));
	}

	/*
	 * Minecraft's onGround boolean isn't synced correctly so this has its own
	 */
	public boolean onGround() {
		return this.entityData.get(ON_GROUND);
	}

	public void setOnGround(boolean onGround) {
		this.entityData.set(ON_GROUND, onGround);
	}

	public boolean isTamed() {
		return this.entityData.get(TAMED);
	}

	public void setTamed(boolean tamed) {
		this.entityData.set(TAMED, tamed);
	}

	public boolean isMovingInAir() {
		return this.entityData.get(MOVING_IN_AIR);
	}

	public void setMovingInAir(boolean moving) {
		this.entityData.set(MOVING_IN_AIR, moving);
	}

	public boolean isBoofed() {
		return this.entityData.get(BOOFED);
	}

	public void setBoofed(boolean boofed) {
		this.entityData.set(BOOFED, boofed);
		this.shouldPlayLandSound = false;
	}

	public boolean isPregnant() {
		return this.babiesToBirth > 0;
	}

	public boolean isHungry() {
		return this.entityData.get(HUNGRY);
	}

	public void setHungry(boolean hungry) {
		this.entityData.set(HUNGRY, hungry);
	}

	public boolean hasCaughtFruit() {
		return this.entityData.get(HAS_FRUIT);
	}

	public boolean hasCaughtPuffBug() {
		return !this.getPassengers().isEmpty() && this.getPassengers().get(0) instanceof PuffBug;
	}

	public void setCaughtFruit(boolean hasCaughtFruit) {
		this.entityData.set(HAS_FRUIT, hasCaughtFruit);
	}

	public void setBoostStatus(int status, boolean add) {
		this.entityData.set(BOOST_STATUS, (byte) (add ? this.entityData.get(BOOST_STATUS) | status : this.entityData.get(BOOST_STATUS) & ~status));
	}

	public void setBoostExpanding(boolean expanding) {
		this.setBoostStatus(1, expanding);
	}

	public boolean isBoostExpanding() {
		return (this.entityData.get(BOOST_STATUS) & 1) != 0;
	}

	public void setBoostLocked(boolean expandingDelay) {
		this.setBoostStatus(2, expandingDelay);
	}

	public boolean isBoostLocked() {
		return (this.entityData.get(BOOST_STATUS) & 2) != 0;
	}

	public float getLockedYaw() {
		return this.entityData.get(LOCKED_YAW);
	}

	public void setLockedYaw(float yaw) {
		this.entityData.set(LOCKED_YAW, yaw);
	}

	public int getBoofloAttackTargetId() {
		return this.entityData.get(ATTACK_TARGET);
	}

	@Nullable
	public Entity getBoofloAttackTarget() {
		Entity entity = this.level().getEntity(this.getBoofloAttackTargetId());
		if (entity == null || entity != null && !entity.isAlive() || entity instanceof Booflo) {
			this.setBoofloAttackTargetId(0);
		}

		if (this.getOwner() != null && this.getOwner() == entity) {
			this.setBoofloAttackTargetId(0);
		}
		return this.getBoofloAttackTargetId() > 0 ? entity : null;
	}

	public boolean hasAggressiveAttackTarget() {
		return this.getBoofloAttackTarget() instanceof LivingEntity;
	}

	public void setBoofloAttackTargetId(int id) {
		this.entityData.set(ATTACK_TARGET, id);
	}

	public void setInLove(@Nullable Player player) {
		this.setInLove(600);
		if (player != null) {
			this.playerInLove = player.getUUID();
		}

		this.level().broadcastEntityEvent(this, (byte) 18);
	}

	public void setFruitsNeeded(int fruitsNeeded) {
		this.entityData.set(FRUITS_NEEDED, fruitsNeeded);
	}

	public int getFruitsNeededTillTamed() {
		return this.entityData.get(FRUITS_NEEDED);
	}

	public void setBoostPower(int power) {
		this.entityData.set(BOOST_POWER, power);
	}

	public int getBoostPower() {
		return this.entityData.get(BOOST_POWER);
	}

	public DyeColor getBraceletsColor() {
		return DyeColor.byId(this.entityData.get(BRACELETS_COLOR));
	}

	public void setBraceletsColor(DyeColor color) {
		this.entityData.set(BRACELETS_COLOR, color.getId());
	}

	public void setInLove(int ticks) {
		this.entityData.set(LOVE_TICKS, ticks);
	}

	public int getInLoveTicks() {
		return this.entityData.get(LOVE_TICKS);
	}

	public boolean canBreed() {
		return this.isTamed() && this.getInLoveTicks() <= 0 && !this.isPregnant() && this.breedDelay <= 0;
	}

	public boolean isInLove() {
		if (this.isPregnant()) {
			return false;
		}
		return this.getInLoveTicks() > 0;
	}

	public void resetInLove() {
		this.setInLove(0);
	}

	@Nullable
	public ServerPlayer getLoveCause() {
		if (this.playerInLove == null) {
			return null;
		} else {
			Player playerentity = this.level().getPlayerByUUID(this.playerInLove);
			return playerentity instanceof ServerPlayer ? (ServerPlayer) playerentity : null;
		}
	}

	public void setTamedBy(Player player) {
		this.setTamed(true);
		this.setOwnerId(player.getUUID());
		if (player instanceof ServerPlayer serverPlayer) {
			//Creates wolf to still trigger tamed - as booflo isn't an AnimalEntity
			CriteriaTriggers.TAME_ANIMAL.trigger(serverPlayer, EntityType.WOLF.create(this.level()));
			if (!this.level().isClientSide) {
				EECriteriaTriggers.TAME_BOOFLO.trigger(serverPlayer);
			}
		}
	}

	@Nullable
	public LivingEntity getOwner() {
		try {
			UUID uuid = this.getOwnerId();
			return uuid == null ? null : this.level().getPlayerByUUID(uuid);
		} catch (IllegalArgumentException exception) {
			return null;
		}
	}

	@Nullable
	public LivingEntity getLastFedPlayer() {
		try {
			UUID uuid = this.getLastFedId();
			return uuid == null ? null : this.level().getPlayerByUUID(uuid);
		} catch (IllegalArgumentException exception) {
			return null;
		}
	}

	public boolean canMateWith(Booflo possibleMate) {
		if (possibleMate == this) {
			return false;
		} else {
			return this.isInLove() && possibleMate.isInLove();
		}
	}

	public int getDefaultGroundHopDelay() {
		return this.isInLove() ? this.random.nextInt(10) + 25 : this.random.nextInt(40) + 80;
	}

	public void boof(float internalStrength, float offensiveStrength, boolean slam) {
		float verticalStrength = 1.0F;

		if (this.isVehicle() && this.getControllingPassenger() instanceof Player && !this.isEndimationPlaying(EEPlayableEndimations.BOOFLO_SLAM) && !this.isBoostLocked()) {
			float boostPower = Mth.clamp(this.getBoostPower() * 0.01F, 0.35F, 1.82F);
			offensiveStrength *= Mth.clamp(boostPower / 2, 0.5F, 1.85F);
			verticalStrength *= Mth.clamp(boostPower, 0.35F, 1.5F);

			float xMotion = -Mth.sin(this.getYRot() * ((float) Math.PI / 180F)) * Mth.cos(this.getXRot() * ((float) Math.PI / 180F));
			float zMotion = Mth.cos(this.getYRot() * ((float) Math.PI / 180F)) * Mth.cos(this.getXRot() * ((float) Math.PI / 180F));
			Vec3 boostFowardForce = new Vec3(xMotion, 1.3F * verticalStrength, zMotion).normalize().scale(boostPower > 0.35 ? boostPower * 2.0F : boostPower);

			this.setDeltaMovement(boostFowardForce.x(), 1.3F * verticalStrength, boostFowardForce.z());
		} else {
			this.push(-Mth.sin((float) (this.getYRot() * Math.PI / 180.0F)) * ((4 * internalStrength) * (this.random.nextFloat() + 0.1F)) * 0.1F, 1.3F * verticalStrength, Mth.cos((float) (this.getYRot() * Math.PI / 180.0F)) * ((4 * internalStrength) * (this.random.nextFloat() + 0.1F)) * 0.1F);
		}

		if (slam) {
			for (int i = 0; i < 12; i++) {
				double offsetX = MathUtil.makeNegativeRandomly(this.random.nextFloat() * 0.25F, this.random);
				double offsetZ = MathUtil.makeNegativeRandomly(this.random.nextFloat() * 0.25F, this.random);

				double x = this.getX() + 0.5D + offsetX;
				double y = this.getY() + 0.5D + (this.random.nextFloat() * 0.05F);
				double z = this.getZ() + 0.5D + offsetZ;

				if (this.level().isClientSide) {
					this.level().addParticle(EEParticleTypes.POISE_BUBBLE.get(), x, y, z, MathUtil.makeNegativeRandomly((this.random.nextFloat() * 0.3F), this.random) + 0.025F, (this.random.nextFloat() * 0.15F) + 0.1F, MathUtil.makeNegativeRandomly((this.random.nextFloat() * 0.3F), this.random) + 0.025F);
				}
			}
		}

		for (Entity entity : this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(3.5F * Math.max(offensiveStrength / 2.0F, 1.0F)), entity -> entity != this && (entity instanceof ItemEntity || entity instanceof LivingEntity) && !(entity instanceof Player && ((Player) entity).isCreative() && ((Player) entity).getAbilities().flying))) {
			float resistance = this.isResistantToBoof(entity) ? 0.15F : 1.0F;
			float amount = (0.2F * offensiveStrength) * resistance;
			if (offensiveStrength > 2.0F && resistance > 0.15F && entity != this.getControllingPassenger()) {
				entity.hurt(this.level().damageSources().mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
				entity.hurtMarked = false;
			}
			Vec3 result = entity.position().subtract(this.position());
			entity.push(result.x * amount, (this.random.nextFloat() * 0.75D + 0.25D) * (offensiveStrength * 0.75F), result.z * amount);
		}
	}

	public LivingEntity growDown() {
		if (this.isAlive()) {
			BoofloAdolescent boofloAdolescent = EEEntityTypes.BOOFLO_ADOLESCENT.get().create(this.level());
			boofloAdolescent.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());

			if (this.hasCustomName()) {
				boofloAdolescent.setCustomName(this.getCustomName());
				boofloAdolescent.setCustomNameVisible(this.isCustomNameVisible());
			}

			if (this.isLeashed()) {
				boofloAdolescent.setLeashedTo(this.getLeashHolder(), true);
				this.dropLeash(true, false);
			}

			if (this.getVehicle() != null) {
				boofloAdolescent.startRiding(this.getVehicle());
			}

			boofloAdolescent.wasBred = this.wasBred;
			boofloAdolescent.setHealth(boofloAdolescent.getMaxHealth());
			this.level().addFreshEntity(boofloAdolescent);
			this.discard();
			return boofloAdolescent;
		}
		return this;
	}

	public void catchPuffBug(PuffBug puffbug) {
		puffbug.startRiding(this, true);
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return !this.isTamed() && !this.wasBred && !this.isPregnant();
	}

	public boolean isTempted() {
		for (Object goals : this.goalSelector.getRunningGoals().toArray()) {
			if (goals instanceof WrappedGoal) {
				return ((WrappedGoal) goals).getGoal() instanceof BoofloTemptGoal;
			}
		}
		return false;
	}

	public List<Player> getNearbyPlayers(float multiplier) {
		return this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(8.0F * multiplier, 4.0F, 8.0F * multiplier), IS_SCARED_BY);
	}

	public boolean isPlayerNear(float multiplier) {
		return !this.getNearbyPlayers(multiplier).isEmpty();
	}

	@Override
	protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
		return this.isBoofed() ? 1.2F : 0.9F;
	}

	@Override
	public int getAmbientSoundInterval() {
		return 120;
	}

	@Override
	public void onEndimationStart(PlayableEndimation endimation, PlayableEndimation oldEndimation) {
		if (endimation == EEPlayableEndimations.BOOFLO_SWIM) {
			float pitch = this.isVehicle() ? 1.0F : this.getXRot();
			float xMotion = -Mth.sin(this.getYRot() * ((float) Math.PI / 180F)) * Mth.cos(pitch * ((float) Math.PI / 180F));
			float yMotion = -Mth.sin(pitch * ((float) Math.PI / 180F));
			float zMotion = Mth.cos(this.getYRot() * ((float) Math.PI / 180F)) * Mth.cos(pitch * ((float) Math.PI / 180F));

			double motionScale = (this.hasAggressiveAttackTarget() && !this.hasCaughtPuffBug()) || (!this.getPassengers().isEmpty() && !this.hasCaughtPuffBug()) ? 0.85F : 0.5F;

			Vec3 motion = new Vec3(xMotion, yMotion, zMotion).normalize().multiply(motionScale, 0.5D, motionScale);

			this.push(motion.x * (this.getAttribute(Attributes.MOVEMENT_SPEED).getValue() - 0.05F), motion.y, motion.z * (this.getAttribute(Attributes.MOVEMENT_SPEED).getValue() - 0.05F));
		}
	}

	@Override
	protected void jumpFromGround() {
		Vec3 vec3d = this.getDeltaMovement();
		this.setDeltaMovement(vec3d.x, 0.55D, vec3d.z);
		this.hasImpulse = true;
	}

	@Override
	protected void doPush(Entity entity) {
		if (entity instanceof BoofloBaby && (((BoofloBaby) (entity)).isBeingBorn() || ((BoofloBaby) (entity)).getMotherNoClipTicks() > 0)) return;
		super.doPush(entity);
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		Item item = itemstack.getItem();

		if (item instanceof SpawnEggItem && ((SpawnEggItem) item).spawnsEntity(itemstack.getTag(), this.getType())) {
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
			return InteractionResult.sidedSuccess(this.level().isClientSide);
		} else if (item == EEBlocks.POISE_CLUSTER.get().asItem() && this.canBreed()) {
			EntityItemStackHelper.consumeItemFromStack(player, itemstack);
			this.setInLove(player);
			return InteractionResult.sidedSuccess(this.level().isClientSide);
		} else if (item == EEItems.BOLLOOM_FRUIT.get() && !this.isAggressive() && !this.hasCaughtFruit() && this.onGround()) {
			ParticleOptions particle = ParticleTypes.HEART;
			EntityItemStackHelper.consumeItemFromStack(player, itemstack);
			this.setCaughtFruit(true);
			this.setHungry(false);

			if (!this.isTamed()) {
				if (this.getFruitsNeededTillTamed() >= 1) {
					this.setFruitsNeeded(this.getFruitsNeededTillTamed() - 1);
					this.setLastFedId(player.getUUID());
					particle = ParticleTypes.SMOKE;

					if (!this.level().isClientSide) {
						NetworkUtil.setPlayingAnimation(this, EEPlayableEndimations.BOOFLO_GROWL);
					}
				} else {
					if (player == this.getLastFedPlayer()) {
						this.setFruitsNeeded(0);
						this.setTamedBy(player);
						this.croakDelay = 40;
					}
				}
			}

			if (this.level().isClientSide) {
				for (int i = 0; i < 7; ++i) {
					double d0 = this.random.nextGaussian() * 0.02D;
					double d1 = this.random.nextGaussian() * 0.02D;
					double d2 = this.random.nextGaussian() * 0.02D;
					this.level().addParticle(particle, this.getX() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), this.getY() + 0.5D + (double) (this.random.nextFloat() * this.getBbHeight()), this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), d0, d1, d2);
				}
			}
			return InteractionResult.sidedSuccess(this.level().isClientSide);
		} else if (item instanceof DyeItem && this.isTamed()) {
			DyeColor dyecolor = ((DyeItem) item).getDyeColor();
			if (dyecolor != this.getBraceletsColor()) {
				this.setBraceletsColor(dyecolor);
				if (!player.getAbilities().instabuild) {
					itemstack.shrink(1);
				}
				return InteractionResult.sidedSuccess(this.level().isClientSide);
			}
		} else {
			InteractionResult result = itemstack.interactLivingEntity(player, this, hand);
			if (result == InteractionResult.CONSUME || result == InteractionResult.SUCCESS) {
				return InteractionResult.PASS;
			}

			if (this.isTamed() && !this.isVehicle() && !this.isPregnant()) {
				if (!this.level().isClientSide) {
					player.setYRot(this.getYRot());
					player.setXRot(this.getXRot());
					player.startRiding(this);
				}
				return InteractionResult.PASS;
			}
		}
		return super.mobInteract(player, hand);
	}

	@Override
	public void positionRider(Entity passenger, Entity.MoveFunction func) {
		if (this.hasPassenger(passenger)) {
			if (passenger instanceof BoofloBaby boofloBaby) {
				Vec3 ridingOffset = boofloBaby.getBirthPositionOffset().yRot(-this.getYRot() * ((float) Math.PI / 180F) - ((float) Math.PI / 2F));
				func.accept(passenger, this.getX() + ridingOffset.x, this.getY() + 0.9F, this.getZ() + ridingOffset.z);
			} else if (passenger instanceof PuffBug puffbug) {
				passenger.setYRot(puffbug.yBodyRot = puffbug.yHeadRot = (this.getYRot() - 75.0F));
				if (this.isEndimationPlaying(EEPlayableEndimations.BOOFLO_EAT) && this.getAnimationTick() > 15) {
					Vec3 ridingPos = (new Vec3(1.0D, 0.0D, 0.0D)).yRot(-this.getYRot() * ((float) Math.PI / 180F) - ((float) Math.PI / 2F));
					float yOffset = puffbug.isBaby() ? 0.1F : 0.3F;

					func.accept(passenger, this.getX() + ridingPos.x(), this.getY() - yOffset - (0.15F * getEatingOffsetProgress(this.getAnimationTick())), this.getZ() + ridingPos.z());
				} else {
					func.accept(passenger, this.getX(), this.getY() + 0.25F, this.getZ());
				}
			} else {
				super.positionRider(passenger, func);
				if (passenger instanceof Mob mob) {
					this.yBodyRot = mob.yBodyRot;
				}
			}
		}
	}

	@Override
	public double getPassengersRidingOffset() {
		double original = super.getPassengersRidingOffset();
		return this.isBoofed() ? original + 0.15F : original;
	}

	@Override
	public boolean onClimbable() {
		return false;
	}

	@Override
	public boolean isPushable() {
		return !this.isVehicle();
	}

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		int limit = this.isPregnant() ? 3 : 1;
		return this.getPassengers().size() < limit;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		Entity entitySource = source.getEntity();
		if (entitySource instanceof LivingEntity && !this.isVehicle()) {
			if (entitySource instanceof Player) {
				if (!entitySource.isSpectator() && !((Player) entitySource).isCreative()) {
					this.setBoofloAttackTargetId(entitySource.getId());
				}
			} else {
				this.setBoofloAttackTargetId(entitySource.getId());
			}
		}
		float newCalculatedDamage = source.is(DamageTypes.IN_WALL) ? 0.5F : amount;
		if (super.hurt(source, source.getEntity() instanceof PuffBug ? 2.5F : newCalculatedDamage)) {
			if (this.isNoEndimationPlaying()) NetworkUtil.setPlayingAnimation(this, EEPlayableEndimations.BOOFLO_HURT);
			return true;
		}
		return false;
	}

	@Override
	protected void actuallyHurt(DamageSource damageSrc, float damageAmount) {
		Entity entitySource = damageSrc.getEntity();
		if (entitySource instanceof LivingEntity && !this.isVehicle()) {
			if (entitySource instanceof Player) {
				if (!entitySource.isSpectator() && !((Player) entitySource).isCreative()) {
					this.setBoofloAttackTargetId(entitySource.getId());
				}
			} else {
				this.setBoofloAttackTargetId(entitySource.getId());
			}
		}
		super.actuallyHurt(damageSrc, damageAmount);
	}

	@Override
	public int getMaxHeadYRot() {
		return 1;
	}

	@Override
	public int getMaxSpawnClusterSize() {
		return 3;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return this.isBoofed() ? BOOFED_SIZE : super.getDimensions(poseIn);
	}

	@Override
	@Nullable
	public LivingEntity getControllingPassenger() {
		Entity entity = this.getFirstPassenger();
		if (entity instanceof Mob mob) {
			return mob;
		} else {
			entity = this.getFirstPassenger();
			if (entity instanceof Player player) {
				return player;
			}

			return null;
		}
	}

	protected boolean isResistantToBoof(Entity entity) {
		return entity instanceof Booflo || entity instanceof BoofloAdolescent || entity instanceof BoofloBaby;
	}

	@Override
	public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void handleEntityEvent(byte id) {
		if (id == 18) {
			for (int i = 0; i < 7; ++i) {
				double d0 = this.random.nextGaussian() * 0.02D;
				double d1 = this.random.nextGaussian() * 0.02D;
				double d2 = this.random.nextGaussian() * 0.02D;
				this.level().addParticle(ParticleTypes.HEART, this.getX() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), this.getY() + 0.5D + (double) (this.random.nextFloat() * this.getBbHeight()), this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), d0, d1, d2);
			}
		} else {
			super.handleEntityEvent(id);
		}
	}

	/*
	 * Overridden to do nothing; gets remade in this class
	 * @see EntityBooflo#livingTick
	 */
	@Override
	public void playAmbientSound() {
	}

	public SoundEvent getHopSound(boolean landing) {
		return landing ? EESoundEvents.BOOFLO_HOP_LAND.get() : EESoundEvents.BOOFLO_HOP.get();
	}

	public SoundEvent getGrowlSound() {
		return EESoundEvents.BOOFLO_GROWL.get();
	}

	public SoundEvent getSlamSound() {
		return EESoundEvents.BOOFLO_SLAM.get();
	}

	public SoundEvent getInflateSound() {
		return EESoundEvents.BOOFLO_INFLATE.get();
	}

	protected SoundEvent getDeflateSound() {
		return EESoundEvents.BOOFLO_DEFLATE.get();
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return EESoundEvents.BOOFLO_CROAK.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return EESoundEvents.BOOFLO_DEATH.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return EESoundEvents.BOOFLO_HURT.get();
	}

	@Override
	public ItemStack getPickedResult(HitResult target) {
		return new ItemStack(EEItems.BOOFLO_SPAWN_EGG.get());
	}

	public static float getEatingOffsetProgress(float ticks) {
		return Mth.abs(2.0F * Mth.frac(ticks / 20.0F) - 1.0F);
	}

	public static class GroundMoveHelperController extends MoveControl {
		private final Booflo booflo;
		private float yRot;
		public boolean isAggressive;

		public GroundMoveHelperController(Booflo booflo) {
			super(booflo);
			this.booflo = booflo;
			this.yRot = (float) (180.0F * booflo.getYRot() / Math.PI);
		}

		public void setDirection(float yRot, boolean aggressive) {
			this.yRot = yRot;
			this.isAggressive = aggressive;
		}

		public void setSpeed(double speed) {
			this.speedModifier = speed;
			this.operation = MoveControl.Operation.MOVE_TO;
		}

		public void tick() {
			if (!this.booflo.hasCaughtPuffBug()) {
				this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.yRot, 90.0F));
				this.mob.yHeadRot = this.mob.getYRot();
				this.mob.yBodyRot = this.mob.getYRot();
			}

			if (this.operation != MoveControl.Operation.MOVE_TO) {
				this.mob.setZza(0.0F);
			} else {
				this.operation = MoveControl.Operation.WAIT;
				if (this.mob.onGround()) {
					this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttribute(Attributes.MOVEMENT_SPEED).getValue()));
					if (this.booflo.hopDelay == 0 && this.booflo.isEndimationPlaying(EEPlayableEndimations.BOOFLO_HOP) && this.booflo.getAnimationTick() == 10) {
						this.booflo.getJumpControl().jump();

						this.booflo.hopDelay = this.booflo.getDefaultGroundHopDelay();
					} else {
						this.booflo.xxa = 0.0F;
						this.booflo.zza = 0.0F;
						this.mob.setSpeed(0.0F);
					}
				} else {
					this.mob.setSpeed(0.0F);
				}
			}
		}
	}

	public static class FlyingMoveController extends MoveControl {
		private final Booflo booflo;

		public FlyingMoveController(Booflo booflo) {
			super(booflo);
			this.booflo = booflo;
		}

		public void tick() {
			if (this.operation == MoveControl.Operation.MOVE_TO && !this.booflo.getNavigation().isDone()) {
				if (this.booflo.hasAggressiveAttackTarget()) {
					Position pos = this.booflo.getPos();
					Vec3 vec3d = new Vec3(this.wantedX - pos.x(), this.wantedY - pos.y(), this.wantedZ - pos.z());

					this.booflo.setYRot(this.rotlerp(this.booflo.getYRot(), (float) ((Mth.atan2(vec3d.z, vec3d.x) * (double) (180F / (float) Math.PI)) - 90F), 10.0F));
					this.booflo.yBodyRot = this.booflo.getYRot();
					this.booflo.yHeadRot = this.booflo.getYRot();

					float f1 = (float) (2 * this.booflo.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
					float f2 = Mth.lerp(0.125F, this.booflo.getSpeed(), f1);

					this.booflo.setSpeed(f2);
				} else {
					Vec3 vec3d = new Vec3(this.wantedX - this.booflo.getX(), this.wantedY - this.booflo.getY(), this.wantedZ - this.booflo.getZ());
					double d0 = vec3d.length();
					double d1 = vec3d.y / d0;
					float f = (float) (Mth.atan2(vec3d.z, vec3d.x) * (double) (180F / (float) Math.PI)) - 90F;

					this.booflo.setYRot(this.rotlerp(this.booflo.getYRot(), f, 10.0F));
					this.booflo.yBodyRot = this.booflo.getYRot();
					this.booflo.yHeadRot = this.booflo.getYRot();

					float f1 = (float) (this.speedModifier * this.booflo.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
					float f2 = Mth.lerp(0.125F, this.booflo.getSpeed(), f1);

					this.booflo.setSpeed(f2);

					double d3 = Math.cos(this.booflo.getYRot() * ((float) Math.PI / 180F));
					double d4 = Math.sin(this.booflo.getYRot() * ((float) Math.PI / 180F));
					double d5 = Math.sin((double) (this.booflo.tickCount + this.booflo.getId()) * 0.75D) * 0.05D;

					if (!this.booflo.isInWater()) {
						float f3 = -((float) (Mth.atan2(vec3d.y, Mth.sqrt((float) (vec3d.x * vec3d.x + vec3d.z * vec3d.z))) * (double) (180F / (float) Math.PI)));
						f3 = Mth.clamp(Mth.wrapDegrees(f3), -85.0F, 85.0F);
						this.booflo.setXRot(this.rotlerp(this.booflo.getXRot(), f3, 5.0F));
					}

					this.booflo.setDeltaMovement(this.booflo.getDeltaMovement().add(0, d5 * (d4 + d3) * 0.25D + (double) f2 * d1 * 0.02D, 0));
				}
				this.booflo.setMovingInAir(true);
			} else {
				this.booflo.setSpeed(0F);
				this.booflo.setMovingInAir(false);
			}
		}
	}

	static class FlyingLookController extends LookControl {
		private final int angleLimit;

		public FlyingLookController(Booflo booflo, int angleLimit) {
			super(booflo);
			this.angleLimit = angleLimit;
		}

		public void tick() {
			if (this.lookAtCooldown > 0) {
				--this.lookAtCooldown;
				this.getYRotD().ifPresent(angle -> {
					this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, angle + 20.0F, this.yMaxRotSpeed);
				});
				this.getXRotD().ifPresent(angle -> {
					this.mob.setXRot(this.rotateTowards(this.mob.getXRot(), angle + 10.0F, this.xMaxRotAngle));
				});
			} else {
				if (this.mob.getNavigation().isDone()) {
					this.mob.setXRot(this.rotateTowards(this.mob.getXRot(), 0.0F, 5.0F));
				}
				this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, this.mob.yBodyRot, this.yMaxRotSpeed);
			}

			float wrappedDegrees = Mth.wrapDegrees(this.mob.yHeadRot - this.mob.yBodyRot);
			if (wrappedDegrees < (float) (-this.angleLimit)) {
				this.mob.yBodyRot -= 4.0F;
			} else if (wrappedDegrees > (float) this.angleLimit) {
				this.mob.yBodyRot += 4.0F;
			}

			if (((Booflo) this.mob).isEndimationPlaying(EEPlayableEndimations.BOOFLO_CHARGE)) {
				this.mob.setXRot(this.rotateTowards(this.mob.getXRot(), 0.0F, 10.0F));
			}
		}
	}
}
