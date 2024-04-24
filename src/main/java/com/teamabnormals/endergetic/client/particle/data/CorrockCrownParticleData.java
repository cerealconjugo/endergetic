package com.teamabnormals.endergetic.client.particle.data;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Optional;

@SuppressWarnings("deprecation")
public class CorrockCrownParticleData implements ParticleOptions {
	public static final ParticleOptions.Deserializer<CorrockCrownParticleData> DESERIALIZER = new ParticleOptions.Deserializer<CorrockCrownParticleData>() {
		@Override
		public CorrockCrownParticleData fromCommand(ParticleType<CorrockCrownParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
			reader.expect(' ');
			boolean eetle = reader.readBoolean();
			reader.expect(' ');
			return new CorrockCrownParticleData(particleTypeIn, eetle, Optional.of(reader.readFloat()));
		}

		@Override
		public CorrockCrownParticleData fromNetwork(ParticleType<CorrockCrownParticleData> particleTypeIn, FriendlyByteBuf buffer) {
			return new CorrockCrownParticleData(particleTypeIn, buffer.readBoolean(), buffer.readBoolean() ? Optional.of(buffer.readFloat()) : Optional.empty());
		}
	};
	private final ParticleType<CorrockCrownParticleData> particleType;
	private final boolean eetle;
	private final Optional<Float> scale;

	public CorrockCrownParticleData(ParticleType<CorrockCrownParticleData> particleType, boolean eetle, Optional<Float> scale) {
		this.particleType = particleType;
		this.eetle = eetle;
		this.scale = scale;
	}

	public CorrockCrownParticleData(ParticleType<CorrockCrownParticleData> particleType, boolean eetle, float scale) {
		this(particleType, eetle, Optional.of(scale));
	}

	public CorrockCrownParticleData(ParticleType<CorrockCrownParticleData> particleType, boolean eetle) {
		this(particleType, eetle, Optional.empty());
	}

	public static Codec<CorrockCrownParticleData> codec(ParticleType<CorrockCrownParticleData> type) {
		return RecordCodecBuilder.create((instance) -> {
			return instance.group(
					Codec.BOOL.fieldOf("eetle").forGetter(data -> data.eetle),
					Codec.FLOAT.optionalFieldOf("scale").forGetter(data -> data.scale)
			).apply(instance, ((eetle, scale) -> new CorrockCrownParticleData(type, eetle, scale)));
		});
	}

	@Override
	public ParticleType<?> getType() {
		return this.particleType;
	}

	@Override
	public void writeToNetwork(FriendlyByteBuf buffer) {
		buffer.writeBoolean(this.eetle);
		Optional<Float> scale = this.scale;
		boolean present = scale.isPresent();
		buffer.writeBoolean(present);
		if (present) {
			buffer.writeDouble(scale.get());
		}
	}

	@Override
	public String writeToString() {
		return BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()) + ", eetle:" + this.eetle + ", scale:" + this.scale;
	}

	public boolean isEetle() {
		return this.eetle;
	}

	public Optional<Float> getScale() {
		return this.scale;
	}
}
