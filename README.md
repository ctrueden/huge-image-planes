This work is the *beginning* of an attempt to support huge image planes via
the original ImageJ API (`ImagePlus`, `ImageProcessor`, `VirtualStack`,
`ImageCanvas`, `ImageWindow`). ***It is not yet functional.***

The goal is to make the active `ImageProcessor` report its image plane as only
what's actually being displayed on the canvas. So if your plane is 100K x 100K,
but your canvas viewport is 512 x 512, then the `ImageProcessor` returns a
512 x 512 image. And then when you perform the usual actions such as zooming
in or out, rather than letting ImageJ apply that magnification, we always build
every pixel of the viewport's image ourselves, at the desired magnification and
offset (and appropriate resolution if the source data is multi-resolution).

Unfortunately, from what I can tell so far, ImageJ's image painting
infrastructure seems geared toward always scaling the `ImageProcessor`'s image
data according to the magnification, so we need to hack around that somehow,
maybe by telling ImageJ that the magnification is always 1.0. But then ImageJ
won't know to paint the location indicators in the top left corner, so we'll
have to find an alternative way to do that.

There are several other wrinkles. ImageJ was not designed for this approach,
and there maybe assumptions baked into the code at various places that we now
violate by trying to do this. For example, the ImageStack knows the image width
and height, but so does the ImageProcessor, and it was
formerly reasonable to assume these two sources of truth would always be
aligned, but now we are doing otherwise, with the VirtualStack subclass
reporting the full-resolution image plane width and height, and the
ImageProcessor reporting the viewport image width and height.

Unless I am missing something in the ImageJ code, it might ultimately be better
for ImageJ itself to support a separate drawing mode for this case, where the
`ImageCanvas` knows that the `ImageProcessor` will handle the magnification and
panning on its own, rather than handling these aspects itself. Perhaps a
subclass of `ImageCanvas` would be sufficient to achieve this, although I
expect there would be some code duplication, because it is a lengthy class,
with monolithic methods such as `paint(Graphics)`.
