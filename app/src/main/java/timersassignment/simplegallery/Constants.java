package timersassignment.simplegallery;

/**
 * Contains App's static values
 */
public class Constants {
    /**
     *
     * TAG_MODE implies the way of managing images.
     * In TAG_MODE, you can just save image id and another informations
     * And When you see images that you entitled, you just Read from disk again
     *
     * If the TAG_MODE is false, you can copy images from disk to your app and managed through
     * app's own database. And even if the image is removed from the disk,
     * your image still available
     *
     *
     */
    public static final boolean TAG_MODE = false;

}
