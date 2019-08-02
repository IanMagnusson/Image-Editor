import java.io.FileReader;
import java.io.IOException;

import imagecontroller.ControllerImpl;
import imagecontroller.Features;
import imagecontroller.ImageController;
import imagecontroller.FileIOHandler;
import imagemodel.ImageModel;
import imagemodel.ModelFacade;
import imageview.ImageView;

/**
 * The Driver for an ImageModel.
 * Run the Driver to activate the Controller.
 */
public class Driver {
  /**
   * input.txt should be located in the outer root folder with src/ and res/ with default ""
   * whereas source images should be located in res/ folder with default "res/".
   */
  private static final String ROOT_DIR = ""; // to modify the base directory
  private static final String IMG_DIR = ROOT_DIR + "res/"; // to modify the image directory

  /**
   * The main() method to start the Image controller.
   *
   * @param args argument String input
   * @throws IOException if FileReader fail
   */
  public static void main(String[] args) throws IOException {
    ImageModel model = new ModelFacade();
    ImageController controller;

    // interactive mode
    if (args.length == 1 && args[0].equals("-interactive")) {
      controller = new ControllerImpl(new FileIOHandler(""), model);
      ImageView view = new ImageView("Image Editor", (Features) controller);

    }
    // batch script mode
    else if (args.length == 2 && args[0].equals("-script")) {
      try {
        controller = new ControllerImpl(
                new FileReader(ROOT_DIR + args[1]),
                new FileIOHandler(IMG_DIR), model);
      } catch (IndexOutOfBoundsException | IOException e) {
        throw new IOException("File Reading Fail - Check File Directory.");
      }
      controller.start();
    }
    // bad usage
    else {
      throw new IllegalArgumentException("Invalid command line args");
    }
  }
}