package imagemodel;

/**
 * A unified way of accessing the parts of the image model. This interface allows an image and an
 * effect to be loaded and then allows the stored image to be mutated by any sequence of loaded
 * effects. Finally the image data can be retrieved from the model along with information about its
 * width and height.
 */
public interface ImageModel {
  /**
   * Sets the image to be the currently worked upon image.
   *
   * @param img an image to work on
   */
  void loadImage(Image img);

  /**
   * Sets an ImageEffect to be used on the the current image. This effect will happen when
   * applyEffect is invoked.
   *
   * @param effect an effect to be used on images stored in the model
   */
  void loadEffect(ImageEffect effect);

  /**
   * Updates the the stored image with the image produced from applying the stored effect to the
   * currently stored image.
   */
  void applyEffect();

  /**
   * Retrieves a copy of the image data of the currently stored image.
   *
   * @return a copy of the image data in the currently stored image
   */
  int[][][] outputImage();

  /**
   * Retrieves a copy of the pixel width of the currently stored image.
   *
   * @return a copy of the pixel width in the currently stored image
   */
  int getWidth();

  /**
   * Retrieves a copy of the pixel height of the currently stored image.
   *
   * @return a copy of the pixel height in the currently stored image
   */
  int getHeight();

  /**
   * Reverts the image stored in the model to the next to last state. Loaded effect remains
   * unchanged. If model is has no previous state, no change is made and return is False.
   * @return True if success, false if no previous state to revert to
   */
  boolean undo();

  /**
   * Restores the image stored in the model to the subsequent state (after some undo). Loaded
   * effect remains unchanged. If model is has no subsequent states (no undos), no change is
   * made and return is False.
   * @return True if success, false if no subsequent state to restore.
   */
  boolean redo();
}
