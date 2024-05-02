package com.teamabnormals.endergetic.client.renderer.entity.booflo;

import com.teamabnormals.endergetic.client.model.booflo.AdolescentBoofloModel;
import com.teamabnormals.endergetic.client.renderer.entity.layers.EmissiveLayerRenderer;
import com.teamabnormals.endergetic.client.renderer.entity.layers.LayerRendererBoofloAdolescentFruit;
import com.teamabnormals.endergetic.common.entity.booflo.BoofloAdolescent;
import com.teamabnormals.endergetic.core.EndergeticExpansion;
import com.teamabnormals.endergetic.core.other.EEModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BoofloAdolescentRenderer extends MobRenderer<BoofloAdolescent, AdolescentBoofloModel<BoofloAdolescent>> {

	public BoofloAdolescentRenderer(EntityRendererProvider.Context context) {
		super(context, new AdolescentBoofloModel<>(context.bakeLayer(EEModelLayers.ADOLESCENT_BOOFLO)), 0.5F);
		this.addLayer(new EmissiveLayerRenderer<>(this, new ResourceLocation(EndergeticExpansion.MOD_ID, "textures/entity/booflo/booflo_adolescent_emissive.png")));
		this.addLayer(new LayerRendererBoofloAdolescentFruit(this, context.getItemInHandRenderer()));
	}

	@Override
	public ResourceLocation getTextureLocation(BoofloAdolescent entity) {
		return new ResourceLocation(EndergeticExpansion.MOD_ID, "textures/entity/booflo/booflo_adolescent.png");
	}

}