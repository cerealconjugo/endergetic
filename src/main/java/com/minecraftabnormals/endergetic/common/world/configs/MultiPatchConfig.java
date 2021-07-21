package com.minecraftabnormals.endergetic.common.world.configs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.IFeatureConfig;

public final class MultiPatchConfig implements IFeatureConfig {
	public static final Codec<MultiPatchConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				Codec.INT.fieldOf("max_extra_patches").forGetter(config -> config.maxExtraPatches),
				Codec.INT.fieldOf("max_extra_radius").forGetter(config -> config.maxExtraRadius)
		).apply(instance, MultiPatchConfig::new);
	});
	private final int maxExtraPatches;
	private final int maxExtraRadius;

	public MultiPatchConfig(int maxExtraPatches, int maxExtraRadius) {
		this.maxExtraPatches = maxExtraPatches;
		this.maxExtraRadius = maxExtraRadius;
	}

	public int getMaxExtraPatches() {
		return this.maxExtraPatches;
	}

	public int getMaxExtraRadius() {
		return this.maxExtraRadius;
	}
}