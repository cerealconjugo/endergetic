package com.teamabnormals.endergetic.client.renderer.entity;

import com.teamabnormals.endergetic.client.model.PoiseClusterModel;
import com.teamabnormals.endergetic.common.entity.PoiseClusterEntity;
import com.teamabnormals.endergetic.core.EndergeticExpansion;
import com.teamabnormals.endergetic.core.other.EEModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;

public class PoiseClusterRender extends LivingEntityRenderer<PoiseClusterEntity, PoiseClusterModel<PoiseClusterEntity>> {

	public PoiseClusterRender(EntityRendererProvider.Context context) {
		super(context, new PoiseClusterModel<>(context.bakeLayer(EEModelLayers.POISE_CLUSTER)), 0.0F);
	}

	@Override
	protected RenderType getRenderType(PoiseClusterEntity cluster, boolean p_230496_2_, boolean p_230496_3_, boolean p_230496_4_) {
		return RenderType.entityTranslucent(this.getTextureLocation(cluster));
	}

	@Override
	public ResourceLocation getTextureLocation(PoiseClusterEntity entity) {
		return new ResourceLocation(EndergeticExpansion.MOD_ID, "textures/entity/poise_cluster.png");
	}

	protected boolean shouldShowName(PoiseClusterEntity entity) {
		return false;
	}

}