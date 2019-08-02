package imagecontroller;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * The Features Interface expose the behaviors provided by the Controller to the View.
 */
public interface Features {
  /**
   * Loads the image at the given filename into the controller's model.
   *
   * @param filename the location of the image to load
   * @throws IOException if the provided file path is invalid
   */
  void loadPhoto(String filename) throws IOException;

  /**
   * Loads a generated rainbow image into the controller's model.
   *
   * @param width        width of image to generate in pixels
   * @param height       height of image to generate in pixels
   * @param isHorizontal True for horizontal stripes, else vertical stripes
   */
  void loadRainbow(int width, int height, boolean isHorizontal);

  /**
   * Loads a generated checker board image into the controller's model.
   *
   * @param tileSize the side length of tiles for the generated image in pixels.
   */
  void loadCheckerBoard(int tileSize);

  /**
   * Saves the current state of the image in the controller's model to the specified file location.
   *
   * @param filename the location to save the image
   * @throws IOException if the provided file path is invalid
   */
  void saveToFile(String filename) throws IOException;

  /**
   * Applies the blur effect to the image loaded in the controller's model.
   */
  void blur();

  /**
   * Applies the sharpen effect to the image loaded in the controller's model.
   */
  void sharpen();

  /**
   * Applies the greyscale effect to the image loaded in the controller's model.
   */
  void greyscale();

  /**
   * Applies the sepia effect to the image loaded in the controller's model.
   */
  void sepia();

  /**
   * Applies the dither effect to the image loaded in the controller's model.
   */
  void dither();

  /**
   * Applies the Mosaic effect to the image loaded in the controller's model.
   *
   * @param seeds number of panes to generate in mosaic
   */
  void mosaic(int seeds);

  /**
   * reverts the controller's model to the image prior to the most recent effect.
   *
   * @throws IllegalStateException if no changes yet to undo
   */
  void undo();

  /**
   * reverts the controller's model to the image after the most recent effect.
   *
   * @throws IllegalStateException if no undos yet to restore
   */
  void redo();

  /**
   * Retrieves a copy of the image data stored in the controller's model image.
   *
   * @return a copy of the image data stored in the controller's model image
   */
  BufferedImage outputImage();

  /**
   * Runs the batch script commands provided in the given readable.
   *
   * @throws IOException if any issues with accessing the Readable
   */
  void executeScript(Readable script) throws IOException;
}