package imagecontroller;

import java.io.IOException;

/**
 * An interface for controlling an ImageModel. Coordinates the loading and outputing of images and
 * application of image effects based on some command source.
 */
public interface ImageController {
  /**
   * Once invoked begins to manipulate the model based on a command source defined in the
   * implementation, until commands are exhausted.
   *
   * @throws IOException if IO problems arise while saving or loading images
   */
  void start() throws IOException;
}
