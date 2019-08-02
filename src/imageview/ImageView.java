package imageview;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Dimension;
import java.io.IOException;
import java.io.StringReader;
import java.util.Hashtable;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.ImageIcon;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;

import imagecontroller.Features;

/**
 * A GUI view for an image editor using the Java Swing Framework. This view is constructed with a
 * Features object whose methods and invoked inside of actionListeners and are used to set the
 * controller in motion to orchestrate the desired behavior.
 */
public class ImageView extends JFrame {
  /**
   * The Maximum and the Minimum numbers of seeds for Mosaic.
   */
  private static final int SEED_MAX = 15000;
  private static final int SEED_INIT = 2500;
  /**
   * All Java Swing components this ImageView contains.
   */
  private JMenuBar menuBar;
  private JPanel imgDisplayPanel;
  private JPanel scriptPanel;
  private JPanel fileIOPanel;
  private JLabel imgPane;
  private JFrame mosaicSeedFrame;
  private JFrame checkerboardFrame;
  private JFrame rainbowFrame;

  /**
   * Sets up all Java swing elements in the gui and emplaces features from the controller into the
   * actionListeners.
   *
   * @param caption  The tile for the application screen
   * @param features The Features interface of the controller to provide the functionality of the
   *                 actionListeners.
   */
  public ImageView(String caption, Features features) {
    super(caption);
    this.setVisible(false);
    this.setLayout(new FlowLayout());
    this.setPreferredSize(new Dimension(900, 750));
    this.setLocation(100, 100);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // menu bar setup
    setMenuBar(features);
    this.setJMenuBar(menuBar);

    // script panel setup
    setScriptPanel(features);
    this.add(scriptPanel, BorderLayout.CENTER);

    // image panel setup
    setImgDisplayPanel(features);
    this.add(imgDisplayPanel, BorderLayout.CENTER);

    // fileIO panel setup
    setFileIOPanel(features);
    this.add(fileIOPanel, BorderLayout.CENTER);

    pack();
    this.setVisible(true);
  }

  /**
   * A private wrapper method to setup the Script Panel.
   *
   * @param features the features from ImageView controller
   */
  private void setScriptPanel(Features features) {
    JTextArea scriptInputArea = new JTextArea(
            "// Use this Script Input Box for batch commands (see README for details)");
    scriptInputArea.setFont(new Font("Serif", Font.PLAIN, 16));
    scriptInputArea.setLineWrap(true);
    scriptInputArea.setWrapStyleWord(true);
    JScrollPane scriptScrollPane = new JScrollPane(scriptInputArea);
    scriptScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scriptScrollPane.setPreferredSize(new Dimension(600, 250));
    JButton exeScriptButton = new JButton("Execute");
    exeScriptButton.addActionListener(l -> {
      Document d = scriptInputArea.getDocument();
      try {
        features.executeScript(new StringReader(d.getText(0, d.getLength())));
        imgPane.setIcon(new ImageIcon(features.outputImage()));
      } catch (BadLocationException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(),
                "Bad Location error", JOptionPane.ERROR_MESSAGE);
      } catch (IOException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(),
                "IO error", JOptionPane.ERROR_MESSAGE);
      } catch (IllegalStateException | IllegalArgumentException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(),
                "Illegal Argument error", JOptionPane.ERROR_MESSAGE);
      }

    });
    // scriptButtonPane contains the exeScriptButton (Center)
    JPanel scriptButtonPane = new JPanel();
    scriptButtonPane.setPreferredSize(new Dimension(150, 250));
    scriptButtonPane.setLayout(new GridLayout());
    scriptButtonPane.setBorder(
            BorderFactory.createEmptyBorder(0, 10, 0, 10));
    scriptButtonPane.add(exeScriptButton);
    // scriptPanel contains the scriptScrollPane (Left) and scriptButtonPane (Right)
    scriptPanel = new JPanel();
    scriptPanel.setPreferredSize(new Dimension(900, 300));
    scriptPanel.setLayout(new FlowLayout());
    scriptPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));
    scriptPanel.add(scriptScrollPane);
    scriptPanel.add(scriptButtonPane);
  }

  /**
   * A private wrapper method to setup the Image Displaying Panel.
   */
  private void setImgDisplayPanel(Features features) {
    imgPane = new JLabel();
    JScrollPane imgScrollPane = new JScrollPane(imgPane);
    imgScrollPane.setPreferredSize(new Dimension(600, 250));
    JButton redoButton = new JButton("Redo");
    JButton undoButton = new JButton("Undo");

    undoButton.addActionListener(l -> {
      try {
        features.undo();
        imgPane.setIcon(new ImageIcon(features.outputImage()));
      } catch (IllegalStateException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(),
                "Illegal State error", JOptionPane.ERROR_MESSAGE);
      }
    });
    redoButton.addActionListener(l -> {
      try {
        features.redo();
        imgPane.setIcon(new ImageIcon(features.outputImage()));
      } catch (IllegalStateException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(),
                "Illegal State error", JOptionPane.ERROR_MESSAGE);
      }
    });

    JPanel imgButtonPanel = new JPanel();
    imgButtonPanel.setLayout(new GridLayout(2, 1, 0, 25));
    imgButtonPanel.setBorder(
            BorderFactory.createEmptyBorder(0, 10, 0, 10));
    imgButtonPanel.setPreferredSize(new Dimension(150, 250));
    imgButtonPanel.add(redoButton);
    imgButtonPanel.add(undoButton);

    imgDisplayPanel = new JPanel();
    imgDisplayPanel.setPreferredSize(new Dimension(900, 300));
    imgDisplayPanel.add(imgScrollPane);
    imgDisplayPanel.add(imgButtonPanel);
  }

  /**
   * A private wrapper method to setup the FileIO Panel.
   *
   * @param features the features from ImageView controller
   */
  private void setFileIOPanel(Features features) {
    //file open
    JButton fileOpenButton = new JButton("Load Image from File");
    fileOpenButton.addActionListener(l -> {
      final JFileChooser fchooser = new JFileChooser(".");
      FileNameExtensionFilter filter = new FileNameExtensionFilter(
              "JPG & PNG Images", "jpg", "png");
      fchooser.setFileFilter(filter);
      int retvalue = fchooser.showOpenDialog(this);
      if (retvalue == JFileChooser.APPROVE_OPTION) {
        try {
          features.loadPhoto(fchooser.getSelectedFile().getCanonicalPath());
          imgPane.setIcon(new ImageIcon(features.outputImage()));
        } catch (IOException e) {
          JOptionPane.showMessageDialog(this, e.getMessage(),
                  "IO error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    // file save
    JButton fileSaveButton = new JButton("Save Image to File");
    fileSaveButton.addActionListener(l -> {
      final JFileChooser fchooser = new JFileChooser(".");
      int retvalue = fchooser.showSaveDialog(this);
      if (retvalue == JFileChooser.APPROVE_OPTION) {
        try {
          features.saveToFile(fchooser.getSelectedFile().getCanonicalPath());
        } catch (IOException e) {
          JOptionPane.showMessageDialog(this, e.getMessage(),
                  "IO error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException e) { // load before save
          JOptionPane.showMessageDialog(this, e.getMessage(),
                  "Illegal State error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });
    fileIOPanel = new JPanel();
    fileIOPanel.setPreferredSize(new Dimension(900, 50));
    fileIOPanel.setLayout(new GridLayout(1, 2, 100, 0));
    fileIOPanel.setBorder(
            BorderFactory.createEmptyBorder(0, 100, 0, 100));
    fileIOPanel.add(fileOpenButton);
    fileIOPanel.add(fileSaveButton);
  }

  /**
   * A private wrapper method to setup the Menu bar.
   *
   * @param features the features from ImageView controller
   */
  private void setMenuBar(Features features) {
    menuBar = new JMenuBar();
    JMenu effectMenu = new JMenu("Apply");
    menuBar.add(effectMenu);
    JMenuItem effectItem = new JMenuItem("Blur");
    effectItem.addActionListener(l -> applyEffect(features::blur, features));
    effectMenu.add(effectItem);
    effectItem = new JMenuItem("Sharpen");
    effectItem.addActionListener(l -> applyEffect(features::sharpen, features));
    effectMenu.add(effectItem);
    effectItem = new JMenuItem("Greyscale");
    effectItem.addActionListener(l -> applyEffect(features::greyscale, features));
    effectMenu.add(effectItem);
    effectItem = new JMenuItem("Sepia");
    effectItem.addActionListener(l -> applyEffect(features::sepia, features));
    effectMenu.add(effectItem);
    effectItem = new JMenuItem("Dither");
    effectItem.addActionListener(l -> applyEffect(features::dither, features));
    effectMenu.add(effectItem);
    effectItem = new JMenuItem("Mosaic");
    setMosaicSeedFrame(features);
    effectItem.addActionListener(l -> mosaicSeedFrame.setVisible(true));
    effectMenu.add(effectItem);
    // Rainbow MenuItem & Rainbow Property Frame
    JMenu createMenu = new JMenu("Create");
    JMenuItem createItem = new JMenuItem("Rainbow");
    setRainbowFrame(features);
    createItem.addActionListener(l -> rainbowFrame.setVisible(true));
    createMenu.add(createItem);
    // CheckerBoard MenuItem & CheckerBoard Property Frame
    createItem = new JMenuItem("CheckerBoard");
    setCheckerboardFrame(features);
    createItem.addActionListener(l -> checkerboardFrame.setVisible(true));
    createMenu.add(createItem);
    menuBar.add(createMenu);
  }

  /**
   * A private wrapper method to setup the Mosaic Frame.
   *
   * @param features the features from ImageView controller
   */
  private void setMosaicSeedFrame(Features features) {
    mosaicSeedFrame =
            getUniversalFrame("Select the Number of Mosaic Seeds", 700, 200);
    JSlider mosaicSeedSlider = new JSlider(JSlider.HORIZONTAL, 0, SEED_MAX, SEED_INIT);
    mosaicSeedSlider.setPreferredSize(new Dimension(600, 100));
    Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
    labelTable.put(0, new JLabel("1"));
    labelTable.put(2500, new JLabel("2500"));
    labelTable.put(5000, new JLabel("5000"));
    labelTable.put(7500, new JLabel("7500"));
    labelTable.put(10000, new JLabel("10000"));
    labelTable.put(12500, new JLabel("12500"));
    labelTable.put(15000, new JLabel("15000"));
    mosaicSeedSlider.setMajorTickSpacing(2500);
    mosaicSeedSlider.setMinorTickSpacing(500);
    mosaicSeedSlider.setPaintTicks(true);
    mosaicSeedSlider.setPaintLabels(true);
    mosaicSeedSlider.setLabelTable(labelTable);
    JButton mosaicSeedButton = new JButton("Apply");
    mosaicSeedButton.addActionListener(l -> {
      int seed = mosaicSeedSlider.getValue() == 0 ? 1 : mosaicSeedSlider.getValue();
      try {
        features.mosaic(seed);
        imgPane.setIcon(new ImageIcon(features.outputImage()));
      } catch (IllegalStateException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(),
                "Illegal State error", JOptionPane.ERROR_MESSAGE);
      }
      mosaicSeedFrame.setVisible(false);
    });
    JPanel mosiacButtonPanel = new JPanel();
    mosiacButtonPanel.add(mosaicSeedButton);
    mosiacButtonPanel.setLayout(new GridLayout());
    mosiacButtonPanel.setBorder(
            BorderFactory.createEmptyBorder(10, 35, 10, 35));
    JPanel mosiacPanel = new JPanel();
    mosiacPanel.setLayout(new GridLayout(2, 1));
    mosiacPanel.setPreferredSize(new Dimension(600, 150));
    mosiacPanel.add(mosaicSeedSlider);
    mosiacPanel.add(mosiacButtonPanel);
    mosaicSeedFrame.add(mosiacPanel);
  }

  /**
   * A private wrapper method to setup the Checkerboard Frame.
   *
   * @param features the features from ImageView controller
   */
  private void setCheckerboardFrame(Features features) {
    checkerboardFrame =
            getUniversalFrame("Size of CheckerBoard Tiles", 700, 150);
    JSpinner checkerboardSpinner = new JSpinner(
            new SpinnerNumberModel(20, 1, Integer.MAX_VALUE, 1));
    checkerboardSpinner.setBorder(
            BorderFactory.createEmptyBorder(5, 0, 5, 0));
    JButton checkerboardCreateButton = new JButton("Create CheckerBoard");
    checkerboardCreateButton.addActionListener(l -> {
      features.loadCheckerBoard((int) checkerboardSpinner.getValue());
      imgPane.setIcon(new ImageIcon(features.outputImage()));
      checkerboardFrame.setVisible(false);
    });
    JPanel checkerboardInputPanel = new JPanel();
    checkerboardInputPanel.setLayout(new GridLayout(1, 2));
    checkerboardInputPanel.add(new JLabel("Enter the Size of Each CheckerBoard Tile: "));
    checkerboardInputPanel.add(checkerboardSpinner);
    JPanel checkerboardButtonPanel = new JPanel();
    checkerboardButtonPanel.add(checkerboardCreateButton);
    checkerboardButtonPanel.setLayout(new GridLayout());
    checkerboardButtonPanel.setBorder(
            BorderFactory.createEmptyBorder(0, 25, 0, 25));
    JPanel checkerboardPanel = new JPanel();
    checkerboardPanel.setLayout(new GridLayout(2, 1));
    checkerboardPanel.setPreferredSize(new Dimension(600, 100));
    checkerboardPanel.add(checkerboardInputPanel);
    checkerboardPanel.add(checkerboardButtonPanel);
    checkerboardFrame.add(checkerboardPanel, BorderLayout.CENTER);
  }

  /**
   * A private wrapper method to setup the Rainbow Frame.
   *
   * @param features the features from ImageView controller
   */
  private void setRainbowFrame(Features features) {
    rainbowFrame = getUniversalFrame("Rainbow Property", 700, 250);
    JSpinner rainbowWidthSpinner = new JSpinner(
            new SpinnerNumberModel(400, 1, Integer.MAX_VALUE, 1));
    JSpinner rainbowHeightSpinner = new JSpinner(
            new SpinnerNumberModel(150, 1, Integer.MAX_VALUE, 1));
    JCheckBox rainbowCheckbox = new JCheckBox("Horizontal?");
    rainbowCheckbox.setSelected(true);
    JButton rainbowCreateButton = new JButton("Create Rainbow");
    rainbowCreateButton.addActionListener(l -> {
      features.loadRainbow((int) rainbowWidthSpinner.getValue(),
              (int) rainbowHeightSpinner.getValue(), rainbowCheckbox.isSelected());
      imgPane.setIcon(new ImageIcon(features.outputImage()));
      rainbowFrame.setVisible(false);
    });
    JPanel rainbowInputPanel = new JPanel();
    rainbowInputPanel.setLayout(new GridLayout(3, 2));
    rainbowInputPanel.setPreferredSize(new Dimension(500, 100));
    rainbowInputPanel.add(new JLabel("Enter the Width of the Rainbow: "));
    rainbowInputPanel.add(rainbowWidthSpinner);
    rainbowInputPanel.add(new JLabel("Enter the Height of the Rainbow: "));
    rainbowInputPanel.add(rainbowHeightSpinner);
    rainbowInputPanel.add(new JLabel("Orientation: "));
    rainbowInputPanel.add(rainbowCheckbox);
    JPanel rainbowButtonPanel = new JPanel();
    rainbowButtonPanel.add(rainbowCreateButton);
    rainbowButtonPanel.setLayout(new GridLayout());
    rainbowButtonPanel.setBorder(
            BorderFactory.createEmptyBorder(25, 25, 25, 25));
    JPanel rainbowPanel = new JPanel();
    rainbowPanel.setLayout(new GridLayout(2, 1));
    rainbowPanel.setPreferredSize(new Dimension(600, 200));
    rainbowPanel.add(rainbowInputPanel);
    rainbowPanel.add(rainbowButtonPanel);
    rainbowFrame.add(rainbowPanel, BorderLayout.CENTER);
  }

  /**
   * Get a general-purpose JFrame with flow layout.
   *
   * @param title  the given title of the frame
   * @param width  the assigned width of the frame
   * @param height the assigned height of the frame
   * @return a JFrame object
   */
  private JFrame getUniversalFrame(String title, int width, int height) {
    JFrame frame = new JFrame(title);
    frame.setVisible(false);
    frame.setLayout(new FlowLayout());
    frame.setSize(width, height);
    frame.setLocation(200, 200);
    return frame;
  }

  /**
   * A private helper method that runs an effect with no argument and updates the image. Pops up a
   * message dialog if IllegalStateException is caught
   *
   * @param effect   Runnable effect methods
   * @param features the Feature from ImageView controller
   */
  private void applyEffect(Runnable effect, Features features) {
    try {
      effect.run();
      imgPane.setIcon(new ImageIcon(features.outputImage()));
    } catch (IllegalStateException e) { // effect before save
      JOptionPane.showMessageDialog(this, e.getMessage(),
              "Illegal State error", JOptionPane.ERROR_MESSAGE);
    }
  }
}
