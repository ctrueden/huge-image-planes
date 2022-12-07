
package net.imagej.planes;

import ij.process.ImageProcessor;

/**
 * Add-on interface for {@link ImageProcessor} implementations supporting huge
 * image planes.
 */
public interface HugeImageProcessor {
	void setViewportOffset(int pxoff, int pyoff);
	void setViewportSize(int vw, int vh);
	void setViewportMag(float mag);
}
