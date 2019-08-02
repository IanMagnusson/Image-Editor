import org.junit.Before;
import org.junit.Test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringReader;

import imagecontroller.ControllerImpl;
import imagecontroller.Features;
import imagecontroller.ImageController;
import imagecontroller.FileIOHandler;
import imagecontroller.IOHandler;
import imagemodel.Image;
import imagemodel.ImageEffect;
import imagemodel.ImageModel;
import imagemodel.ModelFacade;
import imagemodel.RGBArray;

import static java.lang.Math.abs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * A test suite for the controller.
 */
public class ImageTest {
  private final static String ROOT_DIR = "";
  private final static String IMG_DIR = ROOT_DIR + "test/";
  ImageController controller;
  Features features;
  MockModel defaultMock;
  StringBuilder mockLog;
  ImageModel model;
  IOHandler imgDefaultIO;


  private class MockModel implements ImageModel {

    private StringBuilder log;
    private int[][][] output;
    private boolean redoUndoVal;

    public MockModel(StringBuilder log, int[][][] output, boolean redoUndoVal) {
      this.log = log;
      this.output = output;
      this.redoUndoVal = redoUndoVal;
    }

    @Override
    public void loadImage(Image img) {
      log.append("loadImage " + img.getClass().getName() + "\n");
    }

    @Override
    public void loadEffect(ImageEffect effect) {
      log.append("loadEffect " + effect.getClass().getName() + "\n");
    }

    @Override
    public void applyEffect() {
      log.append("applyEffect\n");
    }

    @Override
    public int[][][] outputImage() {
      log.append("outputImage\n");
      return output;
    }

    @Override
    public int getWidth() {
      log.append("getWidth\n");
      return output[0].length;
    }

    @Override
    public int getHeight() {
      log.append("getHeight\n");
      return output.length;
    }

    @Override
    public boolean undo() {
      log.append("undo\n");
      return redoUndoVal;
    }

    @Override
    public boolean redo() {
      log.append("redo\n");
      return redoUndoVal;
    }
  }

  @Before
  public void setup() {
    imgDefaultIO = new FileIOHandler(IMG_DIR);
    mockLog = new StringBuilder();
    defaultMock = new MockModel(mockLog, new int[3][3][3], true);
    features = new ControllerImpl(imgDefaultIO, defaultMock);
  }


  // feature tests

  // invalids
  @Test(expected = IllegalArgumentException.class)
  public void nullLoad() {
    try {
      features.loadPhoto(null);
    } catch (IOException e) {
      fail("shouldn't throw this exception");
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullSave() {
    try {
      features.saveToFile(null);
    } catch (IOException e) {
      fail("shouldn't throw this exception");
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullExecuteScript() {
    try {
      features.executeScript(null);
    } catch (IOException e) {
      fail("shouldn't throw this exception");
    }
  }

  @Test(expected = IllegalStateException.class)
  public void illegalUndo() {
    features = new ControllerImpl(imgDefaultIO,
            new MockModel(mockLog, new int[3][3][3], false));
    features.undo();
  }

  @Test(expected = IllegalStateException.class)
  public void illegalRedo() {
    features = new ControllerImpl(imgDefaultIO,
            new MockModel(mockLog, new int[3][3][3], false));
    features.redo();
  }

  // valids

  @Test
  public void loadPhoto() {
    try {
      features.loadPhoto("manhattan-small.png");
    } catch (IOException e) {
      fail("shouldn't throw exception");
    }

    assertEquals("loadImage imagemodel.RGBArray\n", mockLog.toString());
  }

  @Test
  public void loadRainbow() {
    features.loadRainbow(100, 100, false);
    assertEquals("loadImage imagemodel.Rainbow\n", mockLog.toString());
  }

  @Test
  public void loadCheckerBoard() {
    features.loadCheckerBoard(10);
    assertEquals("loadImage imagemodel.CheckerBoard\n", mockLog.toString());
  }

  @Test
  public void saveToFile() {
    try {
      features.saveToFile("test_save.png");
    } catch (IOException e) {
      fail("should not throw exception");
    }
    assertEquals("outputImage\ngetWidth\ngetHeight\n", mockLog.toString());
  }

  @Test
  public void blur() {
    features.blur();
    assertEquals("loadEffect imagemodel.Blur\napplyEffect\n", mockLog.toString());
  }

  @Test
  public void sharpen() {
    features.sharpen();
    assertEquals("loadEffect imagemodel.Sharpen\napplyEffect\n", mockLog.toString());
  }

  @Test
  public void greyscale() {
    features.greyscale();
    assertEquals("loadEffect imagemodel.Greyscale\napplyEffect\n", mockLog.toString());
  }

  @Test
  public void sepia() {
    features.sepia();
    assertEquals("loadEffect imagemodel.Sepia\napplyEffect\n", mockLog.toString());
  }

  @Test
  public void dither() {
    features.dither();
    assertEquals("loadEffect imagemodel.Dither\napplyEffect\n", mockLog.toString());
  }

  @Test
  public void mosaic() {
    features.mosaic(10);
    assertEquals("loadEffect imagemodel.Mosaic\napplyEffect\n", mockLog.toString());
  }

  @Test
  public void undo() {
    features.undo();
    assertEquals("undo\n", mockLog.toString());
  }

  @Test
  public void redo() {
    features.redo();
    assertEquals("redo\n", mockLog.toString());
  }

  @Test
  public void loadThenSave() {
    try {
      features.loadPhoto("manhattan-small.png");
    } catch (IOException e) {
      fail("shouldn't throw exception");
    }

    try {
      features.saveToFile("test_save.png");
    } catch (IOException e) {
      fail("should not throw exception");
    }
    assertEquals("loadImage imagemodel.RGBArray\n"
            + "outputImage\ngetWidth\ngetHeight\n", mockLog.toString());
  }

  @Test
  public void loadEffectThenSave() {
    try {
      features.loadPhoto("manhattan-small.png");
    } catch (IOException e) {
      fail("shouldn't throw exception");
    }
    features.greyscale();

    features.mosaic(10);

    try {
      features.saveToFile("test_save.png");
    } catch (IOException e) {
      fail("should not throw exception");
    }
    assertEquals("loadImage imagemodel.RGBArray\nloadEffect imagemodel.Greyscale\n"
            + "applyEffect\nloadEffect imagemodel.Mosaic\n"
            + "applyEffect\noutputImage\ngetWidth\ngetHeight\n", mockLog.toString());
  }

  @Test
  public void loadRainbowEffectThenSave() {
    features.loadRainbow(100, 100, true);
    features.greyscale();

    features.mosaic(10);

    try {
      features.saveToFile("test_save.png");
    } catch (IOException e) {
      fail("should not throw exception");
    }
    assertEquals("loadImage imagemodel.Rainbow\nloadEffect imagemodel.Greyscale\n"
            + "applyEffect\nloadEffect imagemodel.Mosaic\napplyEffect\noutputImage\n"
            + "getWidth\ngetHeight\n", mockLog.toString());
  }


  @Test
  public void outputImage() {

    BufferedImage input = features.outputImage();

    int[][][] compare = new int[3][3][3];

    for (int i = 0; i < input.getHeight(); i++) {
      for (int j = 0; j < input.getWidth(); j++) {
        int color = input.getRGB(j, i);
        Color c = new Color(color);
        assertEquals(compare[i][j][0], c.getRed());
        assertEquals(compare[i][j][1], c.getGreen());
        assertEquals(compare[i][j][2], c.getBlue());
      }
    }

    assertEquals("outputImage\ngetWidth\ngetHeight\n", mockLog.toString());
  }


  // constructor tests
  @Test(expected = IllegalArgumentException.class)
  public void illegalControllerConstructor1() {
    controller = new ControllerImpl(null, null, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void illegalControllerConstructor2() {
    controller = new ControllerImpl(new StringReader("load manhattan-small.png"), null, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void illegalControllerConstructor3() {
    controller = new ControllerImpl(null, imgDefaultIO, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void illegalControllerConstructor4() {
    controller = new ControllerImpl(null, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void illegalControllerConstructor5() {
    controller = new ControllerImpl(null, defaultMock);
  }

  @Test(expected = IllegalArgumentException.class)
  public void illegalControllerConstructor6() {
    controller = new ControllerImpl(imgDefaultIO, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void illegalEmptyFile() {
    controller = new ControllerImpl(
            new StringReader(""), imgDefaultIO, new ModelFacade());
    try {
      controller.start();
    } catch (IOException e) {
      fail("Unexpected IOException");
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void illegalLeadingWhitespace() {
    controller = new ControllerImpl(
            new StringReader("  \n  load     manhattan-small.png"), imgDefaultIO,
            new ModelFacade());
    try {
      controller.start();
    } catch (IOException e) {
      fail("Unexpected IOException");
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void illegalOnlyCommentsAndWhitespace() {
    controller = new ControllerImpl(
            new StringReader("  \n// test\n"), imgDefaultIO,
            new ModelFacade());
    try {
      controller.start();
    } catch (IOException e) {
      fail("Unexpected IOException");
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void tooManyArgs_1() {
    controller = new ControllerImpl(
            new StringReader("load manhattan-small.png\ndither 10"),
            imgDefaultIO, new ModelFacade());
    try {
      controller.start();
    } catch (IOException e) {
      fail("Unexpected IOException");
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void tooManyArgs_2() {
    controller = new ControllerImpl(
            new StringReader("load rainbow 300 300 true extra_arg"),
            imgDefaultIO, new ModelFacade());
    try {
      controller.start();
    } catch (IOException e) {
      fail("Unexpected IOException");
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void toofewArgs_1() {
    controller = new ControllerImpl(new StringReader("load"),
            imgDefaultIO, new ModelFacade());
    try {
      controller.start();
    } catch (IOException e) {
      fail("Unexpected IOException");
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void toofewArgs_2() {
    controller = new ControllerImpl(new StringReader("load checkerboard"),
            imgDefaultIO, new ModelFacade());
    try {
      controller.start();
    } catch (IOException e) {
      fail("Unexpected IOException");
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void badArgs_1() {
    controller = new ControllerImpl(
            new StringReader("load manhattan-small.png\nmosaic cat"),
            imgDefaultIO, new ModelFacade());
    try {
      controller.start();
    } catch (IOException e) {
      fail("Unexpected IOException");
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void badArgs_2() {
    controller = new ControllerImpl(
            new StringReader("load manhattan-small.png\ncheckerboard abc"),
            imgDefaultIO, new ModelFacade());
    try {
      controller.start();
    } catch (IOException e) {
      fail("Unexpected IOException");
    }
  }

  /**
   * IOException Load Command Test.
   * Loading a non-existing image
   */
  @Test
  public void badLoadIOException() {
    controller = new ControllerImpl(new StringReader("load notafile.oops"),
            imgDefaultIO, new ModelFacade());
    try {
      controller.start();
      fail("IOException Expected.");
    } catch (IOException e) {
      assert true;
    }
  }

  /**
   * IllegalArgumentException Load Command Test.
   * load is not the first command in script.
   */
  @Test(expected = IllegalArgumentException.class)
  public void noLoadCMD() {
    controller = new ControllerImpl(new StringReader("save img.png"),
            imgDefaultIO, new ModelFacade());
    try {
      controller.start();
    } catch (IOException e) {
      fail("Unexpected IOException");
    }
  }

  /**
   * Load & Save Command Test.
   * Load one image and save the image without applying any ImageEffect should always
   * yield the same image.
   */
  @Test
  public void testLoadSave() {
    controller = new ControllerImpl(
            new StringReader("load manhattan-small.png\n" +
                    "save manhattan-small-test.png\n" +
                    "save manhattan-small-test-copy.png"),
            imgDefaultIO, new ModelFacade());
    try {
      controller.start();
      Image groundTruth = new RGBArray(imgDefaultIO.input("manhattan-small.png"));
      Image imgCopy1 = new RGBArray(imgDefaultIO.input("manhattan-small-test.png"));
      for (int y = 0; y < imgCopy1.getHeight(); ++y) {
        for (int x = 0; x < imgCopy1.getWidth(); ++x) {
          for (int channel = 0; channel < 3; ++channel) {
            assertTrue(abs(groundTruth.getVal(x, y, channel)
                    - imgCopy1.getVal(x, y, channel)) < 1);
          }
        }
      }
      Image imgCopy2 = new RGBArray(imgDefaultIO.input("manhattan-small-test-copy.png"));
      for (int y = 0; y < imgCopy2.getHeight(); ++y) {
        for (int x = 0; x < imgCopy2.getWidth(); ++x) {
          for (int channel = 0; channel < 3; ++channel) {
            assertTrue(abs(imgCopy1.getVal(x, y, channel)
                    - imgCopy2.getVal(x, y, channel)) < 1);
          }
        }
      }
    } catch (IOException e) {
      fail("Unexpected IOException");
    }
  }

  /**
   * GroundTruth Tests.
   * Load one image and apply specified effect and save the image.
   * Check the generated images with the groundTruth ones.
   */
  @Test
  public void testBlurBlur() {
    controller = new ControllerImpl(
            new StringReader("load manhattan-small.png\n" +
                    "blur\n" +
                    "blur\n" +
                    "save manhattan-small-blur-blur-test.png"),
            imgDefaultIO, new ModelFacade());
    try {
      controller.start();
      Image groundTruth = new RGBArray(
              imgDefaultIO.input("manhattan-small-blur-blur.png"));
      Image imgCopy = new RGBArray(
              imgDefaultIO.input("manhattan-small-blur-blur-test.png"));
      for (int y = 0; y < imgCopy.getHeight(); ++y) {
        for (int x = 0; x < imgCopy.getWidth(); ++x) {
          for (int channel = 0; channel < 3; ++channel) {
            assertTrue(abs(groundTruth.getVal(x, y, channel)
                    - imgCopy.getVal(x, y, channel)) < 1);
          }
        }
      }
    } catch (IOException e) {
      fail("Unexpected IOException");
    }
  }

  @Test
  public void testSharpenSharpen() {
    controller = new ControllerImpl(
            new StringReader("load manhattan-small.png\n" +
                    "sharpen\n" +
                    "sharpen\n" +
                    "save manhattan-small-sharpen-sharpen-test.png"),
            imgDefaultIO, new ModelFacade());
    try {
      controller.start();
      Image groundTruth = new RGBArray(
              imgDefaultIO.input("manhattan-small-sharpen-sharpen.png"));
      Image imgCopy = new RGBArray(
              imgDefaultIO.input("manhattan-small-sharpen-sharpen-test.png"));
      for (int y = 0; y < imgCopy.getHeight(); ++y) {
        for (int x = 0; x < imgCopy.getWidth(); ++x) {
          for (int channel = 0; channel < 3; ++channel) {
            assertTrue(abs(groundTruth.getVal(x, y, channel)
                    - imgCopy.getVal(x, y, channel)) < 1);
          }
        }
      }
    } catch (IOException e) {
      fail("Unexpected IOException");
    }
  }

  @Test
  public void testGreyScale() {
    controller = new ControllerImpl(
            new StringReader("load manhattan-small.png\n" +
                    "greyscale\n" +
                    "save manhattan-small-greyscale-test.png"),
            imgDefaultIO, new ModelFacade());
    try {
      controller.start();
      Image groundTruth = new RGBArray(
              imgDefaultIO.input("manhattan-small-greyscale.png"));
      Image imgCopy = new RGBArray(
              imgDefaultIO.input("manhattan-small-greyscale-test.png"));
      for (int y = 0; y < imgCopy.getHeight(); ++y) {
        for (int x = 0; x < imgCopy.getWidth(); ++x) {
          for (int channel = 0; channel < 3; ++channel) {
            assertTrue(abs(groundTruth.getVal(x, y, channel)
                    - imgCopy.getVal(x, y, channel)) < 1);
          }
        }
      }
    } catch (IOException e) {
      fail("Unexpected IOException");
    }
  }

  @Test
  public void testSepia() {
    controller = new ControllerImpl(
            new StringReader("load manhattan-small.png\n" +
                    "sepia\n" +
                    "save manhattan-small-sepia-test.png"),
            imgDefaultIO, new ModelFacade());
    try {
      controller.start();
      Image groundTruth = new RGBArray(
              imgDefaultIO.input("manhattan-small-sepia.png"));
      Image imgCopy = new RGBArray(
              imgDefaultIO.input("manhattan-small-sepia-test.png"));
      for (int y = 0; y < imgCopy.getHeight(); ++y) {
        for (int x = 0; x < imgCopy.getWidth(); ++x) {
          for (int channel = 0; channel < 3; ++channel) {
            assertTrue(abs(groundTruth.getVal(x, y, channel)
                    - imgCopy.getVal(x, y, channel)) < 1);
          }
        }
      }
    } catch (IOException e) {
      fail("Unexpected IOException");
    }
  }

  @Test
  public void testDither() {
    controller = new ControllerImpl(
            new StringReader("load manhattan-small.png\n" +
                    "dither\n" +
                    "save manhattan-small-dither-test.png"),
            imgDefaultIO, new ModelFacade());
    try {
      controller.start();
      Image groundTruth = new RGBArray(
              imgDefaultIO.input("manhattan-small-dither.png"));
      Image imgCopy = new RGBArray(
              imgDefaultIO.input("manhattan-small-dither-test.png"));
      for (int y = 0; y < imgCopy.getHeight(); ++y) {
        for (int x = 0; x < imgCopy.getWidth(); ++x) {
          for (int channel = 0; channel < 3; ++channel) {
            assertTrue(abs(groundTruth.getVal(x, y, channel)
                    - imgCopy.getVal(x, y, channel)) < 1);
          }
        }
      }
    } catch (IOException e) {
      fail("Unexpected IOException");
    }
  }
}
