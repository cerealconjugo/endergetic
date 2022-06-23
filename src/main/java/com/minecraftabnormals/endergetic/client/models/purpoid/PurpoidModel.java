package com.minecraftabnormals.endergetic.client.models.purpoid;

import com.minecraftabnormals.abnormals_core.core.endimator.entity.EndimatorEntityModel;
import com.minecraftabnormals.abnormals_core.core.endimator.entity.EndimatorModelRenderer;
import com.minecraftabnormals.endergetic.common.entities.purpoid.PurpoidEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;

/**
 * @author Endergized
 * @author SmellyModder (Luke Tonon)
 * <p>
 * Created using Tabula 7.0.0
 */
public class PurpoidModel extends EndimatorEntityModel<PurpoidEntity> {
	public EndimatorModelRenderer head;
	public EndimatorModelRenderer rim1;
	public EndimatorModelRenderer rim2;
	public EndimatorModelRenderer rim3;
	public EndimatorModelRenderer rim4;
	public EndimatorModelRenderer tentacleSmall1;
	public EndimatorModelRenderer tentacleSmall2;
	public EndimatorModelRenderer tentacleSmall3;
	public EndimatorModelRenderer tentacleSmall4;
	public EndimatorModelRenderer tentacleLarge1;
	public EndimatorModelRenderer tentacleLarge2;
	public EndimatorModelRenderer tentacleLarge3;
	public EndimatorModelRenderer tentacleLarge4;

	public PurpoidModel() {
		this.texWidth = 64;
		this.texHeight = 96;
		this.rim2 = new EndimatorModelRenderer(this, 12, 59);
		this.rim2.setPos(0.0F, 0.0F, 7.0F);
		this.rim2.addBox(-9.0F, 0.0F, 0.0F, 18, 6, 0, 0.0F);
		this.setRotateAngle(rim2, 0.7853981633974483F, 0.0F, 0.0F);
		this.head = new EndimatorModelRenderer(this, 0, 32);
		this.head.setPos(0.0F, 23.0F, 0.0F);
		this.head.addBox(-7.0F, -13.0F, -7.0F, 14, 13, 14, 0.0F);
		this.rim3 = new EndimatorModelRenderer(this, 12, 41);
		this.rim3.setPos(7.0F, 0.0F, 0.0F);
		this.rim3.addBox(0.0F, 0.0F, -9.0F, 0, 6, 18, 0.0F);
		this.setRotateAngle(rim3, 0.0F, 0.0F, -0.7853981633974483F);
		this.tentacleSmall3 = new EndimatorModelRenderer(this, 0, 59);
		this.tentacleSmall3.mirror = true;
		this.tentacleSmall3.setPos(5.5F, 0.0F, -6.0F);
		this.tentacleSmall3.addBox(-1.5F, 0.0F, 0.0F, 3, 32, 0, 0.0F);
		this.setRotateAngle(tentacleSmall3, 0.0F, -0.7853981633974483F, 0.0F);
		this.tentacleLarge4 = new EndimatorModelRenderer(this, 6, 59);
		this.tentacleLarge4.setPos(0.0F, 0.0F, -7.0F);
		this.tentacleLarge4.addBox(-1.5F, 0.0F, 0.0F, 3, 37, 0, 0.0F);
		this.tentacleLarge3 = new EndimatorModelRenderer(this, 6, 59);
		this.tentacleLarge3.mirror = true;
		this.tentacleLarge3.setPos(0.0F, 0.0F, 7.0F);
		this.tentacleLarge3.addBox(-1.5F, 0.0F, 0.0F, 3, 37, 0, 0.0F);
		this.tentacleSmall4 = new EndimatorModelRenderer(this, 0, 59);
		this.tentacleSmall4.setPos(-5.5F, 0.0F, -6.0F);
		this.tentacleSmall4.addBox(-1.5F, 0.0F, 0.0F, 3, 32, 0, 0.0F);
		this.setRotateAngle(tentacleSmall4, 0.0F, 0.7853981633974483F, 0.0F);
		this.tentacleLarge1 = new EndimatorModelRenderer(this, 6, 59);
		this.tentacleLarge1.mirror = true;
		this.tentacleLarge1.setPos(-7.0F, 0.0F, 0.0F);
		this.tentacleLarge1.addBox(-1.5F, 0.0F, 0.0F, 3, 37, 0, 0.0F);
		this.setRotateAngle(tentacleLarge1, 0.0F, 1.5707963267948966F, 0.0F);
		this.rim1 = new EndimatorModelRenderer(this, 12, 59);
		this.rim1.setPos(0.0F, 0.0F, -7.0F);
		this.rim1.addBox(-9.0F, 0.0F, 0.0F, 18, 6, 0, 0.0F);
		this.setRotateAngle(rim1, -0.7853981633974483F, 0.0F, 0.0F);
		this.rim4 = new EndimatorModelRenderer(this, 12, 41);
		this.rim4.setPos(-7.0F, 0.0F, 0.0F);
		this.rim4.addBox(0.0F, 0.0F, -9.0F, 0, 6, 18, 0.0F);
		this.setRotateAngle(rim4, 0.0F, 0.0F, 0.7853981633974483F);
		this.tentacleSmall1 = new EndimatorModelRenderer(this, 0, 59);
		this.tentacleSmall1.setPos(-5.5F, 0.0F, 6.0F);
		this.tentacleSmall1.addBox(-1.5F, 0.0F, 0.0F, 3, 32, 0, 0.0F);
		this.setRotateAngle(tentacleSmall1, 0.0F, -0.7853981633974483F, 0.0F);
		this.tentacleLarge2 = new EndimatorModelRenderer(this, 6, 59);
		this.tentacleLarge2.setPos(7.0F, 0.0F, 0.0F);
		this.tentacleLarge2.addBox(-1.5F, 0.0F, 0.0F, 3, 37, 0, 0.0F);
		this.setRotateAngle(tentacleLarge2, 0.0F, 1.5707963267948966F, 0.0F);
		this.tentacleSmall2 = new EndimatorModelRenderer(this, 0, 59);
		this.tentacleSmall2.setPos(5.5F, 0.0F, 6.0F);
		this.tentacleSmall2.addBox(-1.5F, 0.0F, 0.0F, 3, 32, 0, 0.0F);
		this.setRotateAngle(tentacleSmall2, 0.0F, 0.7853981633974483F, 0.0F);
		this.head.addChild(this.rim2);
		this.head.addChild(this.rim3);
		this.head.addChild(this.tentacleSmall3);
		this.head.addChild(this.tentacleLarge4);
		this.head.addChild(this.tentacleLarge3);
		this.head.addChild(this.tentacleSmall4);
		this.head.addChild(this.tentacleLarge1);
		this.head.addChild(this.rim1);
		this.head.addChild(this.rim4);
		this.head.addChild(this.tentacleSmall1);
		this.head.addChild(this.tentacleLarge2);
		this.head.addChild(this.tentacleSmall2);

		this.setDefaultBoxValues();
	}

	@Override
	public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		super.renderToBuffer(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		this.head.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void setupAnim(PurpoidEntity purpoid, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(purpoid, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		if (purpoid.isBaby()) limbSwing /= 3.0F;
		float rimAngle = 0.17F * Mth.sin(0.1F * ageInTicks) + Mth.cos(limbSwing * 0.8F) * limbSwingAmount * 1.16F;
		this.rim1.xRot -= rimAngle;
		this.rim2.xRot += rimAngle;
		this.rim3.zRot -= rimAngle;
        this.rim4.zRot += rimAngle;
        float tentacleAngle = 0.09F * Mth.cos(0.1F * ageInTicks + 1.0F) + Mth.sin(limbSwing * 0.6F) * Math.min(0.3F, limbSwingAmount) * 0.5F;
        this.tentacleLarge1.xRot -= tentacleAngle;
		this.tentacleLarge2.xRot += tentacleAngle;
		this.tentacleLarge3.xRot += tentacleAngle;
		this.tentacleLarge4.xRot -= tentacleAngle;
		this.tentacleSmall1.xRot += tentacleAngle;
		this.tentacleSmall2.xRot += tentacleAngle;
		this.tentacleSmall3.xRot -= tentacleAngle;
		this.tentacleSmall4.xRot -= tentacleAngle;
	}

	@Override
	public void animateModel(PurpoidEntity purpoid) {
		super.animateModel(purpoid);
		if (this.tryToPlayEndimation(PurpoidEntity.TELEPORT_TO_ANIMATION) || this.tryToPlayEndimation(PurpoidEntity.FAST_TELEPORT_TO_ANIMATION)) {
			this.startKeyframe(5);
			this.scale(this.head, 1.3F, 1.3F, 1.3F);
			this.rotate(this.tentacleLarge1, -0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleLarge2, 0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleLarge3, 0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleLarge4, -0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleSmall1, 0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleSmall2, 0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleSmall3, -0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleSmall4, -0.26F, 0.0F, 0.0F);
			this.endKeyframe();

			this.startKeyframe(5);
			this.scale(this.head, -1.0F, -1.0F, -1.0F);
			this.rotate(this.tentacleLarge1, 0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleLarge2, -0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleLarge3, -0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleLarge4, 0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleSmall1, -0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleSmall2, -0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleSmall3, 0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleSmall4, 0.26F, 0.0F, 0.0F);
			this.endKeyframe();

			this.setStaticKeyframe(8);
		} else if (this.tryToPlayEndimation(PurpoidEntity.TELEPORT_FROM_ANIMATION)) {
			this.startKeyframe(5);
			this.scale(this.head, 1.3F, 1.3F, 1.3F);
			this.rotate(this.tentacleLarge1, -0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleLarge2, 0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleLarge3, 0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleLarge4, -0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleSmall1, 0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleSmall2, 0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleSmall3, -0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleSmall4, -0.26F, 0.0F, 0.0F);
			this.endKeyframe();

			this.resetKeyframe(5);
		} else if (this.tryToPlayEndimation(PurpoidEntity.DEATH_ANIMATION)) {
			this.startKeyframe(5);
			this.scale(this.head, -0.4F, -0.4F, -0.4F);
			this.rotate(this.tentacleLarge1, 0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleLarge2, -0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleLarge3, -0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleLarge4, 0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleSmall1, -0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleSmall2, -0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleSmall3, 0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleSmall4, 0.26F, 0.0F, 0.0F);
			this.endKeyframe();

			this.startKeyframe(5);
			this.scale(this.head, 0.9F, 0.9F, 0.9F);
			this.rotate(this.tentacleLarge1, -0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleLarge2, 0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleLarge3, 0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleLarge4, -0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleSmall1, 0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleSmall2, 0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleSmall3, -0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleSmall4, -0.26F, 0.0F, 0.0F);
			this.endKeyframe();

			this.setStaticKeyframe(10);
		} else if (this.tryToPlayEndimation(PurpoidEntity.TELEFRAG_ANIMATION)) {
			this.startKeyframe(5);
			this.scale(this.head, 1.0F, 1.0F, 1.0F);
			this.rotate(this.tentacleLarge1, -0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleLarge2, 0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleLarge3, 0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleLarge4, -0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleSmall1, 0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleSmall2, 0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleSmall3, -0.26F, 0.0F, 0.0F);
			this.rotate(this.tentacleSmall4, -0.26F, 0.0F, 0.0F);
			this.endKeyframe();

			this.resetKeyframe(5);
		}
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(EndimatorModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}