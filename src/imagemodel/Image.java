package imagemodel;

/**
 * An interface for an image including all methods an Image class should support.
 */
public interface Image {
  /**
   * Return the pixel value located at the given (x, y) and channel within this Image.
   *
   * @param x       the given x coordinate
   * @param y       the given y coordinate
   * @param channel the given channel
   * @return the pixel value of the given chanel at given (x, y)
   * @throws IllegalArgumentException if given location is not in bounds
   */
  int getVal(int x, int y, int channel) throws IllegalArgumentException;

  /**
   * Return a copy of this Image in int[][][] 3 channel RGB value format.
   *
   * @return an int[][][] representation of this Image
   */
  int[][][] getDataClone();

  /**
   * Return the width of this Image.
   *
   * @return width
   */
  int getWidth();

  /**
   * Return the height of this Image.
   *
   * @return height
   */
  int getHeight();

  /**
   * A helper method that checks if the given (x, y) is inside of this Image.
   *
   * @param x the given x coordinate
   * @param y the given y coordinate
   * @return true if (x, y) is a valid location inside of this Image; false otherwise
   */
  boolean isValidLocation(int x, int y);
}
