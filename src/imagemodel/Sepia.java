package imagemodel;

/**
 * An ImageEffect that can transform a color image into a sepia tone image.
 */
public class Sepia extends Transform {
  /**
   * The Default Constructor for a Sepia ImageEffect.
   * The standard formula to compute the sepia tone is manually provided
   */
  public Sepia() {
    super(new double[][]{
            {0.393, 0.769, 0.189},
            {0.349, 0.686, 0.168},
            {0.272, 0.534, 0.131}});
  }
}
