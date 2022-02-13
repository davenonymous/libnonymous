package com.davenonymous.libnonymous.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import java.util.OptionalDouble;

public class CustomRenderTypes  extends RenderType {

	public CustomRenderTypes(String p_173178_, VertexFormat p_173179_, VertexFormat.Mode p_173180_, int p_173181_, boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_) {
		super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
	}

	private static final LineStateShard THICK_LINES = new LineStateShard(OptionalDouble.of(4.0D));

	public static final RenderType OVERLAY_LINES = create("overlay_lines_libnonymous",
			DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.LINES, 256, true, false,
			CompositeState.builder().setLineState(THICK_LINES)
					.setLayeringState(NO_LAYERING)
					.setShaderState(RENDERTYPE_LINES_SHADER)
					.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
					.setTextureState(NO_TEXTURE)
					.setDepthTestState(NO_DEPTH_TEST)
					.setOverlayState(NO_OVERLAY)
					.setCullState(NO_CULL)
					.setLightmapState(NO_LIGHTMAP)
					.setWriteMaskState(RenderStateShard.COLOR_WRITE)
					.createCompositeState(false));
}