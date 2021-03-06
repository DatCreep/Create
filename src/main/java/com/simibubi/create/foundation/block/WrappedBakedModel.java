package com.simibubi.create.foundation.block;

import java.util.List;
import java.util.Random;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

@SuppressWarnings("deprecation")
public class WrappedBakedModel implements IBakedModel {

	protected IBakedModel template;

	public WrappedBakedModel(IBakedModel template) {
		this.template = template;
	}
	
	@Override
	public IBakedModel getBakedModel() {
		return template;
	}

	@Override
	public boolean isAmbientOcclusion() {
		return template.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return template.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return template.isBuiltInRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleTexture(IModelData data) {
		return template.getParticleTexture(data);
	}

	@Override
	public ItemOverrideList getOverrides() {
		return template.getOverrides();
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
		Pair<? extends IBakedModel, Matrix4f> pair = template.handlePerspective(cameraTransformType);
		return Pair.of(this, pair.getRight());
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
		return getQuads(state, side, rand, EmptyModelData.INSTANCE);
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData data) {
		return template.getQuads(state, side, rand, data);
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return getParticleTexture(EmptyModelData.INSTANCE);
	}

}
