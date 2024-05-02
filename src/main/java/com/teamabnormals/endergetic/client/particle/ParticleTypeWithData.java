package com.teamabnormals.endergetic.client.particle;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Function;

public class ParticleTypeWithData<T extends ParticleOptions> extends ParticleType<T> {
	private final Function<ParticleType<T>, Codec<T>> function;

	@SuppressWarnings("deprecation")
	public ParticleTypeWithData(ParticleOptions.Deserializer<T> deserializer, Function<ParticleType<T>, Codec<T>> function) {
		super(false, deserializer);
		this.function = function;
	}

	@Override
	public Codec<T> codec() {
		return this.function.apply(this);
	}
}