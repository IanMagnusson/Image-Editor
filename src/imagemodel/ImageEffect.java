package imagemodel;

/**
 * An Interface contains all methods an ImageEffect should support.
 * An ImageEffect is either a color filtering or a color transformation.
 */
public interface ImageEffect {
  /**
   * Apply this ImageEffect on an Image.
   *
   * @param input the given Image
   * @return an Image output after applying this ImageEffect
   */
  Image apply(Image input);
}
