package imagemodel;

import java.util.Stack;

/**
 * A unified way of accessing the parts of the image model. This interface allows an image and an
 * effect to be loaded and then allows the stored image to be mutated by any sequence of loaded
 * effects. Finally the image data can be retrieved from the model along with information about its
 * width and height.
 */
public class ModelFacade implements ImageModel {

  private Image img;
  private ImageEffect effect;

  private Stack<int[][][]> previousHistory;
  private Stack<int[][][]> subsequentHistory;

  /**
   * Constructs a model with out anything stored yet.
   */
  public ModelFacade() {
    this.img = null;
    this.effect = null;
    this.previousHistory = new Stack<>();
    this.subsequentHistory = new Stack<>();
  }

  /**
   * Sets the image to be the currently worked upon image.
   *
   * @param img an image to work on
   */
  @Override
  public void loadImage(Image img) {
    if (img == null) {
      throw new IllegalArgumentException("Illegal null arg");
    }

    // put old img on the undo stack
    if (this.img != null) {
      this.previousHistory.push(this.img.getDataClone());
    }
    // clear redo stack
    this.subsequentHistory.clear();

    // set new img
    this.img = img;
  }

  /**
   * Sets an ImageEffect to be used on the the current image. This effect will happen when
   * applyEffect is invoked.
   *
   * @param effect an effect to be used on images stored in the model
   */
  @Override
  public void loadEffect(ImageEffect effect) {
    if (effect == null) {
      throw new IllegalArgumentException("Illegal null arg");
    }
    this.effect = effect;
  }

  /**
   * Updates the the stored image with the image produced from applying the stored effect to the
   * currently stored image.
   *
   * @throws IllegalStateException if no img or effect already loaded
   */
  @Override
  public void applyEffect() throws IllegalStateException {
    if (this.img == null || this.effect == null) {
      throw new IllegalStateException("Image and effect must be loaded before applying effect");
    }
    // put old img on the undo stack
    this.previousHistory.push(this.img.getDataClone());
    // clear redo stack
    this.subsequentHistory.clear();

    // update image
    this.img = this.effect.apply(this.img);
  }

  /**
   * Retrieves a copy of the image data of the currently stored image.
   *
   * @return a copy of the image data in the currently stored image
   * @throws IllegalStateException if no img already loaded
   */
  @Override
  public int[][][] outputImage() throws IllegalStateException {
    if (this.img == null) {
      throw new IllegalStateException("Image must be loaded before outputing");
    }
    return this.img.getDataClone();
  }

  /**
   * Retrieves a copy of the pixel width of the currently stored image.
   *
   * @return a copy of the pixel width in the currently stored image
   * @throws IllegalStateException if no img already loaded
   */
  @Override
  public int getWidth() throws IllegalStateException {
    if (this.img == null) {
      throw new IllegalStateException("Image must be loaded before outputing");
    }
    return this.img.getWidth();
  }

  /**
   * Retrieves a copy of the pixel height of the currently stored image.
   *
   * @return a copy of the pixel height in the currently stored image
   * @throws IllegalStateException if no img already loaded
   */
  @Override
  public int getHeight() throws IllegalStateException {
    if (this.img == null) {
      throw new IllegalStateException("Image must be loaded before outputing");
    }
    return this.img.getHeight();
  }

  /**
   * Reverts the image stored in the model to the next to last state. Loaded effect remains
   * unchanged. If model is has no previous state, no change is made and return is False.
   *
   * @return True if success, false if no previous state to revert to
   */
  @Override
  public boolean undo() {
    if (this.previousHistory.empty()) {
      return false;
    }
    // push current state on redo stack
    this.subsequentHistory.push(this.img.getDataClone());

    // restore most recent image from undo stack
    this.img = new RGBArray(this.previousHistory.pop());

    return true;
  }

  /**
   * Restores the image stored in the model to the subsequent state (after some undo). Loaded
   * effect remains unchanged. If model is has no subsequent states (no undos), no change is
   * made and return is False.
   *
   * @return True if success, false if no subsequent state to restore.
   */
  @Override
  public boolean redo() {
    if (this.subsequentHistory.empty()) {
      return false;
    }
    // push current state on undo stack
    this.previousHistory.push(this.img.getDataClone());

    // restore most recent image from redo stack
    this.img = new RGBArray(this.subsequentHistory.pop());

    return true;
  }
}
