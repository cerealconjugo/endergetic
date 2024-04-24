package com.teamabnormals.endergetic.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.teamabnormals.endergetic.client.model.BoofBlockDispenserModel;
import com.teamabnormals.endergetic.common.block.entity.boof.DispensedBlockBoofTileEntity;
import com.teamabnormals.endergetic.common.block.poise.boof.DispensedBoofBlock;
import com.teamabnormals.endergetic.core.EndergeticExpansion;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class DispensedBoofBlockTileEntityRenderer implements BlockEntityRenderer<DispensedBlockBoofTileEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EndergeticExpansion.MOD_ID, "textures/block/boof_block_dispensed.png");
	private final BoofBlockDispenserModel model;

	public DispensedBoofBlockTileEntityRenderer(BlockEntityRendererProvider.Context context) {
		this.model = new BoofBlockDispenserModel(context.bakeLayer(BoofBlockDispenserModel.LOCATION));
	}

	@Override
	public void render(DispensedBlockBoofTileEntity boof, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		Direction facing = boof.hasLevel() ? boof.getBlockState().getValue(DispensedBoofBlock.FACING) : Direction.NORTH;

		matrixStack.pushPose();
		matrixStack.translate(0.5F, 1.5F, 0.5F);

		if (facing.getAxis().isVertical()) {
			float offset = -facing.getAxisDirection().getStep();
			matrixStack.mulPose(Axis.XP.rotationDegrees(90.0F * offset));
			matrixStack.translate(0.0F, 1.125F, 1.0F * offset);
		} else {
			matrixStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
		}

		matrixStack.scale(1.0F, -1.0F, -1.0F);

		VertexConsumer ivertexbuilder = buffer.getBuffer(RenderType.entityCutout(TEXTURE));
		this.model.renderAll(matrixStack, ivertexbuilder, combinedLightIn, combinedOverlayIn);

		matrixStack.popPose();
	}
}