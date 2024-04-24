package com.teamabnormals.endergetic.core.registry;

import com.mojang.serialization.Codec;
import com.teamabnormals.endergetic.client.particle.CorrockCrownParticle;
import com.teamabnormals.endergetic.client.particle.FastBlockParticle;
import com.teamabnormals.endergetic.client.particle.PoiseBubbleParticle;
import com.teamabnormals.endergetic.client.particle.data.CorrockCrownParticleData;
import com.teamabnormals.endergetic.core.EndergeticExpansion;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

@EventBusSubscriber(modid = EndergeticExpansion.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EEParticleTypes {
	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, EndergeticExpansion.MOD_ID);

	public static final RegistryObject<SimpleParticleType> POISE_BUBBLE = createBasicParticleType(true, "poise_bubble");
	public static final RegistryObject<SimpleParticleType> SHORT_POISE_BUBBLE = createBasicParticleType(true, "short_poise_bubble");
	public static final RegistryObject<SimpleParticleType> ENDER_FLAME = createBasicParticleType(true, "ender_flame");
	public static final RegistryObject<ParticleType<BlockParticleOption>> FAST_BLOCK = createParticleType("fast_block", BlockParticleOption.DESERIALIZER, BlockParticleOption::codec);
	public static final RegistryObject<ParticleType<CorrockCrownParticleData>> OVERWORLD_CROWN = createParticleType("overworld_crown", CorrockCrownParticleData.DESERIALIZER, CorrockCrownParticleData::codec);
	public static final RegistryObject<ParticleType<CorrockCrownParticleData>> NETHER_CROWN = createParticleType("nether_crown", CorrockCrownParticleData.DESERIALIZER, CorrockCrownParticleData::codec);
	public static final RegistryObject<ParticleType<CorrockCrownParticleData>> END_CROWN = createParticleType("end_crown", CorrockCrownParticleData.DESERIALIZER, CorrockCrownParticleData::codec);

	private static RegistryObject<SimpleParticleType> createBasicParticleType(boolean alwaysShow, String name) {
		return PARTICLES.register(name, () -> new SimpleParticleType(alwaysShow));
	}

	@SuppressWarnings("deprecation")
	private static <T extends ParticleOptions> RegistryObject<ParticleType<T>> createParticleType(String name, ParticleOptions.Deserializer<T> deserializer, Function<ParticleType<T>, Codec<T>> function) {
		return PARTICLES.register(name, () -> new ParticleTypeWithData<>(deserializer, function));
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void registerParticleTypes(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(ENDER_FLAME.get(), FlameParticle.Provider::new);
		event.registerSpriteSet(POISE_BUBBLE.get(), PoiseBubbleParticle.Factory::new);
		event.registerSpriteSet(SHORT_POISE_BUBBLE.get(), PoiseBubbleParticle.ShortFactory::new);
		event.registerSpecial(FAST_BLOCK.get(), new FastBlockParticle.Factory());
		event.registerSpriteSet(OVERWORLD_CROWN.get(), CorrockCrownParticle.Factory::new);
		event.registerSpriteSet(NETHER_CROWN.get(), CorrockCrownParticle.Factory::new);
		event.registerSpriteSet(END_CROWN.get(), CorrockCrownParticle.Factory::new);
	}

	static class ParticleTypeWithData<T extends ParticleOptions> extends ParticleType<T> {
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
}