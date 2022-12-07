package net.imagej.planes;

import ij.process.FloatProcessor;

public class FloatPlaneProcessor extends FloatProcessor implements
	HugeImageProcessor
{

	private FloatPlane plane;

	private float[] pix;

	/** X offset into the full image plane data. */
	private int pxoff;

	/** Y offset into the full image plane data. */
	private int pyoff;

	/** Viewport magnification/scale. */
	private float mag; // TODO: Do we need separate X and Y scales?

	// TODO: Do we actually need these here?
	private int planeWidth, planeHeight;

	/**
	 * Fills in pixel values from the image plane data.
	 * 
	 * @param plane Function from image plane coordinates to values.
	 * @param pix Array of pixels to populate.
	 * @param pxoff X offset into the image plane.
	 * @param pyoff Y offset into the image plane.
	 * @param mag Magnification/scale factor from data to viewport.
	 * @param vw Viewport width.
	 * @param vh Viewport height.
	 */
	private static void fill(FloatPlane plane, float[] pix,
		int pxoff, int pyoff, float mag, int vw, int vh)
	{
		if (pix.length != vw * vh) {
			throw new IllegalArgumentException("Pixels array is length " +
				pix.length + " but expected " + vw + " x " + vh + " = " + //
				(vw * vh));
		}
		int i = 0;
		for (int vy=0; vy<vh; vy++) {
			final int py = pyoff + (int) (mag * vy);
			for (int vx=0; vx<vw; vx++) {
				final int px = pxoff + (int) (mag * vx);
				pix[i++] = plane.get(px, py);
				if (i % 10000 == 0) System.out.println("Pixel #" + i + " = " + pix[i-1] + " (" + vx + "," + vy + "," + px + "," + py + ")");
			}
		}
	}

	/**
	 * Calculates pixel values from the image plane data.
	 * 
	 * @param plane Function from image plane coordinates to values.
	 * @param pxoff X offset into the image plane.
	 * @param pyoff Y offset into the image plane.
	 * @param mag Magnification/scale factor from data to viewport.
	 * @param vw Viewport width.
	 * @param vh Viewport height.
	 * @return Populated array of pixels.
	 */
	private static float[] calc(FloatPlane plane,
		int pxoff, int pyoff, float mag, int vw, int vh)
	{
		float[] pix = new float[vw * vh];
		fill(plane, pix, pxoff, pyoff, mag, vw, vh);
		return pix;
	}

	public FloatPlaneProcessor(FloatPlane plane, int width, int height) {
		// NB: Set the *viewport* width and height to 512 x 512 initially.
		// From the FloatProcessor superclass's perspective, the image dimensions
		// are actually 512 x 512. But our subclass will be more clever.
		super(512, 512, calc(plane, 0, 0, 1, 512, 512));
		this.plane = plane;
		this.planeWidth = width;
		this.planeHeight = height;
		setViewportSize(512, 512);
	}

	@Override
	public void setViewportOffset(int pxoff, int pyoff) {
		if (pxoff == this.pxoff && pyoff == this.pyoff) return;
		this.pxoff = pxoff;
		this.pyoff = pyoff;
		update();
	}

	@Override
	public void setViewportSize(int w, int h) {
		if (w == width && h == height) return;
		width = w;
		height = h;
		update();
	}

	@Override
	public void setViewportMag(float mag) {
		if (mag == this.mag) return;
		this.mag = mag;
		update();
	}

	private void update() {
		int vw = width, vh = height;
		if (pix == null || pix.length != vw * vh) pix = new float[vw * vh];
		fill(plane, pix, pxoff, pyoff, mag, vw, vh);
		System.out.println("==> FloatPlaneProcessor.update(): " + //
			vw + " x " + vh + " -> " + " " + pix.length + " pixels computed");
		setPixels(pix);
	}

	@Override
	public void setPixels(final Object pixels) {
		if (!(pixels instanceof float[])) {
			throw new IllegalArgumentException("Invalid pixels type");
		}
		pix = (float[]) pixels;
		System.out.println("==> FloatPlaneProcessor.setPixels: " + pix.length);
		super.setPixels(pixels);
	}
}
