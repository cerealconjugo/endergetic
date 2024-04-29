package com.teamabnormals.endergetic.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamabnormals.blueprint.client.BlueprintRenderTypes;
import com.teamabnormals.endergetic.client.model.purpoid.PurpoidGelModel;
import com.teamabnormals.endergetic.client.model.purpoid.PurpoidModel;
import com.teamabnormals.endergetic.common.entity.purpoid.Purpoid;
import com.teamabnormals.endergetic.core.other.EEModelLayers;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;

public class PurpoidGelLayer extends RenderLayer<Purpoid, PurpoidModel> {
	private final PurpoidGelModel[] gelModels;

	public PurpoidGelLayer(RenderLayerParent<Purpoid, PurpoidModel> parent, EntityModelSet entityModelSet) {
		super(parent);
		this.gelModels = new PurpoidGelModel[]{
				new PurpoidGelModel(entityModelSet.bakeLayer(EEModelLayers.PURPOID_GEL)),
				new PurpoidGelModel(entityModelSet.bakeLayer(EEModelLayers.PURP_GEL)),
				new PurpoidGelModel(entityModelSet.bakeLayer(EEModelLayers.PURPAZOID_GEL))
		};
	}

	@Override
	public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, Purpoid purpoid, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		int ordinal = purpoid.getSize().ordinal();
		PurpoidGelModel gelModel = this.gelModels[ordinal];
		gelModel.parentToHead(this.getParentModel().head);
		gelModel.setupAnim(purpoid, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		int overlay = LivingEntityRenderer.getOverlayCoords(purpoid, 0.0F);
		gelModel.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(purpoid))), packedLightIn, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
		float alpha = 1.0F;
		int stunTimer = purpoid.getStunTimer();
		if (stunTimer > 0) {
			float stunTimerHalfCycles = stunTimer / 20.0F;
			float progress = 1.0F - 2.0F * Mth.abs(stunTimerHalfCycles - Mth.floor(stunTimerHalfCycles + 0.5F));
			alpha = (progress * progress);
		}
		gelModel.renderToBuffer(matrixStackIn, bufferIn.getBuffer(BlueprintRenderTypes.getUnshadedTranslucentEntity(PurpoidEmissiveLayer.TEXTURES[ordinal], true)), 240, overlay, 1.0F, 1.0F, 1.0F, alpha);
	}

	public PurpoidGelModel getGelModel(int index) {
		return this.gelModels[index];
	}
}
