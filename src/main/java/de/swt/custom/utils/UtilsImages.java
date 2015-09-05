package de.swt.custom.utils;

import org.apache.commons.lang.Validate;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities to access to resources (e.g. files).
 * 
 * @author rub
 * 
 */
public final class UtilsImages {

    private static final Logger LOG = LoggerFactory.getLogger(UtilsImages.class);

    private UtilsImages() {
    }




    /**
     * Icon sizes.
     * 
     * @author rub
     * 
     */
    public static enum ImageSize {
        Size16x16(16), Size24x24(24), Size32x32(32), Size48x48(48), Size64x64(64), Size128x128(128);

        private Integer size;

        private ImageSize(Integer aSize) {
            this.size = aSize;
        }

        /**
         * Image size, height = width
         * 
         * @return size
         */
        public Integer size() {
            return size;
        }

        /**
         * Retrieves a {@link ImageSize} by a numeric value. If there's no value of the given size
         * <code>null</code> is returned.
         * 
         * @param size
         *            side length
         * @return image size of given side length or <code>null</code>
         */
        public static ImageSize getBySize(int size) {
            for (ImageSize s : values()) {
                if (s.size == size) {
                    return s;
                }
            }
            return null;
        }
    }

    /**
     * Resizes an image, using the given scaling factor. Constructs a new image resource, please
     * take care of resource
     * disposal if you no longer need the original one. This method is optimized for quality, not
     * for speed.
     * 
     * @param image
     *            source image
     * @param scale
     *            scale factor (<1 = downscaling, >1 = upscaling)
     * @return scaled image
     */
    public static Image resize(Image image, float scale) {
        int w = image.getBounds().width;
        int h = image.getBounds().height;

        // resize buffered image
        int newWidth = Math.round(scale * w);
        int newHeight = Math.round(scale * h);

        Image newImage = new Image(Display.getDefault(), image.getImageData().scaledTo(newWidth, newHeight));
        return newImage;
    }

    /**
     * Brings the given image to the targetSize, it's assumed the given image's width = height.
     * 
     * @param image
     *            image to scale
     * @param targetSize
     *            target width an height
     * @return a scaled image
     */
    public static Image resize(Image image, Integer targetSize) {
        Validate.notNull(image);
        Validate.notNull(targetSize);
        float scale = (float) targetSize / (float) image.getImageData().width;
        return resize(image, scale);
    }

    /**
     * Creates a new image with the given targetSize.
     * 
     * @param image
     *            image to scale
     * @param targetSize
     *            image size
     * @return a new image of defined target size
     */
    public static Image resize(Image image, ImageSize targetSize) {
        Validate.notNull(image);
        Validate.notNull(targetSize);
        return resize(image, targetSize.size());
    }

    
}
