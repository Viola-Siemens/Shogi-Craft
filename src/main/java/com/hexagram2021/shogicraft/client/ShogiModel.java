package com.hexagram2021.shogicraft.client;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

import javax.annotation.Nonnull;

public class ShogiModel<T extends Entity> extends EntityModel<T> {
	private final ModelRenderer root;

	public ShogiModel() {
		this.texWidth = 64;
		this.texHeight = 32;

		this.root = new ModelRenderer(this);
		this.root.setPos(0.0F, 24.0F, 0.0F);
		this.root.texOffs(42, 6).addBox(-1.0F, -1.75F, -6.0F, 2.0F, 2.0F, 1.0F, -0.25F, false);
		this.root.texOffs(32, 6).addBox(-2.0F, -1.75F, -5.5F, 4.0F, 2.0F, 1.0F, -0.25F, false);
		this.root.texOffs(32, 3).addBox(-3.0F, -1.75F, -5.0F, 6.0F, 2.0F, 1.0F, -0.25F, false);
		this.root.texOffs(32, 0).addBox(-4.0F, -1.75F, -4.5F, 8.0F, 2.0F, 1.0F, -0.25F, false);
		this.root.texOffs(0, 20).addBox(-4.5F, -1.75F, -4.0F, 9.0F, 2.0F, 2.0F, -0.25F, false);
		this.root.texOffs(0, 16).addBox(-5.0F, -1.75F, -2.5F, 10.0F, 2.0F, 2.0F, -0.25F, false);
		this.root.texOffs(0, 12).addBox(-5.5F, -1.75F, -1.0F, 11.0F, 2.0F, 2.0F, -0.25F, false);
		this.root.texOffs(0, 8).addBox(-6.0F, -1.75F, 0.5F, 12.0F, 2.0F, 2.0F, -0.25F, false);
		this.root.texOffs(0, 4).addBox(-6.5F, -1.75F, 2.0F, 13.0F, 2.0F, 2.0F, -0.25F, false);
		this.root.texOffs(0, 0).addBox(-7.0F, -1.75F, 3.5F, 14.0F, 2.0F, 2.0F, -0.25F, false);
	}

	@Override
	public void setupAnim(@Nonnull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		
	}

	@Override
	public void renderToBuffer(@Nonnull MatrixStack transform, @Nonnull IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.root.render(transform, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}