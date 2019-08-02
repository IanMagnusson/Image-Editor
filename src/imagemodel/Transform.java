package imagemodel;

/**
 * Transform is an ImageEffect
 * that can transform the color of an Image with its matrix.
 * A color transformation modifies the color of a pixel based on its own color.
 */
public class Transform implements ImageEffect {

  /**
   * The 2D double array matrix that modifies a pixel value by matrix multiplication.
   * A matrix's length and width must be equal to the number of channels.
   */
  private final double[][] matrix;

  /**
   * The number of channels this Transform has.
   */
  private static final int NUM_CHANNELS = 3;

  /**
   * The Default Constructor for a Transform
   * that takes a 2D double array input as matrix.
   *
   * @param matrix the given 2D double array matrix
   * @throws IllegalArgumentException if matrix is not in format
   */
  public Transform(double[][] matrix) throws IllegalArgumentException {
    if (matrix == null) {
      throw new IllegalArgumentException("matrix can't be null");
    }
    this.matrix = matrix;
    if (!this.validMatrix()) {
      throw new IllegalArgumentException("matrix must be " + NUM_CHANNELS + " by " + NUM_CHANNELS);
    }
  }

  /**
   * Apply this Transform to an Image.
   *
   * @param input the given Image
   * @return a new Image after applying this Transform
   */
  @Override
  public Image apply(Image input) {
    int w = input.getWidth();
    int h = input.getHeight();
    int[][][] output = new int[h][w][NUM_CHANNELS];
    for (int y = 0; y < h; ++y) {
      for (int x = 0; x < w; ++x) {
        for (int channel = 0; channel < NUM_CHANNELS; ++channel) {
          int new_val = 0;
          for (int oldChannel = 0; oldChannel < NUM_CHANNELS; ++oldChannel) {
            new_val += input.getVal(x, y, oldChannel) * this.matrix[channel][oldChannel];
          }
          output[y][x][channel] = new_val;
        }
      }
    }
    return new RGBArray(output);
  }

  /**
   * Validate the matrix dimension.
   *
   * @return true if matrix dimension equals to the number of channels; false otherwise
   */
  private boolean validMatrix() {
    if (this.matrix.length != NUM_CHANNELS) {
      return false;
    }
    for (double[] row : this.matrix) {
      if (row.length != NUM_CHANNELS) {
        return false;
      }
    }
    return true;
  }
}
