package com.teamabnormals.endergetic.core.registry;

import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.client.renderer.block.TypedBlockEntityWithoutLevelRenderer;
import com.teamabnormals.blueprint.common.block.BlueprintBeehiveBlock;
import com.teamabnormals.blueprint.common.block.BlueprintDirectionalBlock;
import com.teamabnormals.blueprint.common.block.LogBlock;
import com.teamabnormals.blueprint.common.block.chest.BlueprintChestBlock;
import com.teamabnormals.blueprint.common.block.chest.BlueprintTrappedChestBlock;
import com.teamabnormals.blueprint.common.block.sign.BlueprintStandingSignBlock;
import com.teamabnormals.blueprint.common.block.sign.BlueprintWallSignBlock;
import com.teamabnormals.blueprint.common.item.BEWLRBlockItem;
import com.teamabnormals.endergetic.core.registry.other.EEProperties;
import com.teamabnormals.endergetic.common.block.*;
import com.teamabnormals.endergetic.common.block.entity.BolloomBudTileEntity;
import com.teamabnormals.endergetic.common.block.poise.*;
import com.teamabnormals.endergetic.common.block.poise.boof.BoofBlock;
import com.teamabnormals.endergetic.common.block.poise.boof.DispensedBoofBlock;
import com.teamabnormals.endergetic.common.block.poise.hive.PuffBugHiveBlock;
import com.teamabnormals.endergetic.common.block.poise.hive.PuffbugHiveHangerBlock;
import com.teamabnormals.endergetic.core.EndergeticExpansion;
import com.teamabnormals.endergetic.core.registry.util.EndergeticBlockSubRegistryHelper;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.BiFunction;

@EventBusSubscriber(modid = EndergeticExpansion.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class EEBlocks {
	private static final EndergeticBlockSubRegistryHelper HELPER = EndergeticExpansion.REGISTRY_HELPER.getBlockSubHelper();

	public static final RegistryObject<Block> OVERWORLD_CORROCK = HELPER.createBlock("overworld_corrock", () -> new CorrockPlantBlock(EEProperties.corrockBase(MapColor.TERRACOTTA_BROWN, false), false));
	public static final RegistryObject<Block> NETHER_CORROCK = HELPER.createBlock("nether_corrock", () -> new CorrockPlantBlock(EEProperties.corrockBase(MapColor.TERRACOTTA_RED, false), false));
	public static final RegistryObject<Block> END_CORROCK = HELPER.createBlock("end_corrock", () -> new CorrockPlantBlock(EEProperties.corrockBase(MapColor.TERRACOTTA_PURPLE, false), false));
	public static final RegistryObject<Block> SPECKLED_OVERWORLD_CORROCK = HELPER.createBlock("speckled_overworld_corrock", () -> new SpeckledCorrockBlock(Properties.copy(Blocks.END_STONE)));
	public static final RegistryObject<Block> SPECKLED_NETHER_CORROCK = HELPER.createBlock("speckled_nether_corrock", () -> new SpeckledCorrockBlock(Properties.copy(Blocks.END_STONE)));
	public static final RegistryObject<Block> SPECKLED_END_CORROCK = HELPER.createBlock("speckled_end_corrock", () -> new SpeckledCorrockBlock(Properties.copy(Blocks.END_STONE)));
	public static final RegistryObject<Block> OVERWORLD_CORROCK_BLOCK = HELPER.createBlock("overworld_corrock_block", () -> new CorrockBlock(EEProperties.corrockBase(MapColor.TERRACOTTA_BROWN, true), SPECKLED_OVERWORLD_CORROCK, OVERWORLD_CORROCK));
	public static final RegistryObject<Block> NETHER_CORROCK_BLOCK = HELPER.createBlock("nether_corrock_block", () -> new CorrockBlock(EEProperties.corrockBase(MapColor.TERRACOTTA_RED, true), SPECKLED_NETHER_CORROCK, NETHER_CORROCK));
	public static final RegistryObject<Block> END_CORROCK_BLOCK = HELPER.createBlock("end_corrock_block", () -> new CorrockBlock(EEProperties.corrockBase(MapColor.TERRACOTTA_PURPLE, true), SPECKLED_END_CORROCK, END_CORROCK));

	public static final RegistryObject<Block> PETRIFIED_OVERWORLD_CORROCK = HELPER.createBlock("petrified_overworld_corrock", () -> new CorrockPlantBlock(EEProperties.corrockBase(MapColor.TERRACOTTA_BROWN, false), true));
	public static final RegistryObject<Block> PETRIFIED_NETHER_CORROCK = HELPER.createBlock("petrified_nether_corrock", () -> new CorrockPlantBlock(EEProperties.corrockBase(MapColor.TERRACOTTA_RED, false), true));
	public static final RegistryObject<Block> PETRIFIED_END_CORROCK = HELPER.createBlock("petrified_end_corrock", () -> new CorrockPlantBlock(EEProperties.corrockBase(MapColor.TERRACOTTA_PURPLE, false), true));
	public static final RegistryObject<Block> PETRIFIED_SPECKLED_OVERWORLD_CORROCK = HELPER.createBlock("petrified_speckled_overworld_corrock", () -> new Block(Properties.copy(Blocks.END_STONE)));
	public static final RegistryObject<Block> PETRIFIED_SPECKLED_NETHER_CORROCK = HELPER.createBlock("petrified_speckled_nether_corrock", () -> new Block(Properties.copy(Blocks.END_STONE)));
	public static final RegistryObject<Block> PETRIFIED_SPECKLED_END_CORROCK = HELPER.createBlock("petrified_speckled_end_corrock", () -> new Block(Properties.copy(Blocks.END_STONE)));
	public static final RegistryObject<Block> PETRIFIED_OVERWORLD_CORROCK_BLOCK = HELPER.createBlock("petrified_overworld_corrock_block", () -> new Block(EEProperties.corrockBase(MapColor.TERRACOTTA_BROWN, true)));
	public static final RegistryObject<Block> PETRIFIED_NETHER_CORROCK_BLOCK = HELPER.createBlock("petrified_nether_corrock_block", () -> new Block(EEProperties.corrockBase(MapColor.TERRACOTTA_RED, true)));
	public static final RegistryObject<Block> PETRIFIED_END_CORROCK_BLOCK = HELPER.createBlock("petrified_end_corrock_block", () -> new Block(EEProperties.corrockBase(MapColor.TERRACOTTA_PURPLE, true)));

	public static final RegistryObject<CorrockCrownWallBlock> OVERWORLD_WALL_CORROCK_CROWN = HELPER.createBlockNoItem("overworld_wall_corrock_crown", () -> new CorrockCrownWallBlock(EEProperties.glowingCorrockBase(MapColor.COLOR_BROWN), CorrockCrownBlock.DimensionalType.OVERWORLD, false));
	public static final RegistryObject<CorrockCrownBlock> OVERWORLD_CORROCK_CROWN = HELPER.createCorrockStandingBlock("overworld_corrock_crown", () -> new CorrockCrownStandingBlock(EEProperties.glowingCorrockBase(MapColor.COLOR_BROWN), CorrockCrownBlock.DimensionalType.OVERWORLD, false), OVERWORLD_WALL_CORROCK_CROWN);
	public static final RegistryObject<CorrockCrownWallBlock> NETHER_WALL_CORROCK_CROWN = HELPER.createBlockNoItem("nether_wall_corrock_crown", () -> new CorrockCrownWallBlock(EEProperties.glowingCorrockBase(MapColor.COLOR_RED), CorrockCrownBlock.DimensionalType.NETHER, false));
	public static final RegistryObject<CorrockCrownBlock> NETHER_CORROCK_CROWN = HELPER.createCorrockStandingBlock("nether_corrock_crown", () -> new CorrockCrownStandingBlock(EEProperties.glowingCorrockBase(MapColor.COLOR_RED), CorrockCrownBlock.DimensionalType.NETHER, false), NETHER_WALL_CORROCK_CROWN);
	public static final RegistryObject<CorrockCrownWallBlock> END_WALL_CORROCK_CROWN = HELPER.createBlockNoItem("end_wall_corrock_crown", () -> new CorrockCrownWallBlock(EEProperties.glowingCorrockBase(MapColor.COLOR_PURPLE), CorrockCrownBlock.DimensionalType.END, false));
	public static final RegistryObject<CorrockCrownBlock> END_CORROCK_CROWN = HELPER.createCorrockStandingBlock("end_corrock_crown", () -> new CorrockCrownStandingBlock(EEProperties.glowingCorrockBase(MapColor.COLOR_PURPLE), CorrockCrownBlock.DimensionalType.END, false), END_WALL_CORROCK_CROWN);

	public static final RegistryObject<CorrockCrownWallBlock> PETRIFIED_OVERWORLD_WALL_CORROCK_CROWN = HELPER.createBlockNoItem("petrified_overworld_wall_corrock_crown", () -> new CorrockCrownWallBlock(EEProperties.glowingCorrockBase(MapColor.COLOR_BROWN), CorrockCrownBlock.DimensionalType.OVERWORLD, true));
	public static final RegistryObject<CorrockCrownBlock> PETRIFIED_OVERWORLD_CORROCK_CROWN = HELPER.createCorrockStandingBlock("petrified_overworld_corrock_crown", () -> new CorrockCrownStandingBlock(EEProperties.glowingCorrockBase(MapColor.COLOR_BROWN), CorrockCrownBlock.DimensionalType.OVERWORLD, true), PETRIFIED_OVERWORLD_WALL_CORROCK_CROWN);
	public static final RegistryObject<CorrockCrownWallBlock> PETRIFIED_NETHER_WALL_CORROCK_CROWN = HELPER.createBlockNoItem("petrified_nether_wall_corrock_crown", () -> new CorrockCrownWallBlock(EEProperties.glowingCorrockBase(MapColor.COLOR_RED), CorrockCrownBlock.DimensionalType.NETHER, true));
	public static final RegistryObject<CorrockCrownBlock> PETRIFIED_NETHER_CORROCK_CROWN = HELPER.createCorrockStandingBlock("petrified_nether_corrock_crown", () -> new CorrockCrownStandingBlock(EEProperties.glowingCorrockBase(MapColor.COLOR_RED), CorrockCrownBlock.DimensionalType.NETHER, true), PETRIFIED_NETHER_WALL_CORROCK_CROWN);
	public static final RegistryObject<CorrockCrownWallBlock> PETRIFIED_END_WALL_CORROCK_CROWN = HELPER.createBlockNoItem("petrified_end_wall_corrock_crown", () -> new CorrockCrownWallBlock(EEProperties.glowingCorrockBase(MapColor.COLOR_PURPLE), CorrockCrownBlock.DimensionalType.END, true));
	public static final RegistryObject<CorrockCrownBlock> PETRIFIED_END_CORROCK_CROWN = HELPER.createCorrockStandingBlock("petrified_end_corrock_crown", () -> new CorrockCrownStandingBlock(EEProperties.glowingCorrockBase(MapColor.COLOR_PURPLE), CorrockCrownBlock.DimensionalType.END, true), PETRIFIED_END_WALL_CORROCK_CROWN);

	public static final RegistryObject<Block> INFESTED_CORROCK = HELPER.createBlock("infested_corrock", () -> new InfestedCorrockBlock(EEProperties.INFESTED_CORROCK));
	public static final RegistryObject<Block> PETRIFIED_INFESTED_CORROCK = HELPER.createBlock("petrified_infested_corrock", () -> new Block(EEProperties.PETRIFIED_INFESTED_CORROCK));

	/*
	 * Poise Forest
	 */
	public static final RegistryObject<Block> EUMUS_POISMOSS = HELPER.createBlock("eumus_poismoss", () -> new PoismossEumusBlock(EEProperties.EUMUS_POISMOSS));
	public static final RegistryObject<Block> POISMOSS = HELPER.createBlock("poismoss", () -> new PoismossBlock(EEProperties.poiseGrass(false)));
	public static final RegistryObject<Block> POISE_BUSH = HELPER.createBlock("poise_bush", () -> new PoiseBushBlock(EEProperties.poiseGrass(true).offsetType(BlockBehaviour.OffsetType.XYZ)));
	public static final RegistryObject<Block> TALL_POISE_BUSH = HELPER.createBlock("tall_poise_bush", () -> new PoiseTallBushBlock(EEProperties.poiseGrass(true).offsetType(BlockBehaviour.OffsetType.XZ)));
	public static final RegistryObject<Block> POISE_CLUSTER = HELPER.createBlock("poise_cluster", () -> new PoiseClusterBlock(EEProperties.POISE_CLUSTER.randomTicks()));

	public static final RegistryObject<Block> STRIPPED_POISE_STEM = HELPER.createBlock("stripped_poise_stem", () -> new RotatedPillarBlock(EEProperties.POISE.log()));
	public static final RegistryObject<Block> STRIPPED_POISE_WOOD = HELPER.createBlock("stripped_poise_wood", () -> new RotatedPillarBlock(EEProperties.POISE.log()));
	public static final RegistryObject<Block> POISE_STEM = HELPER.createBlock("poise_stem", () -> new LogBlock(EEBlocks.STRIPPED_POISE_STEM, EEProperties.POISE.log()));
	public static final RegistryObject<Block> POISE_WOOD = HELPER.createBlock("poise_wood", () -> new LogBlock(EEBlocks.STRIPPED_POISE_WOOD, EEProperties.POISE.log()));
	public static final RegistryObject<Block> GLOWING_POISE_STEM = HELPER.createBlock("glowing_poise_stem", () -> new GlowingPoiseStemBlock(STRIPPED_POISE_STEM, EEProperties.POISE.log().lightLevel(state -> 15)));
	public static final RegistryObject<Block> GLOWING_POISE_WOOD = HELPER.createBlock("glowing_poise_wood", () -> new GlowingPoiseStemBlock(STRIPPED_POISE_WOOD, EEProperties.POISE.log().lightLevel(state -> 15)));
	public static final RegistryObject<Block> POISE_PLANKS = HELPER.createBlock("poise_planks", () -> new Block(EEProperties.POISE.planks()));
	public static final RegistryObject<Block> POISE_DOOR = HELPER.createBlock("poise_door", () -> new DoorBlock(EEProperties.POISE.door(), EEProperties.POISE_BLOCK_SET));
	public static final RegistryObject<Block> POISE_SLAB = HELPER.createBlock("poise_slab", () -> new SlabBlock(EEProperties.POISE.planks()));
	public static final RegistryObject<Block> POISE_STAIRS = HELPER.createBlock("poise_stairs", () -> new StairBlock(() -> POISE_PLANKS.get().defaultBlockState(), EEProperties.POISE.planks()));
	public static final RegistryObject<Block> POISE_FENCE = HELPER.createFuelBlock("poise_fence", () -> new FenceBlock(EEProperties.POISE.planks()), 300);
	public static final RegistryObject<Block> POISE_FENCE_GATE = HELPER.createFuelBlock("poise_fence_gate", () -> new FenceGateBlock(EEProperties.POISE.planks(), EEProperties.POISE_WOOD_TYPE), 300);
	public static final RegistryObject<Block> POISE_PRESSURE_PLATE = HELPER.createBlock("poise_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, EEProperties.POISE.pressurePlate(), EEProperties.POISE_BLOCK_SET));
	public static final RegistryObject<Block> POISE_BUTTON = HELPER.createBlock("poise_button", () -> new ButtonBlock(EEProperties.POISE.button(), EEProperties.POISE_BLOCK_SET, 30, true));
	public static final RegistryObject<Block> POISE_TRAPDOOR = HELPER.createBlock("poise_trapdoor", () -> new TrapDoorBlock(EEProperties.POISE.trapdoor(), EEProperties.POISE_BLOCK_SET));
	public static final RegistryObject<Block> POISE_BOOKSHELF = HELPER.createFuelBlock("poise_bookshelf", () -> new Block(EEProperties.POISE.chiseledBookshelf()), 300);
	public static final RegistryObject<Block> POISE_BOARDS = HELPER.createFuelBlock("poise_boards", () -> new RotatedPillarBlock(EEProperties.POISE.planks()), 300);
	public static final RegistryObject<Block> POISE_LADDER = HELPER.createFuelBlock("poise_ladder", () -> new LadderBlock(EEProperties.POISE.ladder()), 300);
	public static final RegistryObject<Block> POISE_BEEHIVE = HELPER.createBlock("poise_beehive", () -> new BlueprintBeehiveBlock(EEProperties.POISE.beehive()));
	public static final Pair<RegistryObject<BlueprintStandingSignBlock>, RegistryObject<BlueprintWallSignBlock>> POISE_SIGNS = HELPER.createSignBlock("poise", EEProperties.POISE_WOOD_TYPE, EEProperties.POISE.sign());
	public static final RegistryObject<BlueprintChestBlock> POISE_CHEST = HELPER.createChestBlock("poise", EEProperties.POISE.chest());
	public static final RegistryObject<BlueprintTrappedChestBlock> TRAPPED_POISE_CHEST = HELPER.createTrappedChestBlockNamed("poise", EEProperties.POISE.chest());

	public static final RegistryObject<Block> BOLLOOM_BUD = HELPER.createBlockWithBEWLR("bolloom_bud", () -> new BolloomBudBlock(EEProperties.bolloomBud().randomTicks()), () -> () -> new BEWLRBlockItem.LazyBEWLR(bolloomBudISTER()));
	public static final RegistryObject<Block> BOLLOOM_PARTICLE = HELPER.createBlockNoItem("bolloom_particle", () -> new Block(EEProperties.bolloomBud().noCollission()));
	public static final RegistryObject<Block> PUFFBUG_HIVE = HELPER.createBlock("puffbug_hive", () -> new PuffBugHiveBlock(EEProperties.getPuffBugHive(true)));
	public static final RegistryObject<Block> HIVE_HANGER = HELPER.createBlockNoItem("hive_hanger", () -> new PuffbugHiveHangerBlock(EEProperties.getPuffBugHive(false)));
	public static final RegistryObject<Block> BOOF_BLOCK = HELPER.createBlock("boof_block", () -> new BoofBlock(EEProperties.BOOF_BLOCK));
	public static final RegistryObject<Block> DISPENSED_BOOF_BLOCK = HELPER.createBlockNoItem("dispensed_boof_block", () -> new DispensedBoofBlock(EEProperties.BOOF_BLOCK.noCollission().noOcclusion().strength(-1, 3600000.0F)));
	public static final RegistryObject<Block> BOLLOOM_CRATE = HELPER.createFuelBlock("bolloom_crate", () -> new BlueprintDirectionalBlock(EEProperties.BOLLOOM_CRATE), 300);

	/*
	 * Misc
	 */
	public static final RegistryObject<Block> EUMUS = HELPER.createBlock("eumus", () -> new Block(EEProperties.EUMUS));
	public static final RegistryObject<Block> EUMUS_BRICKS = HELPER.createBlock("eumus_bricks", () -> new Block(EEProperties.EUMUS_BRICKS));
	public static final RegistryObject<Block> CRACKED_EUMUS_BRICKS = HELPER.createBlock("cracked_eumus_bricks", () -> new Block(EEProperties.EUMUS_BRICKS));
	public static final RegistryObject<Block> CHISELED_EUMUS_BRICKS = HELPER.createBlock("chiseled_eumus_bricks", () -> new Block(EEProperties.EUMUS_BRICKS));
	public static final RegistryObject<Block> EUMUS_BRICK_SLAB = HELPER.createBlock("eumus_brick_slab", () -> new SlabBlock(EEProperties.EUMUS_BRICKS));
	public static final RegistryObject<Block> EUMUS_BRICK_STAIRS = HELPER.createBlock("eumus_brick_stairs", () -> new StairBlock(() -> EUMUS_BRICKS.get().defaultBlockState(), EEProperties.EUMUS_BRICKS));
	public static final RegistryObject<Block> EUMUS_BRICK_WALL = HELPER.createBlock("eumus_brick_wall", () -> new WallBlock(EEProperties.EUMUS_BRICKS));

	public static final RegistryObject<Block> POTTED_POISE_BUSH = HELPER.createBlockNoItem("potted_poise_bush", () -> new FlowerPotBlock(POISE_BUSH.get(), Properties.copy(Blocks.POTTED_PINK_TULIP)));
	public static final RegistryObject<Block> POTTED_TALL_POISE_BUSH = HELPER.createBlockNoItem("potted_tall_poise_bush", () -> new FlowerPotBlock(TALL_POISE_BUSH.get(), Properties.copy(Blocks.POTTED_PINK_TULIP)));

	public static final RegistryObject<Block> ACIDIAN_LANTERN = HELPER.createBlock("acidian_lantern", () -> new AcidianLanternBlock(EEProperties.ACIDIAN_LANTERN));
	public static final RegistryObject<Block> CRYSTAL_HOLDER = HELPER.createBlock("crystal_holder", () -> new Block(EEProperties.MYSTICAL_OBSIDIAN));
	public static final RegistryObject<Block> MYSTICAL_OBSIDIAN = HELPER.createBlock("mystical_obsidian", () -> new Block(EEProperties.MYSTICAL_OBSIDIAN));
	public static final RegistryObject<Block> MYSTICAL_OBSIDIAN_WALL = HELPER.createBlock("mystical_obsidian_wall", () -> new WallBlock(EEProperties.MYSTICAL_OBSIDIAN));
	public static final RegistryObject<Block> MYSTICAL_OBSIDIAN_RUNE = HELPER.createBlock("mystical_obsidian_rune", () -> new RotatableBlock(EEProperties.MYSTICAL_OBSIDIAN));
	public static final RegistryObject<Block> MYSTICAL_OBSIDIAN_ACTIVATION_RUNE = HELPER.createBlock("mystical_obsidian_activation_rune", () -> new RotatableBlock(EEProperties.MYSTICAL_OBSIDIAN));
	public static final RegistryObject<Block> ACTIVATED_MYSTICAL_OBSIDIAN_ACTIVATION_RUNE = HELPER.createBlock("activated_mystical_obsidian_activation_rune", () -> new RotatableBlock(EEProperties.MYSTICAL_OBSIDIAN.lightLevel(state -> 5)));

	public static final RegistryObject<Block> ENDER_FIRE = HELPER.createBlockNoItem("ender_fire", () -> new EnderFireBlock(Properties.copy(Blocks.FIRE)));
	public static final RegistryObject<Block> ENDER_CAMPFIRE = HELPER.createBlock("ender_campfire", () -> new EnderCampfireBlock(Block.Properties.copy(Blocks.CAMPFIRE)));
	public static final RegistryObject<Block> ENDER_LANTERN = HELPER.createBlock("ender_lantern", () -> new LanternBlock(Block.Properties.copy(Blocks.LANTERN)));
	public static final RegistryObject<Block> ENDER_WALL_TORCH = HELPER.createBlockNoItem("ender_wall_torch", () -> new EnderWallTorchBlock(Block.Properties.copy(Blocks.TORCH)));
	public static final RegistryObject<Block> ENDER_TORCH = HELPER.createStandingAndWallBlock("ender_torch", () -> new EnderTorchBlock(Block.Properties.copy(Blocks.TORCH)), ENDER_WALL_TORCH, Direction.DOWN);

	public static final RegistryObject<Block> CHISELED_END_STONE_BRICKS = HELPER.createBlock("chiseled_end_stone_bricks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.END_STONE_BRICKS)));
	public static final RegistryObject<Block> CRACKED_END_STONE_BRICKS = HELPER.createBlock("cracked_end_stone_bricks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.END_STONE_BRICKS)));
	public static final RegistryObject<Block> CRACKED_PURPUR_BLOCK = HELPER.createBlock("cracked_purpur_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.PURPUR_BLOCK)));

	public static final RegistryObject<Block> EUMUS_POISMOSS_PATH = HELPER.createBlock("eumus_poismoss_path", () -> new PoismossPathBlock(EUMUS, EEProperties.EUMUS_POISMOSS_PATH));
	public static final RegistryObject<Block> POISMOSS_PATH = HELPER.createBlock("poismoss_path", () -> new PoismossPathBlock(() -> Blocks.END_STONE, EEProperties.POISMOSS_PATH));

	public static final RegistryObject<Block> EETLE_EGG = HELPER.createBlock("eetle_egg", () -> new EetleEggBlock(EEProperties.EETLE_EGG));

	@OnlyIn(Dist.CLIENT)
	private static BiFunction<BlockEntityRenderDispatcher, EntityModelSet, BlockEntityWithoutLevelRenderer> bolloomBudISTER() {
		return (dispatcher, modelSet) -> new TypedBlockEntityWithoutLevelRenderer<>(dispatcher, modelSet, new BolloomBudTileEntity(BlockPos.ZERO, BOLLOOM_BUD.get().defaultBlockState()));
	}
}