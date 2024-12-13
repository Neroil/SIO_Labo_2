package ch.heig.sio.lab2.groupD;

import ch.heig.sio.lab2.display.HeuristicComboItem;
import ch.heig.sio.lab2.display.ObservableTspConstructiveHeuristic;
import ch.heig.sio.lab2.groupD.heuristics.ClosestFirstInsert;
import ch.heig.sio.lab2.groupD.heuristics.FarthestFirstInsert;
import ch.heig.sio.lab2.groupD.heuristics.GenericConstructiveHeuristic;
import ch.heig.sio.lab2.groupD.heuristics.RandomInsert;
import ch.heig.sio.lab2.tsp.RandomTour;
import ch.heig.sio.lab2.tsp.TspConstructiveHeuristic;
import ch.heig.sio.lab2.tsp.TspData;

import java.sql.Time;
import java.text.DecimalFormat;
import java.util.*;

import static java.util.Collections.max;
import static java.util.Collections.min;

public final class Analyze {
  private record Statistics(double max, double min, double median, double mean, double stdDev) {}

  public static void main(String[] args) {
    // TODO
    //  - Renommer le package ;
    //  - Intégrer (et corriger si nécessaire) les heuristiques constructives du labo 1 dans ce package ;
    //  - Implémenter l'heuristique 2-opt utilisant la stratégie "meilleure amélioration" ;
    //  - Documentation soignée comprenant :
    //    - la javadoc, avec auteurs et description des implémentations ;
    //    - des commentaires sur les différentes parties de vos algorithmes.

    // Longueurs optimales :
    // pcb442  : 50778
    // att532  : 86729
    // u574    : 36905
    // pcb1173 : 56892
    // nrw1379 : 56638
    // u1817   : 57201

    // Exemple de lecture d'un jeu de données :
    // TspData data = TspData.fromFile("data/att532.dat");

    //TODO : POUR LES VILLES DE DÉPART DES HEURISTIQUES DISTANCE BASED,
    // FAIRE UNE SECONDE TOURNÉE ALÉATOIRE ET PRENDRE LES CINQUANTES PREMIÈRES VILLES.
   TspConstructiveHeuristic[] heuristics = {
            new ClosestFirstInsert(),
            new FarthestFirstInsert(),
            new RandomTour()
    };

    var opt2 = new Improvement2Opt();

    int numberOfCities = 50;

    // Array of files
    String[] files = {"pcb442", "att532", "u574", "pcb1173", "nrw1379", "u1817"};
    long[] optimalDistances = {50778, 86729, 36905, 56892, 56638, 57201};

    System.out.println("Analyzing heuristics...");
    System.out.println("The performance is a percentage of the mean distance compared to the optimal distance. It should never be below 100% and the closer to 100% the better.");

    // Loop through all the files
    for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
      String file = files[fileIndex];

      // Open the file and analyze the heuristics
      try {
        TspData data = TspData.fromFile("data/" + file + ".dat");

        System.out.println("\nProcessing dataset: " + file + ".dat (" + numberOfCities + " cities)");
        Map<String, Statistics> statsMap = new LinkedHashMap<>();

        //Get 50 first cities of a random tour
        var randomTour = new RandomTour();
        var cities = randomTour.computeTour(data, 0).tour();

        // Loop through all the heuristics
        for (var heuristic : heuristics) {
          ArrayList<Long> results = new ArrayList<>();
          long meanValue = 0;

          // Loop through all the cities
          for (int i = 0; i < numberOfCities; ++i) {
            var timeBefore = System.currentTimeMillis();
            var tourHeuristic = heuristic.computeTour(data, cities.get(i));
            long tour2Opt = opt2.computeTour(tourHeuristic);
            var timeExec = System.currentTimeMillis() - timeBefore;
            long length2Opt = tour2Opt.length();
            long lengthHeuristic = tour.length();
            results.add(lengthHeuristic);
            meanValue += lengthHeuristic;

            updateProgress(i + 1, numberOfCities, heuristic.toString());
          }

          double mean = (double) meanValue / data.getNumberOfCities();
          double medianValue = median(results);
          double stdDevValue = stdDev(results, mean);

          statsMap.put(heuristic.toString(), new Statistics(
                  max(results),
                  min(results),
                  medianValue,
                  mean,
                  stdDevValue
          ));
        }

        printStatistics(file, statsMap, optimalDistances[fileIndex]);

      } catch (Exception e) {
        System.err.println("There was an error in processing " + file + ".dat");
        System.err.println(e.getMessage());
        return;
      }
    }
  }

  public static double median(List<Long> values) {
    Collections.sort(values);
    int middle = values.size() / 2;
    if (values.size() % 2 == 0) {
      return (values.get(middle - 1) + values.get(middle)) / 2.0;
    } else {
      return values.get(middle);
    }
  }

  public static double stdDev(List<Long> values, double mean) {
    double sum = 0;
    for (Long value : values) {
      sum += Math.pow(value - mean, 2);
    }
    return Math.sqrt(sum / (values.size() - 1));
  }

  /**
   * Print the statistics for the heuristics in a nice readable format.
   * Generated with ClaudeAI.
   *
   * @param filename The name of the file being analyzed
   * @param heuristicStats The statistics for each heuristic
   * @param optimalDistance The optimal distance for the dataset
   */
  private static void printStatistics(String filename, Map<String, Statistics> heuristicStats, long optimalDistance) {
    int metricWidth = 20;
    int valueWidth = 18;

    // Print header
    System.out.println("\nAnalysis for dataset: " + filename);
    System.out.println("Optimal tour length: " + String.format("%,d", optimalDistance));
    System.out.printf("%-" + metricWidth + "s", "Metric");
    for (String heuristic : heuristicStats.keySet()) {
      System.out.printf("%-" + valueWidth + "s", heuristic);
    }
    System.out.println();

    // Print separator
    System.out.println("-".repeat(metricWidth + (valueWidth * heuristicStats.size())));

    // Print statistics
    String[] metrics = {"Max", "Min", "Median", "Mean", "Standard Deviation", "Performance (%)"};
    DecimalFormat df = new DecimalFormat("#,##0.00");

    for (String metric : metrics) {
      System.out.printf("%-" + metricWidth + "s", metric);
      for (Statistics stats : heuristicStats.values()) {
        double value = switch (metric) {
          case "Min" -> stats.min;
          case "Max" -> stats.max;
          case "Median" -> stats.median;
          case "Mean" -> stats.mean;
          case "Standard Deviation" -> stats.stdDev;
          case "Performance (%)" -> (stats.mean / optimalDistance) * 100;
          default -> 0.0;
        };
        System.out.printf("%-" + valueWidth + "s", df.format(value));
      }
      System.out.println();
    }
  }

  /**
   * Display a progress bar for the current heuristic tested.
   * Generated with ClaudeAI.
   * @param current           The current city processed, used to track progress
   * @param total             The total number of cities
   * @param currentHeuristic  The name of the current heuristic tested
   */
  private static void updateProgress(int current, int total, String currentHeuristic) {
    int progressBarWidth = 40;
    double percentage = (double) current / total * 100;
    int completedWidth = progressBarWidth * current / total;

    // Create the progress bar
    StringBuilder progressBar = new StringBuilder("[");
    for (int i = 0; i < progressBarWidth; i++) {
      if (i < completedWidth) {
        progressBar.append("=");
      } else if (i == completedWidth) {
        progressBar.append(">");
      } else {
        progressBar.append(" ");
      }
    }
    progressBar.append("]");

    // Print the progress bar and percentage
    System.out.print("\r" + currentHeuristic + " Progress: " + progressBar + " " +
            String.format("%.1f%%", percentage) + "    "); // Extra spaces to clear any previous longer text

    // Print newline if we're done processing
    if (current == total) {
      System.out.println();
    }
  }

}

