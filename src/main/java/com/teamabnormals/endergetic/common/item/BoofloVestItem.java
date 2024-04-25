package com.teamabnormals.endergetic.common.item;

import com.teamabnormals.endergetic.client.model.armor.BoofloVestModel;
import com.teamabnormals.endergetic.core.EndergeticExpansion;
import com.teamabnormals.endergetic.core.other.EEArmorMaterials;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * @author - SmellyModder(Luke Tonon)
 */
public class BoofloVestItem extends ArmorItem {
	private static final String DEFAULT_TEXTURE = EndergeticExpansion.MOD_ID + ":textures/models/armor/booflo_vest.png";
	private static final String BOOFED_TEXTURE = EndergeticExpansion.MOD_ID + ":textures/models/armor/booflo_vest_boofed.png";
	public static final String TICKS_BOOFED_TAG = "ticksBoofed";
	public static final String BOOFED_TAG = "boofed";
	public static final String TIMES_BOOFED_TAG = "timesBoofed";

	public BoofloVestItem(Properties properties) {
		super(EEArmorMaterials.BOOFLO_VEST, Type.CHESTPLATE, properties);
	}

	@Override
	public void onArmorTick(ItemStack stack, Level world, Player player) {
		CompoundTag tag = stack.getOrCreateTag();
		int ticksBoofed = tag.getInt(TICKS_BOOFED_TAG);
		if (tag.getBoolean(BOOFED_TAG)) {
			ticksBoofed++;
			tag.putInt(TICKS_BOOFED_TAG, ticksBoofed);
		} else {
			tag.putInt(TICKS_BOOFED_TAG, 0);
		}

		if (ticksBoofed >= 10) {
			tag.putBoolean(BOOFED_TAG, false);
		}

		if (tag.getInt(TICKS_BOOFED_TAG) == 10) {
			player.getItemBySlot(EquipmentSlot.CHEST).hurtAndBreak(2, player, (onBroken) -> {
				onBroken.broadcastBreakEvent(EquipmentSlot.CHEST);
			});
		}

		if (player.onGround() || (player.isPassenger() && player.getVehicle().onGround())) {
			tag.putInt(TIMES_BOOFED_TAG, 0);
		}
	}

	@Override
	public boolean canBeDepleted() {
		return true;
	}

	public static boolean canBoof(ItemStack stack, Player player) {
		return !player.getCooldowns().isOnCooldown(stack.getItem()) && !stack.getOrCreateTag().getBoolean(BOOFED_TAG);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		return stack.hasTag() && stack.getTag().getBoolean(BOOFED_TAG) ? BOOFED_TEXTURE : DEFAULT_TEXTURE;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			@Override
			@NotNull
			public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack stack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
				return stack.hasTag() && stack.getTag().getBoolean(BOOFED_TAG) ? BoofloVestModel.INSTANCE : original;
			}
		});
	}
}
