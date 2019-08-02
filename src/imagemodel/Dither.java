package imagemodel;

import static java.lang.Math.round;

/**
 * A Dither ImageEffect that compresses pixel values to black and white only. Object stores no data.
 */
public class Dither implements ImageEffect {
  /**
   * Apply this Dither ImageEffect to an Image.
   *
   * @param input the given Image
   * @return an Image that has been dithered by this Dither object
   */
  @Override
  public Image apply(Image input) {
    // greyscale first
    // error will be handled by Greyscale() constructor
    Image greyImg = new Greyscale().apply(input);
    int[][][] output = greyImg.getDataClone();
    int w = greyImg.getWidth();
    int h = greyImg.getHeight();
    for (int y = 0; y < h; ++y) {
      for (int x = 0; x < w; ++x) {
        int oldPix = output[y][x][0];
        int newPix = findClosestPaletteColor(oldPix);
        int error = oldPix - newPix;
        // update the current pixel
        output[y][x][0] = newPix;
        output[y][x][1] = newPix;
        output[y][x][2] = newPix;
        // update the right pixel if exists
        if (x + 1 < w) {
          addRGBVal(output, x + 1, y, error * 7 / 16);
        }
        // update the bottom left, bottom, and bottom right pixels if exist
        if (y + 1 < h) {
          addRGBVal(output, x, y + 1, error * 5 / 16);
          if (x - 1 >= 0) {
            addRGBVal(output, x - 1, y + 1, error * 3 / 16);
          }
          if (x + 1 < w) {
            addRGBVal(output, x + 1, y + 1, error / 16);
          }
        }
      }
    }
    return new RGBArray(output);
  }

  /**
   * Return the closest palette color (either 0 or 255) to the pixel value.
   *
   * @param oldPix the given pixel value
   * @return 0 or 255, whichever is closer to oldPix
   */
  private int findClosestPaletteColor(int oldPix) {
    return round(oldPix / 255) * 255;
  }

  /**
   * Add the value to all three channels of the (x, y) pixel in the matrix.
   *
   * @param matrix the given matrix
   * @param x      the given x coordinate
   * @param y      the given y coordinate
   * @param val    the value to be added
   */
  private void addRGBVal(int[][][] matrix, int x, int y, int val) {
    matrix[y][x][0] += val;
    matrix[y][x][1] += val;
    matrix[y][x][2] += val;
  }
}
