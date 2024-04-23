package com.teamabnormals.endergetic.core.mixin;

import com.teamabnormals.endergetic.common.entity.bolloom.BolloomBalloon;
import com.teamabnormals.endergetic.common.levelgen.EndergeticDragonFightManager;
import com.teamabnormals.endergetic.core.interfaces.BalloonHolder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;

@Mixin(ServerLevel.class)
public final class ServerLevelMixin {
	@Shadow
	@Final
	EntityTickList entityTickList;

	@Shadow
	public EndDragonFight dragonFight;

	@Inject(at = @At("RETURN"), method = "<init>")
	private void replaceDragonFightManager(MinecraftServer server, Executor p_215000_, LevelStorageAccess p_215001_, ServerLevelData p_215002_, ResourceKey<Level> p_215003_, LevelStem p_215004_, ChunkProgressListener p_215005_, boolean p_215006_, long p_215007_, List<CustomSpawner> p_215008_, boolean p_215009_, RandomSequences p_288977_, CallbackInfo ci) {
		if (this.dragonFight != null) {
			this.dragonFight = new EndergeticDragonFightManager(this.dragonFight.level, server.getWorldData().worldGenOptions().seed(), server.getWorldData().endDragonFightData());
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V", ordinal = 0, shift = At.Shift.AFTER), method = "tickNonPassenger")
	private void updateBalloons(Entity entity, CallbackInfo info) {
		BalloonHolder balloonHolder = (BalloonHolder) entity;
		for (BolloomBalloon balloon : balloonHolder.getBalloons()) {
			if (!balloon.isRemoved() && balloon.getAttachedEntity() == entity) {
				if (this.entityTickList.contains(balloon)) {
					balloon.setOldPosAndRot();
					balloon.tickCount++;
					balloon.updateAttachedPosition();
				}
			} else {
				balloon.detachFromEntity();
			}
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V", shift = At.Shift.AFTER), method = "tickPassenger")
	private void updatePassengerBalloons(Entity ridingEntity, Entity passenger, CallbackInfo info) {
		BalloonHolder balloonHolder = (BalloonHolder) passenger;
		for (BolloomBalloon balloon : balloonHolder.getBalloons()) {
			if (!balloon.isRemoved() && balloon.getAttachedEntity() == passenger) {
				if (this.entityTickList.contains(balloon)) {
					balloon.setOldPosAndRot();
					balloon.tickCount++;
					balloon.updateAttachedPosition();
				}
			} else {
				balloon.detachFromEntity();
			}
		}
	}
}