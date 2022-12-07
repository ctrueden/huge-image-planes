
package net.imagej.planes;

import ij.VirtualStack;
import ij.process.ImageProcessor;

import java.util.function.Function;

public class BytePlaneStack extends VirtualStack {

	private Function<Integer, BytePlane> planes;

	public BytePlaneStack(final Function<Integer, BytePlane> planes,
		final int width, final int height, final int slices)
	{
		super(width, height, slices);
		this.planes = planes;
	}

	@Override
	public ImageProcessor getProcessor(int n) {
		// FIXME
//		return new BytePlaneProcessor(planes.apply(n), getWidth(), getHeight());
		return null;
	}

}
