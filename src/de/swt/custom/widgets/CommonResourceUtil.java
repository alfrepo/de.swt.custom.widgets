package de.swt.custom.widgets;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities to access to resources (e.g. files).
 * 
 * @author rub
 * 
 */
public final class CommonResourceUtil {

    private static final Logger LOG = LoggerFactory.getLogger(CommonResourceUtil.class);


    private CommonResourceUtil() {
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
    
//    /**
//     * Creates a new {@link Image} for the given path
//     * 
//     * @param relativeImagePath
//     *            the relative path to the Image
//     * @return an ImageDescriptor of an Image from the given path, or null.
//     */
//    public static Image getImage(String relativeImagePath) {
//        return getImage(null, relativeImagePath);
//    }
//
//    /**
//     * {@inheritDoc #getImageDescriptor(Bundle, String)}
//     * 
//     * @param relativeImagePath
//     *            {@inheritDoc #getImageDescriptor(Bundle, String)}
//     * @return {@inheritDoc #getImageDescriptor(Bundle, String)}
//     */
//    public static ImageDescriptor getImageDescriptor(String relativeImagePath) {
//        return getImageDescriptor((Bundle) null, relativeImagePath);
//    }

//    /**
//     * Retrieves an {@link ImageDescriptor} of an image in the given.
//     * 
//     * @param bundle
//     *            bundle the image is expected in
//     * @param relativeImagePath
//     *            path of the resource in the passes bundle
//     * @return an image descriptor or null
//     */
//    public static ImageDescriptor getImageDescriptor(Bundle bundle, String relativeImagePath) {
//        Bundle definingBundle = getBundle(bundle);
//        ImageDescriptor imageDescriptor = null;
//        if (definingBundle != null) {
//            imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(definingBundle.getSymbolicName(),
//                    relativeImagePath);
//        }
//        return imageDescriptor;
//    }
//
//    /**
//     * Retrieves an {@link ImageDescriptor} of an image, found in the Bundle with the given
//     * identifier.
//     * 
//     * @param bundleid
//     *            identifier, e.g. de.ivu.fare.rcp.tariff.bundle
//     * @param relativeImagePath
//     *            path of the resource in the passes bundle
//     * @return an image descriptor or null
//     */
//    public static ImageDescriptor getImageDescriptor(String bundleid, String relativeImagePath) {
//        return new URLImageDescriptor(bundleid, relativeImagePath);
//    }
//
//    /**
//     * Retrieves an image out of the passed bundle.
//     * 
//     * @param bundle
//     *            container of the image
//     * @param relativeImagePath
//     *            relative path inside the bundle
//     * @return image if {@link #getImageDescriptor(Bundle, String)} is not null, null otherwise
//     */
//    public static Image getImage(Bundle bundle, String relativeImagePath) {
//        ImageDescriptor descriptor = getImageDescriptor(bundle, relativeImagePath);
//        if (descriptor != null) {
//            return descriptor.createImage();
//        }
//        LOG.error("image {} not available", relativeImagePath);
//        return null;
//    }

}
