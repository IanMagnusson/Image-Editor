package imagemodel;

/**
 * A Sharpen Filter class can sharpen an Image.
 * A Sharpen Filter can be applied to the same Image
 * for more than one time.
 */
public class Sharpen extends Filter {
  /**
   * The Default Constructor of a Sharpen class.
   * A Sharpen's kernel is pre-defined.
   */
  public Sharpen() {
    super(new double[][]{
            {-0.125, -0.125, -0.125, -0.125, -0.125},
            {-0.125, 0.25, 0.25, 0.25, -0.125},
            {-0.125, 0.25, 1.0, 0.25, -0.125},
            {-0.125, 0.25, 0.25, 0.25, -0.125},
            {-0.125, -0.125, -0.125, -0.125, -0.125}});
  }
}
