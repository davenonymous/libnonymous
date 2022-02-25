package com.davenonymous.libnonymous.helper;

import com.mojang.math.Matrix4f;
import net.minecraft.core.Direction;
import net.minecraft.core.Rotations;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShapeHelper {
	static enum Rotation {
		Clockwise,
		CounterClockwise
	};

	public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
		VoxelShape[] buffer = new VoxelShape[]{ shape, Shapes.empty() };

		int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;
		for (int i = 0; i < times; i++) {
			buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1], Shapes.create(1-maxZ, minY, minX, 1-minZ, maxY, maxX)));
			buffer[0] = buffer[1];
			buffer[1] = Shapes.empty();
		}

		return buffer[0];
	}

	/*
	public static VoxelShape rotateShape(Direction.Axis axis, Rotation rotation, VoxelShape shape) {
		final VoxelShape[] result = {Shapes.empty()};

		shape.forAllBoxes((pMinX, pMinY, pMinZ, pMaxX, pMaxY, pMaxZ) -> {
			VoxelShape newShape;
			// identical shape = Shapes.create(pMinX, pMinY, pMinX, pMaxX, pMaxY, pMaxZ);
			if(axis == Direction.Axis.Y) {
				// x := z
				// y := y
				// z := -x

			} else if(axis == Direction.Axis.X) {
				// x := x
				// y := -z
				// z := y
				newShape = Shapes.create(pMinX, -pMinZ, pMinY, );

			} else if(axis == Direction.Axis.Z) {
				// This seems correct
				// x := -y
				// y := x
				// z := z
				newShape = Shapes.create(-pMinY, pMinX, pMinZ, -pMaxY, pMaxX, pMaxZ);

				//newShape = Shapes.create(1-pMaxZ, pMinY, pMinX, 1-pMinZ, pMaxY, pMaxX);
			}

			result[0] = Shapes.or(result[0], newShape);
		});

		return result[0];
	}
	*/
}