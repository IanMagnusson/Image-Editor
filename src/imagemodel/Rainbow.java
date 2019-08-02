package imagemodel;

import javafx.scene.paint.Color;

import static java.lang.Math.ceil;

/**
 * A class that generates rainbow images. Rainbows can be horizontal or vertical, colors are fixed.
 */
public class Rainbow extends RGBArray {

  private static final Color[] rainbowColors = {
    Color.RED, Color.ORANGE, Color.YELLOW,
    Color.GREEN, Color.BLUE, Color.INDIGO, Color.VIOLET};

  /**
   * Generates a rainbow with 7 color stripes. Dimensions of whole image are specified. Final color
   * stripe will be up to 7 pixels less to accommodate dimensions that are not multiples of 7.
   *
   * @param width        width in pixels of the whole image
   * @param height       height in pixels of the whole image
   * @param isHorizontal True if horizontal stripes desired, false for vertical
   */
  public Rainbow(int width, int height, boolean isHorizontal) {
    super(new int[height][width][NUM_CHANNELS]);

    // width or thickness of each stripe
    int stripeDim = (int) ceil((isHorizontal ? height : width) / (double) rainbowColors.length);

    // iterate image
    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        // draw horizontal or vertical stripes
        int dimLoc = (isHorizontal) ? y : x;
        this.data[y][x][0] = (int) (rainbowColors[dimLoc / stripeDim].getRed() * MAX_VAL);
        this.data[y][x][1] = (int) (rainbowColors[dimLoc / stripeDim].getGreen() * MAX_VAL);
        this.data[y][x][2] = (int) (rainbowColors[dimLoc / stripeDim].getBlue() * MAX_VAL);
      }
    }
  }
}
