
package net.imagej.planes;

import ij.VirtualStack;
import ij.process.ImageProcessor;

import java.util.function.Function;

public class FloatPlaneStack extends VirtualStack {

	private Function<Integer, FloatPlane> planes;

	public FloatPlaneStack(final Function<Integer, FloatPlane> planes,
		final int width, final int height, final int slices)
	{
		super(width, height, slices, "32-bit");
		this.planes = planes;
	}

	@Override
	public ImageProcessor getProcessor(int n) {
		return new FloatPlaneProcessor(planes.apply(n), getWidth(), getHeight());
	}

}
