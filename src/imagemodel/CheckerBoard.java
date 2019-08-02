package imagemodel;

/**
 * A CheckerBoard class representing an Image of a checker board.
 * A CheckerBoard class utilizes RGBArray as its default constructor.
 */
public class CheckerBoard extends RGBArray {

  /**
   * The size of this CheckerBoard.
   */
  private static final int tilesPerSide = 8;

  /**
   * Generates a CheckerBoard of 8x8 tiles. The tile size is specified by args.
   *
   * @param size width and height of a tile in pixels
   */
  public CheckerBoard(int size) {
    super(new int[size * tilesPerSide][size * tilesPerSide][3]);
    for (int y = 0; y < this.getHeight(); ++y) {
      for (int x = 0; x < this.getWidth(); ++x) {
        for (int channel = 0; channel < 3; ++channel) {
          this.data[y][x][channel] = ((x / size + y / size) % 2 == 1) ? 0 : 255;
        }
      }
    }
  }
}
