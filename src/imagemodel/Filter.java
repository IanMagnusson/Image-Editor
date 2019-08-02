package imagemodel;

/**
 * Filter is an ImageEffect that can filter the pixel of an Image with pre-designed kernel.
 * Filtering modifies the value of a pixel depending on the values of its neighbors.
 */
public class Filter implements ImageEffect {
  /**
   * A 2D double array that can be applied to the pixel of an Image.
   * Kernel should always have odd width and length.
   */
  private final double[][] kernel;

  /**
   * The number of channels this filter has.
   */
  private static final int NUM_CHANNELS = 3;

  /**
   * The Default Constructor of a Filter that takes a 2D double array as the kernel.
   *
   * @param kernel the given 2D double array
   * @throws IllegalArgumentException if kernel is empty or length is not odd
   *                                  or the dimension is not consistent
   */
  public Filter(double[][] kernel) throws IllegalArgumentException {
    if (kernel == null) {
      throw new IllegalArgumentException("kernel can't be null");
    }
    if (!(kernel.length != 0 && kernel[0].length != 0)) {
      throw new IllegalArgumentException("kernel must have nonzero dimensions");
    }
    if (kernel.length % 2 == 0 || kernel[0].length % 2 == 0) {
      throw new IllegalArgumentException("kernel must have odd dimensions");
    }
    this.kernel = kernel;
    if (!areKernelDimensionsConsistent()) {
      throw new IllegalArgumentException("kernel dimensions must be consistent");
    }
  }

  /**
   * Apply this Filter to an Image.
   *
   * @param input the given Image
   * @return a new Image after applying this filter
   */
  @Override
  public Image apply(Image input) {
    int w = input.getWidth();
    int h = input.getHeight();
    int[][][] output = new int[h][w][NUM_CHANNELS];

    for (int y = 0; y < h; ++y) {
      for (int x = 0; x < w; ++x) {
        for (int channel = 0; channel < NUM_CHANNELS; ++channel) {
          output[y][x][channel] = this.applyKernel(input, x, y, channel);
        }
      }
    }

    return new RGBArray(output);
  }

  /**
   * An apply helper method that is used to calculate a new pixel value
   * with the given (x, y) and channel of an Image and
   * the kernel of this Filter.
   * If some portions of the kernel do not overlap any pixels,
   * those pixels are not included in the computation.
   *
   * @param input   the given Image
   * @param x       the give  x coordinate
   * @param y       the given y coordinate
   * @param channel the given channel
   * @return a new pixel value after applying the filter
   */
  private int applyKernel(Image input, int x, int y, int channel) {
    double sum = 0.0;
    // calculate location on original image corresponding to top left corner of kernel
    int imgCornerX = x - (this.getWidth() / 2);
    int imgCornerY = y - (this.getHeight() / 2);

    // for each value in the kernel
    for (int kernY = 0; kernY < this.getHeight(); ++kernY) {
      for (int kernX = 0; kernX < this.getWidth(); ++kernX) {
        // compute the equivalent location in the image
        int imgX = imgCornerX + kernX;
        int imgY = imgCornerY + kernY;
        // if that location is within the image bounds, multiply kernel and image val, and sum
        if (input.isValidLocation(imgX, imgY)) {
          sum += input.getVal(imgX, imgY, channel) * this.kernel[kernY][kernX];
        }
      }
    }
    return (int) sum;
  }

  /**
   * Return the width of this Filter's kernel.
   *
   * @return width of kernel
   */
  private int getWidth() {
    return this.kernel[0].length;
  }

  /**
   * Return the height of this Filter's kernel.
   *
   * @return height of kernel
   */
  private int getHeight() {
    return this.kernel.length;
  }

  /**
   * Check whether the kernel dimension is consistent.
   *
   * @return true if consistent; false otherwise
   */
  private boolean areKernelDimensionsConsistent() {
    for (double[] row : this.kernel) {
      if (row.length != this.getWidth()) {
        return false;
      }
    }
    return true;
  }
}
