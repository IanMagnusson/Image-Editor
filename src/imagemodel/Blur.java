package imagemodel;

/**
 * A Blur Filter class that can blur an Image.
 * A Blur Filter can be applied to the same Image for more than one time.
 */
public class Blur extends Filter {
  /**
   * The Default Constructor of a Blur class.
   * A Blur's kernel is pre-defined.
   */
  public Blur() {
    super(new double[][]{
            {0.0625, 0.125, 0.0625},
            {0.125, 0.25, 0.125},
            {0.0625, 0.125, 0.0625}});
  }
}
