package imagemodel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javafx.util.Pair;

/**
 * A Mosaic ImageEffect that gives an image a “stained glass window” effect. A Mosaic ImageEffect
 * takes and stores the number of seeds during construction. The seeds are randomly chosen set of
 * points in the Image during apply and each pixel in the image is paired to the seed that is
 * closest to it. The color of each pixel is then replaced with the average color of its cluster.
 */
public class Mosaic implements ImageEffect {
  /**
   * The number of seeds this Mosaic has.
   */
  private final int numOfSeeds;
  private static final int MAX_SEED = 15000;
  private static final int MIN_SEED = 1;

  /**
   * The default constructor for a Mosaic ImageEffect object. The number of seeds is given and will
   * be final. The actual seeds are randomized in apply() method.
   *
   * @param numOfSeeds the given number of seeds for this Mosaic
   * @throws IllegalArgumentException if the number of seeds is not in the assigned range
   */
  public Mosaic(int numOfSeeds) throws IllegalArgumentException {
    if (numOfSeeds < MIN_SEED || numOfSeeds > MAX_SEED) {
      throw new IllegalArgumentException(
              "Error: Seeds Must Stay Between " + MIN_SEED + " And " + MAX_SEED);
    }
    this.numOfSeeds = numOfSeeds;
  }

  /**
   * Apply this Mosaic ImageEffect to an Image object. The number of seeds is the assigned
   * this.numOfSeeds during Mosaic construction. Seeds are randomized Pair objects and repetition
   * will be ignored. Larger this.seeds will take more time to process. n = num of pixels, m = num
   * of seeds, time O(2n + 2m + nm).
   *
   * @param input the given Image object
   * @return an Image object that has been mosaiced
   */
  @Override
  public Image apply(Image input) {
    // a seedsSet HashSet to store the randomized seeds
    Set<Pair<Integer, Integer>> seedsSet = new HashSet<>();
    int h = input.getHeight();
    int w = input.getWidth();
    int[][][] output = input.getDataClone();
    // get this.seeds number of random (x, y) pairs
    // repetition will be ignored
    Random rand = new Random();
    for (int i = 0; i < this.numOfSeeds; ++i) {
      seedsSet.add(new Pair<>(rand.nextInt(w), rand.nextInt(h)));
    }
    // initialize the seed clusters array lists
    List<List<Integer[]>> clusters = new ArrayList<>();
    for (int i = 0; i < seedsSet.size(); i++) {
      clusters.add(new ArrayList<>());
    }
    // for each pixel calculate the distance (euclidean) to every seed
    // and assign the pixel to the closest seed cluster
    for (int y = 0; y < h; ++y) {
      for (int x = 0; x < w; ++x) {
        int clusterNum = 0;
        int counter = 0;
        double min = Double.MAX_VALUE;
        for (Pair<Integer, Integer> seed : seedsSet) {
          double dist = getDist(x, y, seed);
          if (dist < min) {
            min = dist;
            clusterNum = counter;
          }
          counter++;
        }
        clusters.get(clusterNum).add(new Integer[]{x, y});
      }
    }
    // for each seed cluster calculate the average RGB values
    // and assign them to every pixel in this cluster
    updateRGBWithClusters(output, clusters);
    return new RGBArray(output);
  }

  /**
   * A private helper method that calculate the average RGB values within a cluster and update the
   * RGB array with the average values.
   *
   * @param matrix   the given RGB array
   * @param clusters the given pixel clusters
   */
  private void updateRGBWithClusters(int[][][] matrix, List<List<Integer[]>> clusters) {
    for (List<Integer[]> group : clusters) {
      int r = 0;
      int g = 0;
      int b = 0;
      int size = group.size();
      for (Integer[] point : group) {
        r += matrix[point[1]][point[0]][0];
        g += matrix[point[1]][point[0]][1];
        b += matrix[point[1]][point[0]][2];
      }
      r /= size;
      g /= size;
      b /= size;
      for (Integer[] point : group) {
        matrix[point[1]][point[0]][0] = r;
        matrix[point[1]][point[0]][1] = g;
        matrix[point[1]][point[0]][2] = b;
      }
    }
  }

  /**
   * A simple euclidean dist calculator that calculate the euclidean distance between two (x, y)
   * points.
   *
   * @param x the first point's x coordinate
   * @param y the first point's y coordinate
   * @param b the second point as a Pair object (x, y)
   * @return the euclidean distance between two points
   */
  private double getDist(int x, int y, Pair<Integer, Integer> b) {
    int i = x - b.getKey();
    int j = y - b.getValue();
    return Math.sqrt(i * i + j * j);
  }
}
