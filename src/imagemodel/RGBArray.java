package imagemodel;

/**
 * A class that stores an image formatted in 3 channels with values between 255 and 0. The
 * constructor will clampAndValidateDimConsistency any values that are outside of those bounds.
 */
public class RGBArray implements Image {
  /**
   * The 3D Integer Array that represents this RGBArray.
   */
  protected final int[][][] data;

  /**
   * The Maximum and Minimum a pixel value can be.
   */
  protected static final int MAX_VAL = 255;
  protected static final int MIN_VAL = 0;

  /**
   * The number of channels this RGBArray has.
   */
  protected static final int NUM_CHANNELS = 3;

  /**
   * Constructs an a new object with validated and clamped values.
   *
   * @param data all of the pixel values for all of the channels
   * @throws IllegalArgumentException for null, empty, malformed data, or wrong number of channels
   */
  public RGBArray(int[][][] data) throws IllegalArgumentException {
    if (data == null) {
      throw new IllegalArgumentException("argument cannot be null");
    }
    if (!(data.length != 0 && data[0].length != 0)) {
      throw new IllegalArgumentException("data must have nonzero dimensions");
    }
    this.data = this.clampAndValidateDimConsistency(data);
  }

  /**
   * Return the pixel value located at the given (x, y) and channel within this Image.
   *
   * @param x       the given x coordinate
   * @param y       the given y coordinate
   * @param channel the given channel
   * @return the pixel value of the given chanel at given (x, y)
   * @throws IllegalArgumentException if given location is not in bounds
   */
  @Override
  public int getVal(int x, int y, int channel) throws IllegalArgumentException {
    try {
      return this.data[y][x][channel];
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new IllegalArgumentException("location out of bounds");
    }
  }

  /**
   * Return a copy of this Image in int[][][] 3 channel RGB value format.
   *
   * @return an int[][][] representation of this Image
   */
  @Override
  public int[][][] getDataClone() {
    int w = this.getWidth();
    int h = this.getHeight();
    int[][][] output = new int[h][w][NUM_CHANNELS];
    for (int y = 0; y < h; ++y) {
      for (int x = 0; x < w; ++x) {
        for (int i = 0; i < 3; ++i) {
          output[y][x][i] = this.getVal(x, y, i);
        }
      }
    }
    return output;
  }

  /**
   * Return the width of this Image.
   *
   * @return width
   */
  @Override
  public int getWidth() {
    return this.data[0].length;
  }

  /**
   * Return the height of this Image.
   *
   * @return height
   */
  @Override
  public int getHeight() {
    return this.data.length;
  }

  /**
   * A helper method that checks if the given (x, y) is inside of this Image.
   *
   * @param x the given x coordinate
   * @param y the given y coordinate
   * @return true if (x, y) is a valid location inside of this Image; false otherwise
   */
  @Override
  public boolean isValidLocation(int x, int y) {
    return x >= 0 && x < this.getWidth() && y >= 0 && y < this.getHeight();
  }

  /**
   * Floors values beyond MAX_VAL and ceilings values below MIN_VAL. For optimization it
   * also validates dimension consistency while iterating.
   *
   * @param data all of the pixel values for all of the channels
   * @return the input data with out of bound values clamped to MAX_VAL and MIN_VAL
   * @throws IllegalArgumentException if data has inconsistent dimensions
   */
  private int[][][] clampAndValidateDimConsistency(int[][][] data)
          throws IllegalArgumentException {
    for (int y = 0; y < data.length; ++y) {
      if (data[y].length != data[0].length) {
        throw new IllegalArgumentException("data has inconsistent dimensions");
      }
      for (int x = 0; x < data[0].length; ++x) {
        if (data[y][x].length != NUM_CHANNELS) {
          throw new IllegalArgumentException("Error: Invalid Number of Channels.");
        }
        for (int channel = 0; channel < NUM_CHANNELS; ++channel) {
          if (data[y][x][channel] < MIN_VAL) {
            data[y][x][channel] = MIN_VAL;
          }
          if (data[y][x][channel] >= MAX_VAL) {
            data[y][x][channel] = MAX_VAL;
          }
        }
      }
    }
    return data;
  }
}
