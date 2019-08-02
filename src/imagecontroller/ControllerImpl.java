package imagecontroller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Scanner;

import imagemodel.Blur;
import imagemodel.CheckerBoard;
import imagemodel.Dither;
import imagemodel.Greyscale;
import imagemodel.ImageModel;
import imagemodel.Mosaic;
import imagemodel.RGBArray;
import imagemodel.Rainbow;
import imagemodel.Sepia;
import imagemodel.Sharpen;

import static imagecontroller.ImageUtil.intArrayToBufferedImage;

/**
 * A concrete class to control an image model based on text commands. Legal commands are detailed in
 * the readme. The source of the commands is any Readable (defined in the constructor), and the
 * locations to load and output images are defined by an IOHandler object.
 */
public class ControllerImpl implements ImageController, Features {

  private Readable commandsSource;
  private final IOHandler imgIO;
  private final ImageModel model;

  /**
   * Sets up the controller with a source for text commands and the location for loading and
   * outputting images.
   *
   * @param commandsSource the source of text commands
   * @param imgIO          defines the location for loading and outputting images
   * @param model          the model for the controller to manipulate
   * @throws IllegalArgumentException if either arg is null
   */
  public ControllerImpl(Readable commandsSource, IOHandler imgIO, ImageModel model)
          throws IllegalArgumentException {
    if (commandsSource == null || imgIO == null || model == null) {
      throw new IllegalArgumentException("Arguments cannot be null");
    }
    this.commandsSource = commandsSource;
    this.imgIO = imgIO;
    this.model = model;
  }


  /**
   * Sets up the controller with for interactive use. CommandsSource must be specified later using
   * executeScript, before calling start.
   *
   * @param model the model for the controller to manipulate
   * @param imgIO defines the location for loading and outputting images
   */
  public ControllerImpl(IOHandler imgIO, ImageModel model) throws IllegalArgumentException {
    if (imgIO == null || model == null) {
      throw new IllegalArgumentException("Arguments cannot be null");
    }
    this.commandsSource = null; // will be set by execute feature
    this.imgIO = imgIO;
    this.model = model;
  }

  /**
   * Manipulate the model based on commands read from commandsSource.
   *
   * @throws IOException              if IO problems arise while saving or loading images
   * @throws IllegalArgumentException for bad script format, or no commands
   * @throws IllegalStateException    if command source is not set at init or by executeScript
   */
  @Override
  public void start() throws IOException, IllegalArgumentException, IllegalStateException {
    if (this.commandsSource == null) {
      throw new IllegalStateException("commandsSource must be set at init or by executeScript");
    }

    Scanner scan = new Scanner(this.commandsSource);
    boolean isLoadFirst = false;

    // read line by line and parse commands
    while (scan.hasNextLine()) {
      String[] commands = scan.nextLine().split("\\s+");
      if (commands.length == 0) {
        continue;
      }
      String cmd = commands[0].toLowerCase();

      // comments and blank lines
      if (cmd.matches("//\\.*") || cmd.matches("\\s*")) {
        continue;
      }

      // first load command check
      if (!isLoadFirst) {
        if (cmd.equals("load")) {
          isLoadFirst = true;
        } else {
          throw new IllegalArgumentException("Error - Load Must Be the First Command in Script.");
        }
      }

      // load commands
      if (cmd.equals("load")) {
        loadProcessing(model, commands);
      }

      // save command
      else if (cmd.equals("save")) {
        try {
          this.saveToFile(commands[1]);
        } catch (IOException | IndexOutOfBoundsException e) {
          throw new IOException("Image Saving Fail.");
        }
      }

      // commands with no args
      else if (commands.length == 1) {
        cmdProcessing(model, cmd);
      }

      // commands with args
      else {
        cmdProcessing(model, commands);
      }

    }

    // only comments and spaces in script
    if (!isLoadFirst) {
      throw new IllegalArgumentException("Warning: Script has no commands");
    }

  }


  /**
   * A helper to parse and execute commands with no args.
   *
   * @param model the model to be manipulated
   * @param cmd   the name of the command
   * @throws IllegalArgumentException if command unrecognized
   */
  private void cmdProcessing(ImageModel model, String cmd) throws IllegalArgumentException {
    switch (cmd) {
      case "blur":
        this.blur();
        break;
      case "sharpen":
        this.sharpen();
        break;
      case "greyscale":
        this.greyscale();
        break;
      case "sepia":
        this.sepia();
        break;
      case "dither":
        this.dither();
        break;
      default:
        throw new IllegalArgumentException("Command Unrecognizable.");
    }
  }

  /**
   * A helper to parse and execute commands with args.
   *
   * @param model the model to be manipulated
   * @param args  the tokens of the command line starting with the command, followed by args
   * @throws IllegalArgumentException if too many, too few, or wrong type of args, or bad command
   */
  private void cmdProcessing(ImageModel model, String[] args) throws IllegalArgumentException {

    switch (args[0].toLowerCase()) {
      case "mosaic":
        if (args.length != 2) {
          throw new IllegalArgumentException("Mosaic must have exactly 1 argument");
        }
        try {
          this.mosaic(Integer.parseInt(args[1]));
          break;
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
          throw new IllegalArgumentException("Mosaic's Seed Must Be an Int.");
        }
      case "":
        // placeholder for future commands to satisfy java style
        break;
      default:
        throw new IllegalArgumentException("Command Unrecognizable.");
    }
  }

  /**
   * A helper to process loading and generating images.
   *
   * @param model the model to be manipulated
   * @param args  the tokens of the command line starting with the command, followed by args
   * @throws IOException if trouble loading image
   */
  private void loadProcessing(ImageModel model, String[] args) throws IOException,
          IllegalArgumentException {
    if (args.length < 2) {
      throw new IllegalArgumentException("A Load Command Must Have At Least 2 Arguments");
    }

    switch (args[1]) {
      case "rainbow":
        if (args.length != 5) {
          throw new IllegalArgumentException(
                  "Rainbow Command Must Have String Format of [load rainbow *int *int *boolean].");
        }
        try {
          this.loadRainbow(Integer.parseInt(args[2]),
                  Integer.parseInt(args[3]), Boolean.parseBoolean(args[4]));
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
          throw new IllegalArgumentException(
                  "Rainbow Command Must Have String Format of [load rainbow *int *int *boolean].");
        }
        break;
      case "checkerboard":
        if (args.length != 3) {
          throw new IllegalArgumentException("CheckerBoard Must Have Exactly One Argument.");
        }
        try {
          this.loadCheckerBoard(Integer.parseInt(args[2]));
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
          throw new IllegalArgumentException("CheckerBoard Must Have a (*int) size Argument.");
        }
        break;
      default:
        if (args.length != 2) {
          throw new IllegalArgumentException("Load From File Must Have Exactly 1 Argument");
        }
        this.loadPhoto(args[1]);
    }
  }

  /**
   * Loads the image at the given filename into the controller's model.
   *
   * @param filename the location of the image to load
   */
  @Override
  public void loadPhoto(String filename) throws IOException {
    if (filename == null) {
      throw new IllegalArgumentException("Illegal null arg");
    }
    model.loadImage(new RGBArray(imgIO.input(filename)));
  }

  /**
   * Loads a generated rainbow image into the controller's model.
   *
   * @param width        width of image to generate in pixels
   * @param height       height of image to generate in pixels
   * @param isHorizontal True for horizontal stripes, else vertical stripes
   */
  @Override
  public void loadRainbow(int width, int height, boolean isHorizontal) {
    model.loadImage(new Rainbow(width, height, isHorizontal));
  }

  /**
   * Loads a generated checker board image into the controller's model.
   *
   * @param tileSize the side length of tiles for the generated image in pixels.
   */
  @Override
  public void loadCheckerBoard(int tileSize) {
    model.loadImage(new CheckerBoard(tileSize));
  }

  /**
   * Saves the current state of the image in the controller's model to the specified file location.
   *
   * @param filename the location to save the image
   * @throws IOException           if any issue writing to given filename
   * @throws IllegalStateException if save before load
   */
  @Override
  public void saveToFile(String filename) throws IOException, IllegalStateException {
    if (filename == null) {
      throw new IllegalArgumentException("Illegal null arg");
    }
    imgIO.output(model.outputImage(), model.getWidth(), model.getHeight(), filename);
  }

  /**
   * Applies the blur effect to the image loaded in the controller's model.
   */
  @Override
  public void blur() {
    model.loadEffect(new Blur());
    model.applyEffect();
  }

  /**
   * Applies the sharpen effect to the image loaded in the controller's model.
   */
  @Override
  public void sharpen() {
    model.loadEffect(new Sharpen());
    model.applyEffect();
  }

  /**
   * Applies the greyscale effect to the image loaded in the controller's model.
   */
  @Override
  public void greyscale() {
    model.loadEffect(new Greyscale());
    model.applyEffect();
  }

  /**
   * Applies the sepia effect to the image loaded in the controller's model.
   */
  @Override
  public void sepia() {
    model.loadEffect(new Sepia());
    model.applyEffect();
  }

  /**
   * Applies the dither effect to the image loaded in the controller's model.
   */
  @Override
  public void dither() {
    model.loadEffect(new Dither());
    model.applyEffect();
  }

  /**
   * Applies the Mosaic effect to the image loaded in the controller's model.
   *
   * @param seeds number of panes to generate in mosaic
   */
  @Override
  public void mosaic(int seeds) {
    model.loadEffect(new Mosaic(seeds));
    model.applyEffect();
  }

  /**
   * reverts the controller's model to the image prior to the most recent effect.
   *
   * @throws IllegalStateException if no changes yet to undo
   */
  @Override
  public void undo() {
    if (!this.model.undo()) {
      throw new IllegalStateException("No changes yet to undo");
    }
  }


  @Override
  public void redo() {
    if (!this.model.redo()) {
      throw new IllegalStateException("No undos yet to restore");
    }
  }

  /**
   * Retrieves a copy of the image data stored in the controller's model image.
   *
   * @return a copy of the image data stored in the controller's model image
   */
  @Override
  public BufferedImage outputImage() {
    return intArrayToBufferedImage(model.outputImage(), model.getWidth(), model.getHeight());
  }

  /**
   * Runs the batch script commands provided in the given readable.
   *
   * @throws IOException              if any issues with accessing the Readable
   * @throws IllegalArgumentException if batch script has bad format, null args
   */
  @Override
  public void executeScript(Readable script) throws IOException, IllegalArgumentException {
    if (script == null) {
      throw new IllegalArgumentException("Illegal null arg");
    }
    this.commandsSource = script;
    this.start();
  }
}