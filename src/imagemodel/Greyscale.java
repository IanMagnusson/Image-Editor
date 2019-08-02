package imagemodel;

/**
 * An ImageEffect that can transform a color image into a greyscale image.
 */
public class Greyscale extends Transform {

  /**
   * The Default Constructor for a Greyscale ImageEffect.
   * The standard formula to compute the “luma” is manually provided.
   */
  public Greyscale() {
    super(new double[][]{
            {0.2126, 0.7152, 0.0722},
            {0.2126, 0.7152, 0.0722},
            {0.2126, 0.7152, 0.0722}});
  }
}
