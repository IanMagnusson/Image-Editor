package imagecontroller;

import java.io.IOException;

/**
 * An interface for managing the location to load and output images. Allows IO redirection.
 */
public interface IOHandler {
  /**
   * Gets image data from the named source.
   *
   * @param name source name
   * @return image data in 3 channel RGB format
   * @throws IOException if any issue accessing the source
   */
  int[][][] input(String name) throws IOException;

  /**
   * Writes image data to the named output.
   *
   * @param imgData image data in 3 channel RGB format
   * @param width   image data width in pixels
   * @param height  image data height in pixels
   * @param name    output name
   * @throws IOException if any issue accessing output
   */
  void output(int[][][] imgData, int width, int height, String name) throws IOException;
}
