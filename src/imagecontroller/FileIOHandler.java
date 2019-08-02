package imagecontroller;

import java.io.IOException;

/**
 * A class that manages IO for images located in files. Used by an ImageController.
 */
public class FileIOHandler implements IOHandler {

  private final String baseDirectory;

  /**
   * Constructs with a specified base directory.
   */
  public FileIOHandler(String baseDirectory) {
    if (baseDirectory == null) {
      throw new IllegalArgumentException("Arg cannot be null");
    }
    this.baseDirectory = baseDirectory;
  }

  /**
   * Gets image data from the named file.
   *
   * @param name source file name
   * @return image data in 3 channel RGB format
   * @throws IOException if any issue accessing the source file
   */
  @Override
  public int[][][] input(String name) throws IOException {
    return ImageUtil.readImage(this.baseDirectory + name);
  }

  /**
   * Writes image data to the named output file.
   *
   * @param imgData image data in 3 channel RGB format
   * @param width   image data width in pixels
   * @param height  image data height in pixels
   * @param name    output file name
   * @throws IOException if any issue accessing output file
   */
  @Override
  public void output(int[][][] imgData, int width, int height, String name) throws IOException {
    ImageUtil.writeImage(imgData, width, height, this.baseDirectory + name);
  }
}
