package com.teamabnormals.endergetic.common.entity;

import com.teamabnormals.blueprint.core.util.MathUtil;
import com.teamabnormals.blueprint.core.util.NetworkUtil;
import com.teamabnormals.endergetic.core.registry.EEBlocks;
import com.teamabnormals.endergetic.core.registry.EEEntityTypes;
import com.teamabnormals.endergetic.core.registry.EESoundEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class PoiseClusterEntity extends LivingEntity {
	private static final int MAX_BLOCKS_TO_MOVE_UP = 30;
	private static final EntityDataAccessor<BlockPos> ORIGIN = SynchedEntityData.defineId(PoiseClusterEntity.class, EntityDataSerializers.BLOCK_POS);
	private static final EntityDataAccessor<Integer> BLOCKS_TO_MOVE_UP = SynchedEntityData.defineId(PoiseClusterEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> TIMES_HIT = SynchedEntityData.defineId(PoiseClusterEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> ASCEND = SynchedEntityData.defineId(PoiseClusterEntity.class, EntityDataSerializers.BOOLEAN);
	private boolean playedSound;

	public PoiseClusterEntity(EntityType<? extends PoiseClusterEntity> cluster, Level worldIn) {
		super(EEEntityTypes.POISE_CLUSTER.get(), worldIn);
	}

	public PoiseClusterEntity(Level worldIn, BlockPos origin, double x, double y, double z) {
		this(EEEntityTypes.POISE_CLUSTER.get(), worldIn);
		this.setHealth(100);
		this.setOrigin(new BlockPos(origin));
		this.setPos(x + 0.5D, y, z + 0.5D);
		this.setNoGravity(true);
		this.yBodyRotO = 180.0F;
		this.yBodyRot = 180.0F;
		this.setYRot(180.0F);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ORIGIN, BlockPos.ZERO);
		this.entityData.define(BLOCKS_TO_MOVE_UP, 0);
		this.entityData.define(TIMES_HIT, 0);
		this.entityData.define(ASCEND, true);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putLong("OriginPos", this.getOrigin().asLong());
		nbt.putInt("BlocksToMoveUp", this.getBlocksToMoveUp());
		nbt.putInt("TimesHit", this.getTimesHit());
		nbt.putBoolean("IsAscending", this.isAscending());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		this.setOrigin(BlockPos.of(nbt.getLong("OriginPos")));
		this.setBlocksToMoveUp(nbt.getInt("BlocksToMoveUp"));
		this.setTimesHit(nbt.getInt("TimesHit"));
		this.setAscending(nbt.getBoolean("IsAscending"));
	}

	@Override
	public void tick() {
		super.tick();
		if (this.isAscending()) {
			this.moveEntitiesUp();
		}

		this.yBodyRot = this.yBodyRotO = 180.0F;
		this.setYRot(this.yRotO = 180.0F);

		if (this.getY() + 1.0F < (this.getOrigin().getY() + this.getBlocksToMoveUp()) && this.isAscending()) {
			this.setDeltaMovement(0.0F, 0.05F, 0.0F);
		}

		if (this.getY() + 1.0F >= this.getOrigin().getY() + this.getBlocksToMoveUp()) {
			if (!this.level().isClientSide) {
				this.setAscending(false);
			}
			this.setBlocksToMoveUp(0);
		}

		if (!this.isAscending()) {
			if (this.getY() > this.getOrigin().getY()) {
				this.setDeltaMovement(0, -0.05F, 0);
			} else if (Math.ceil(this.getY()) == this.getOrigin().getY() && this.tickCount > 10) {
				for (int i = 0; i < 8; i++) {
					double offsetX = MathUtil.makeNegativeRandomly(this.random.nextFloat() * 0.25F, this.random);
					double offsetZ = MathUtil.makeNegativeRandomly(this.random.nextFloat() * 0.25F, this.random);

					double x = this.getOrigin().getX() + 0.5D + offsetX;
					double y = this.getOrigin().getY() + 0.5D + (this.random.nextFloat() * 0.05F);
					double z = this.getOrigin().getZ() + 0.5D + offsetZ;

					if (this.isEffectiveAi()) {
						NetworkUtil.spawnParticle("endergetic:short_poise_bubble", x, y, z, MathUtil.makeNegativeRandomly((random.nextFloat() * 0.1F), random) + 0.025F, (random.nextFloat() * 0.15F) + 0.1F, MathUtil.makeNegativeRandomly((random.nextFloat() * 0.1F), random) + 0.025F);
					}
				}
				this.level().setBlockAndUpdate(this.getOrigin(), EEBlocks.POISE_CLUSTER.get().defaultBlockState());
				this.discard();
			}

			if (this.isBlockBlockingPath(true) && this.tickCount > 10) {
				BlockPos pos = this.blockPosition();

				for (int i = 0; i < 8; i++) {
					double offsetX = MathUtil.makeNegativeRandomly(this.random.nextFloat() * 0.25F, this.random);
					double offsetZ = MathUtil.makeNegativeRandomly(this.random.nextFloat() * 0.25F, this.random);

					double x = pos.getX() + 0.5D + offsetX;
					double y = pos.getY() + 0.5D + (this.random.nextFloat() * 0.05F);
					double z = pos.getZ() + 0.5D + offsetZ;

					if (this.isEffectiveAi()) {
						NetworkUtil.spawnParticle("endergetic:short_poise_bubble", x, y, z, MathUtil.makeNegativeRandomly((this.random.nextFloat() * 0.1F), this.random) + 0.025F, (this.random.nextFloat() * 0.15F) + 0.1F, MathUtil.makeNegativeRandomly((this.random.nextFloat() * 0.1F), this.random) + 0.025F);
					}
				}

				this.level().setBlockAndUpdate(pos, EEBlocks.POISE_CLUSTER.get().defaultBlockState());
				this.discard();
			}
		}

		/*
		 * Used to make it try to move down if  there is another entity of itself above it
		 */
		AABB bb = this.getBoundingBox().move(0, 1, 0);
		List<Entity> entities = this.getCommandSenderWorld().getEntitiesOfClass(Entity.class, bb);
		int entityCount = entities.size();
		boolean hasEntity = entityCount > 0;
		if (hasEntity && this.isAscending()) {
			for (Entity entity : entities) {
				if (entity instanceof PoiseClusterEntity) {
					if (!this.level().isClientSide) {
						this.setAscending(false);
					}
					this.setBlocksToMoveUp(0);
				}
				entity.fallDistance = 0.0F;
			}
		}

		/*
		 * Tell it to being moving down if  a block is blocking its way up at its position above
		 */
		if (this.isAscending()) {
			if (this.yo == this.getY() && this.isBlockBlockingPath(false)) {
				this.beingDescending();
			}

			if (this.yo == this.getY() && this.tickCount % 25 == 0 && this.getY() + 1.0F >= this.getOrigin().getY() + this.getBlocksToMoveUp()) {
				this.beingDescending();
			}
		}

		if (this.getBlocksToMoveUp() > MAX_BLOCKS_TO_MOVE_UP) {
			this.setBlocksToMoveUp(MAX_BLOCKS_TO_MOVE_UP);
		}

		this.removeAllEffects();
		this.clearFire();

		if (this.getHealth() != 0) this.setHealth(100);

		if (!this.level().isClientSide) {
			if (!this.playedSound) {
				this.level().broadcastEntityEvent(this, (byte) 1);
				this.playedSound = true;
			}
		}
	}

	@Override
	public boolean skipAttackInteraction(Entity entityIn) {
		this.setTimesHit(this.getTimesHit() + 1);
		if (this.getTimesHit() >= 3) {
			if (!this.level().isClientSide) {
				Block.popResource(this.level(), this.blockPosition(), new ItemStack(EEBlocks.POISE_CLUSTER.get()));
				this.playSound(EESoundEvents.CLUSTER_BREAK.get());
			} else if (this.level() instanceof ClientLevel clientLevel) {
				BlockState state = EEBlocks.POISE_CLUSTER.get().defaultBlockState();
				VoxelShape voxelshape = state.getShape(this.level(), this.blockPosition());
				voxelshape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> {
					double d1 = Math.min(1.0D, x2 - x1);
					double d2 = Math.min(1.0D, y2 - y1);
					double d3 = Math.min(1.0D, z2 - z1);
					int i = Math.max(2, Mth.ceil(d1 / 0.25D));
					int j = Math.max(2, Mth.ceil(d2 / 0.25D));
					int k = Math.max(2, Mth.ceil(d3 / 0.25D));

					for (int l = 0; l < i; ++l) {
						for (int i1 = 0; i1 < j; ++i1) {
							for (int j1 = 0; j1 < k; ++j1) {
								double d4 = ((double) l + 0.5D) / (double) i;
								double d5 = ((double) i1 + 0.5D) / (double) j;
								double d6 = ((double) j1 + 0.5D) / (double) k;
								double d7 = d4 * d1 + x1;
								double d8 = d5 * d2 + y1;
								double d9 = d6 * d3 + z1;
								Minecraft.getInstance().particleEngine.add(new TerrainParticle(clientLevel, this.getX() + d7 - 0.5F, this.getY() + d8, this.getZ() + d9 - 0.5F, d4 - 0.5D, d5 - 0.5D, d6 - 0.5D, state, this.blockPosition()).updateSprite(state, this.blockPosition()));
							}
						}
					}
				});
			}

			this.discard();
			return true;
		}
		this.setAscending(true);
		this.setBlocksToMoveUp((int) (Math.ceil(this.getY()) - this.getOrigin().getY()) + 10);
		return false;
	}

	@Override
	protected void actuallyHurt(DamageSource damageSrc, float damageAmount) {
		if (damageSrc.is(DamageTypeTags.IS_PROJECTILE)) {
			this.setTimesHit(this.getTimesHit() + 1);

			if (this.getTimesHit() >= 3) {
				if (!this.getCommandSenderWorld().isClientSide) {
					Block.popResource(this.level(), this.blockPosition(), new ItemStack(EEBlocks.POISE_CLUSTER.get()));
				}
				this.discard();
			}

			if ((int) (Math.ceil(this.getY()) - this.getOrigin().getY()) + 10 < 30) {
				this.setAscending(true);
				this.setBlocksToMoveUp((int) (Math.ceil(this.getY()) - this.getOrigin().getY()) + 10);
			} else {
				this.discard();
			}
		}

		super.actuallyHurt(damageSrc, damageAmount);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void handleEntityEvent(byte id) {
		if (id == 1) {
			Minecraft.getInstance().getSoundManager().play(new PoiseClusterSound(this));
		} else {
			super.handleEntityEvent(id);
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return EESoundEvents.CLUSTER_BREAK.get();
	}

	@Override
	public boolean causeFallDamage(float p_147187_, float p_147188_, DamageSource source) {
		return false;
	}

	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		return source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
	}

	@Override
	protected float getStandingEyeHeight(Pose poseIn, EntityDimensions size) {
		return size.height;
	}

	private boolean isBlockBlockingPath(boolean down) {
		Vec3 eyePos = down ? this.position() : this.getEyePosition(1.0F);
		return this.level().clip(new ClipContext(
				eyePos,
				eyePos.add(this.getDeltaMovement()),
				ClipContext.Block.OUTLINE,
				ClipContext.Fluid.ANY,
				this
		)).getType() != Type.MISS;
	}

	private void moveEntitiesUp() {
		if (this.getDeltaMovement().length() > 0 && this.isAscending()) {
			AABB clusterBB = this.getBoundingBox().move(0.0F, 0.01F, 0.0F);
			List<Entity> entitiesAbove = this.level().getEntities(this, clusterBB);
			if (!entitiesAbove.isEmpty()) {
				for (Entity entity : entitiesAbove) {
					if (!entity.isPassenger() && !(entity instanceof PoiseClusterEntity) && entity.getPistonPushReaction() != PushReaction.IGNORE) {
						AABB entityBB = entity.getBoundingBox();
						double distanceMotion = (clusterBB.maxY - entityBB.minY) + (entity instanceof Player ? 0.0225F : 0.02F);

						if (entity instanceof Player) {
							entity.move(MoverType.PISTON, new Vec3(0.0F, distanceMotion, 0.0F));
						} else {
							entity.move(MoverType.SELF, new Vec3(0.0F, distanceMotion, 0.0F));
						}
						entity.setOnGround(true);
					}
				}
			}
		}
	}

	private void beingDescending() {
		if (!this.level().isClientSide) {
			this.setAscending(false);
		}
		this.setBlocksToMoveUp(0);
	}

	public void setOrigin(BlockPos pos) {
		this.entityData.set(ORIGIN, pos);
	}

	public BlockPos getOrigin() {
		return this.entityData.get(ORIGIN);
	}

	public void setBlocksToMoveUp(int value) {
		this.entityData.set(BLOCKS_TO_MOVE_UP, value);
	}

	public int getBlocksToMoveUp() {
		return this.entityData.get(BLOCKS_TO_MOVE_UP);
	}

	protected void setTimesHit(int hits) {
		this.entityData.set(TIMES_HIT, hits);
	}

	protected int getTimesHit() {
		return this.entityData.get(TIMES_HIT);
	}

	protected void setAscending(boolean acscending) {
		this.entityData.set(ASCEND, acscending);
	}

	public boolean isAscending() {
		return this.entityData.get(ASCEND);
	}

	@Override
	public void knockback(double p_147241_, double p_147242_, double p_147243_) {
	}

	@Override
	protected void doPush(Entity entityIn) {
	}

	@Override
	public MobType getMobType() {
		return MobType.ILLAGER;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean displayFireAnimation() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Nullable
	public AABB getCollisionBox(Entity entityIn) {
		return entityIn.isPushable() ? entityIn.getBoundingBox() : null;
	}

	@Nullable
	public AABB getCollisionBoundingBox() {
		return this.getBoundingBox();
	}

	@Override
	protected float getWaterSlowDown() {
		return 0;
	}

	@Override
	public boolean isPushedByFluid() {
		return false;
	}

	@Override
	public boolean isPushable() {
		return true;
	}

	@Override
	protected MovementEmission getMovementEmission() {
		return MovementEmission.NONE;
	}

	@Override
	public boolean startRiding(Entity entityIn, boolean force) {
		return false;
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	public void setSecondsOnFire(int seconds) {
	}

	@Override
	public Iterable<ItemStack> getArmorSlots() {
		return NonNullList.withSize(4, ItemStack.EMPTY);
	}

	@Override
	public ItemStack getItemBySlot(EquipmentSlot slotIn) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setItemSlot(EquipmentSlot slotIn, ItemStack stack) {
	}

	@Override
	public HumanoidArm getMainArm() {
		return HumanoidArm.RIGHT;
	}

	@OnlyIn(Dist.CLIENT)
	private static class PoiseClusterSound extends AbstractTickableSoundInstance {
		private final PoiseClusterEntity cluster;
		private int ticksRemoved;

		private PoiseClusterSound(PoiseClusterEntity cluster) {
			super(EESoundEvents.POISE_CLUSTER_AMBIENT.get(), SoundSource.NEUTRAL, cluster.random);
			this.cluster = cluster;
			this.looping = true;
			this.delay = 0;
			this.volume = 1.0F;
			this.x = (float) cluster.getX();
			this.y = (float) cluster.getY();
			this.z = (float) cluster.getZ();

			this.pitch = cluster.getRandom().nextFloat() * 0.3F + 0.8F;
		}

		@Override
		public boolean canStartSilent() {
			return true;
		}

		public void tick() {
			if (this.cluster.isAlive()) {
				this.x = (float) this.cluster.getX();
				this.y = (float) this.cluster.getY();
				this.z = (float) this.cluster.getZ();
			} else {
				this.ticksRemoved++;
				if (this.ticksRemoved > 10) {
					this.stop();
				}
			}

			this.volume = Math.max(0.0F, this.volume - ((float) this.ticksRemoved / 10.0F));
		}
	}
}
