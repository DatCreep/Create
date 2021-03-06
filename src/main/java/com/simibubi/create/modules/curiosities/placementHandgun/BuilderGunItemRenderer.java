package com.simibubi.create.modules.curiosities.placementHandgun;

import static com.simibubi.create.modules.curiosities.placementHandgun.BuilderGunItem.Components.Accelerator;
import static com.simibubi.create.modules.curiosities.placementHandgun.BuilderGunItem.Components.Amplifier;
import static com.simibubi.create.modules.curiosities.placementHandgun.BuilderGunItem.Components.Body;
import static com.simibubi.create.modules.curiosities.placementHandgun.BuilderGunItem.Components.Retriever;
import static com.simibubi.create.modules.curiosities.placementHandgun.BuilderGunItem.Components.Scope;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.simibubi.create.modules.curiosities.placementHandgun.BuilderGunItem.ComponentTier;
import com.simibubi.create.modules.curiosities.placementHandgun.BuilderGunItem.Components;

import net.minecraft.block.BlockState;
import net.minecraft.block.FourWayBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.animation.Animation;

public class BuilderGunItemRenderer extends ItemStackTileEntityRenderer {

	@Override
	public void renderByItem(ItemStack stack) {
		ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
		BuilderGunModel mainModel = (BuilderGunModel) itemRenderer.getModelWithOverrides(stack);
		float pt = Minecraft.getInstance().getRenderPartialTicks();
		float worldTime = Animation.getWorldTime(Minecraft.getInstance().world, pt);

		GlStateManager.pushMatrix();
		GlStateManager.translatef(0.5F, 0.5F, 0.5F);
		float lastCoordx = GLX.lastBrightnessX;
		float lastCoordy = GLX.lastBrightnessY;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, Math.min(lastCoordx + 60, 240), Math.min(lastCoordy + 120, 240));

		itemRenderer.renderItem(stack, mainModel.getBakedModel());
		renderComponent(stack, Body, itemRenderer, mainModel.body, mainModel.goldBody, mainModel.chorusBody);
		renderComponent(stack, Amplifier, itemRenderer, null, mainModel.goldAmp, mainModel.chorusAmp);
		renderComponent(stack, Retriever, itemRenderer, null, mainModel.goldRetriever, mainModel.chorusRetriever);
		renderComponent(stack, Scope, itemRenderer, null, mainModel.goldScope, mainModel.chorusScope);

		// Block indicator
		if (mainModel.showBlock && stack.hasTag() && stack.getTag().contains("BlockUsed"))
			renderBlockUsed(stack, itemRenderer);

		ClientPlayerEntity player = Minecraft.getInstance().player;
		boolean leftHanded = player.getPrimaryHand() == HandSide.LEFT;
		boolean mainHand = player.getHeldItemMainhand() == stack;
		boolean offHand = player.getHeldItemOffhand() == stack;
		float last = mainHand ^ leftHanded ? BuilderGunHandler.lastRightHandAnimation
				: BuilderGunHandler.lastLeftHandAnimation;
		float current = mainHand ^ leftHanded ? BuilderGunHandler.rightHandAnimation
				: BuilderGunHandler.leftHandAnimation;
		float animation = MathHelper.clamp(MathHelper.lerp(pt, last, current) * 5, 0, 1);

		// Core glows
		GlStateManager.disableLighting();
		float multiplier = MathHelper.sin(worldTime * 5);
		if (mainHand || offHand) {
			multiplier = animation;
		}
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, multiplier * 240, 120);
		itemRenderer.renderItem(stack, mainModel.core);
		if (BuilderGunItem.getTier(Amplifier, stack) != ComponentTier.None)
			itemRenderer.renderItem(stack, mainModel.ampCore);
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, lastCoordx, lastCoordy);
		GlStateManager.enableLighting();

		// Accelerator spins
		float angle = worldTime * -25;
		if (mainHand || offHand)
			angle += 360 * animation;

		angle %= 360;
		float offset = -.155f;
		GlStateManager.translatef(0, offset, 0);
		GlStateManager.rotatef(angle, 0, 0, 1);
		GlStateManager.translatef(0, -offset, 0);
		renderComponent(stack, Accelerator, itemRenderer, mainModel.acc, mainModel.goldAcc, mainModel.chorusAcc);

		GlStateManager.popMatrix();
	}

	public void renderBlockUsed(ItemStack stack, ItemRenderer itemRenderer) {
		BlockState state = NBTUtil.readBlockState(stack.getTag().getCompound("BlockUsed"));

		GlStateManager.pushMatrix();
		GlStateManager.translatef(-0.8F, -0.7F, -0.5F);
		GlStateManager.scalef(0.25F, 0.25F, 0.25F);
		IBakedModel modelForState = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(state);

		if (state.getBlock() instanceof FourWayBlock)
			modelForState = Minecraft.getInstance().getItemRenderer()
					.getModelWithOverrides(new ItemStack(state.getBlock()));
		
		itemRenderer.renderItem(new ItemStack(state.getBlock()), modelForState);
		GlStateManager.popMatrix();
	}

	public void renderComponent(ItemStack stack, Components component, ItemRenderer itemRenderer, IBakedModel none,
			IBakedModel gold, IBakedModel chorus) {
		ComponentTier tier = BuilderGunItem.getTier(component, stack);

		IBakedModel model = tier == ComponentTier.ChorusChrome ? chorus : gold;
		if (tier == ComponentTier.None) {
			if (none == null)
				return;
			model = none;
		}

		itemRenderer.renderItem(stack, model);
	}

}
