package imagecontroller;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * This class contains utility methods to read an image from file and write to a file.
 */
public class ImageUtil {

  /**
   * Read an image file and return the contents as an array.
   *
   * @param filename the path of the file. Look at the IOHandler documentation to see which file
   *                 formats are supported.
   * @return the image as a 3D array of integer values
   * @throws IOException              if any problems reading file
   * @throws IllegalArgumentException if null filename
   */
  public static int[][][] readImage(String filename) throws IOException,
          IllegalArgumentException {
    if (filename == null) {
      throw new IllegalArgumentException("filename cannot be null");
    }
    BufferedImage input;

    input = ImageIO.read(new FileInputStream(filename));

    int[][][] result = new int[input.getHeight()][input.getWidth()][3];

    for (int i = 0; i < input.getHeight(); i++) {
      for (int j = 0; j < input.getWidth(); j++) {
        int color = input.getRGB(j, i);
        Color c = new Color(color);
        result[i][j][0] = c.getRed();
        result[i][j][1] = c.getGreen();
        result[i][j][2] = c.getBlue();
      }
    }
    return result;
  }

  /**
   * Convenience function to get the width of an image.
   *
   * @param filename the full path of the image file. Look at the IOHandler class to see which file
   *                 formats are supported
   * @return the width of the file
   * @throws IOException if the file is not found
   */

  public static int getWidth(String filename) throws IOException {
    BufferedImage input;

    input = ImageIO.read(new FileInputStream(filename));

    return input.getWidth();
  }

  /**
   * Convenience function to get the height of an image.
   *
   * @param filename the full path of the image file. Look at the IOHandler class to see which file
   *                 formats are supported
   * @return the height of the file
   * @throws IOException if the file is not found
   */
  public static int getHeight(String filename) throws IOException {
    BufferedImage input;

    input = ImageIO.read(new FileInputStream(filename));

    return input.getHeight();
  }

  /**
   * Write an image to a file in a given format.
   *
   * @param rgb      the image data as a 3D array of integers. The dimensions are row, col and
   *                 channel respectively
   * @param width    the width of the image
   * @param height   the height of the image
   * @param filename the full path of where the image must be stored. This should include the name
   *                 and extension of the file
   * @throws IOException if the file cannot be written to the provided path
   */
  public static void writeImage(int[][][] rgb, int width, int height, String
          filename)
          throws IOException {

    BufferedImage output = intArrayToBufferedImage(rgb, width, height);
    String extension = filename.substring(filename.indexOf(".") + 1);
    ImageIO.write(output, extension, new FileOutputStream(filename));
  }

  /**
   * Convert a 3D array of integers into a BufferedImage.
   *
   * @param rgb    the image data as a 3D array of integers. The dimensions *                 are
   *               row, col and channel respectively
   * @param width  the width of the image
   * @param height the height of the image
   */
  public static BufferedImage intArrayToBufferedImage(int[][][] rgb, int width, int height) {
    BufferedImage output = new BufferedImage(
            width,
            height,
            BufferedImage.TYPE_INT_RGB);

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int r = rgb[i][j][0];
        int g = rgb[i][j][1];
        int b = rgb[i][j][2];

        //color is stored in 1 integer, with the 4 bytes storing ARGB in that
        //order. Each of r,g,b are stored in 8 bits (hence between 0 and 255).
        // So we put them all in one integer by using bit-shifting << as below
        int color = (r << 16) + (g << 8) + b;
        output.setRGB(j, i, color);
      }
    }

    return output;
  }
}