package com.davenonymous.libnonymous.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Transformation;
import net.minecraft.core.Position;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraftforge.client.model.pipeline.QuadBakingVertexConsumer;

public class QuadBaker {
	private static void putVertex(VertexConsumer builder, Vec3 normal, Vector4f vector, float u, float v, TextureAtlasSprite sprite, float r, float g, float b, float alpha) {

		builder
			.vertex(vector.x(), vector.y(), vector.z())
			.color(r, g, b, alpha)
			.uv(sprite.getU(u), sprite.getV(v))
			.uv2(0, 0)
			.normal((float)normal.x(), (float)normal.y(), (float)normal.z())
			.endVertex();
	}

	public static BakedQuad createQuad(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, Transformation rotation, TextureAtlasSprite sprite, float r, float g, float b, float alpha) {
		Vec3 normal = v3.subtract(v2).cross(v1.subtract(v2)).normalize();

		BakedQuad[] quad = new BakedQuad[1];
		QuadBakingVertexConsumer builder = new QuadBakingVertexConsumer(q -> quad[0] = q);
		builder.setSprite(sprite);
		builder.setDirection(Direction.getNearest(normal.x, normal.y, normal.z));
		putVertex(builder, normal, v1.x, v1.y, v1.z, 0, 0, sprite, r, g, b, alpha);
		putVertex(builder, normal, v2.x, v2.y, v2.z, 0, 16, sprite, r, g, b, alpha);
		putVertex(builder, normal, v3.x, v3.y, v3.z, 16, 16, sprite, r, g, b, alpha);
		putVertex(builder, normal, v4.x, v4.y, v4.z, 16, 0, sprite, r, g, b, alpha);
		return quad[0];
	}

	public static void putVertex(VertexConsumer builder, Position normal,
								 double x, double y, double z, float u, float v,
								 TextureAtlasSprite sprite, float r, float g, float b, float a) {
		float iu = sprite.getU(u);
		float iv = sprite.getV(v);
		builder.vertex(x, y, z)
				.uv(iu, iv)
				.uv2(0, 0)
				.color(r, g, b, a)
				.normal((float) normal.x(), (float) normal.y(), (float) normal.z())
				.endVertex();
	}
}