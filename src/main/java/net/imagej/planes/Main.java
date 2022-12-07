package net.imagej.planes;

import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.process.ImageProcessor;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.function.Function;

// https://github.com/imagej/ImageJ/issues/120
public class Main {

	public static void main(String[] args) {
		ImageJ ij = new ImageJ();

		int dataWidth = 876543210, dataHeight = 234567890, slices = 333;

		final Function<Integer, FloatPlane> planes = //
			n -> (x, y) -> n + y + x;

		final FloatPlaneStack stack = new FloatPlaneStack(planes, dataWidth, dataHeight, slices);

		final ImagePlus imp = new ImagePlus("Huge Plane from Function", stack);

		// inject our own window and canvas
		final ImageCanvas canvas = new ImageCanvas(imp) {
			@Override
			public void paint(Graphics g) {
				System.out.println("==> ImageCanvas.paint: source rect = " + getSrcRect() + ", mag = " + getMagnification());
				super.paint(g);
			}
		};

		final ImageWindow window = new ImageWindow(imp, canvas);
		imp.setWindow(window);

		final ComponentAdapter resizeListener = new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				// every time the canvas resizes, tell the ImageProcessor
				// so that it can know the current viewport bounds.
				Rectangle bounds = window.getBounds();
				int w = bounds.width;
				int h = bounds.height;
				int n = imp.getSlice();
				System.out.println("==> ImageWindow resized: " + w + ", " + h);
				ImageProcessor ip = stack.getProcessor(n);
				if (!(ip instanceof HugeImageProcessor)) {
					throw new IllegalStateException("Bad ImageProcessor #" + n + "!");
				}
				((HugeImageProcessor) ip).setViewportSize(w, h);
				imp.setProcessor(imp.getProcessor()); // Trigger a repaint!
			}
		};
		//canvas.addComponentListener(resizeListener);
		window.addComponentListener(resizeListener);

		imp.show();
	}
}
