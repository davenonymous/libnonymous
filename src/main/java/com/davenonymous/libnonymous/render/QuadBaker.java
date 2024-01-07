package com.davenonymous.libnonymous.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraftforge.client.model.pipeline.QuadBakingVertexConsumer;

public class QuadBaker {
	private static void putVertex(VertexConsumer builder, Vector3f normal, Vector4f vector, float u, float v, TextureAtlasSprite sprite, float r, float g, float b, float alpha) {

		builder
			.vertex(vector.x(), vector.y(), vector.z())
			.color(r, g, b, alpha)
			.uv(sprite.getU(u), sprite.getV(v))
			.uv2(0, 0)
			.normal((float)normal.x(), (float)normal.y(), (float)normal.z())
			.endVertex();
	}
/*
	private static void putVertexUV(BakedQuadBuilder builder, float u, float v, TextureAtlasSprite sprite, int j, VertexFormatElement e) {
		switch(e.getIndex()) {
			case 0 -> builder.put(j, sprite.getU(u), sprite.getV(v));
			case 2 -> builder.put(j, (short) 0, (short) 0);
			default -> builder.put(j);
		}
	}*/

	public static BakedQuad createQuad(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4, Transformation rotation, TextureAtlasSprite sprite, float r, float g, float b, float alpha) {
		Vector3f normal = v3.copy();
		normal.sub(v2);
		Vector3f temp = v1.copy();
		temp.sub(v2);
		normal.cross(temp);
		normal.normalize();

		int tw = sprite.getWidth();
		int th = sprite.getHeight();

		rotation = rotation.blockCenterToCorner();
		rotation.transformNormal(normal);

		Vector4f vv1 = new Vector4f(v1);
		rotation.transformPosition(vv1);
		Vector4f vv2 = new Vector4f(v2);
		rotation.transformPosition(vv2);
		Vector4f vv3 = new Vector4f(v3);
		rotation.transformPosition(vv3);
		Vector4f vv4 = new Vector4f(v4);
		rotation.transformPosition(vv4);

		BakedQuad[] quad = new BakedQuad[1];
		var builder = new QuadBakingVertexConsumer(q -> quad[0] = q);
		builder.setSprite(sprite);
		builder.setDirection(Direction.getNearest(normal.x(), normal.y(), normal.z()));
		putVertex(builder, normal, vv1, 0, 0, sprite, r, g, b, alpha);
		putVertex(builder, normal, vv2, 0, th, sprite, r, g, b, alpha);
		putVertex(builder, normal, vv3, tw, th, sprite, r, g, b, alpha);
		putVertex(builder, normal, vv4, tw, 0, sprite, r, g, b, alpha);
		
		return quad[0];
	}

	public static Vector3f v(float x, float y, float z) {
		return new Vector3f(x, y, z);
	}
}