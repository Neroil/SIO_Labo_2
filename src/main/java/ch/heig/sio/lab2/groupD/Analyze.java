package ch.heig.sio.lab2.groupD;


import ch.heig.sio.lab2.groupD.heuristics.ClosestFirstInsert;
import ch.heig.sio.lab2.groupD.heuristics.FarthestFirstInsert;
import ch.heig.sio.lab2.tsp.RandomTour;
import ch.heig.sio.lab2.tsp.TspConstructiveHeuristic;
import ch.heig.sio.lab2.tsp.TspData;
import io.github.cdimascio.dotenv.Dotenv;

import java.text.DecimalFormat;
import java.util.*;

import static java.util.Collections.max;
import static java.util.Collections.min;

/**
 * <p>
 * Cette classe permet de faire l'analyse des heuristiques sans affichage gui.
 * On récupère des statistiques sur les distances calculées telles que la médiane, la moyenne, la dérivation standard,
 * la distance maximale et la distance minimale pour chaque heuristique avant et après la 2-optimisation et par fichier de données.
 * Finalement, on affiche ces statistiques avec un calcul de la performance pour chaque
 * indicateur (qui est la distance de l'indicateur comparée à la distance optimale, plus elle est proche de 100% mieux c'est)
 * ainsi que le temps d'exécution de l'heuristique avec la 2-optimisation.
 * </p>
 * <p>
 * Exemple d'affichage :
 * </p>
 * <pre>
 * Analysis for dataset: pcb442
 * Optimal tour length: 50’778
 * Metric              ClosestFirstInsert        FarthestFirstInsert       RandomTour
 * ---------------------------------------------------------------------------------------
 * Min                 59'368.00 (116.92%)      56'016.00 (110.32%)      727'438.00 (1'432.58%)
 * Median              60'362.50 (118.88%)      57'266.50 (112.78%)      770'826.00 (1'518.03%)
 * Mean                60'385.48 (118.92%)      57'416.56 (113.07%)      773'247.40 (1'522.80%)
 * Max                 61'351.00 (120.82%)      59'875.00 (117.92%)      797'408.00 (1'570.38%)
 * StdDev              510.08                   797.87                   15'306.18
 * Min2opt             54'373.00 (107.08%)      54'914.00 (108.15%)      53'916.00 (106.18%)
 * Median2opt          55'439.50 (109.18%)      56'671.50 (111.61%)      56'326.50 (110.93%)
 * Mean2opt            55'294.62 (108.89%)      56'727.72 (111.72%)      56'286.34 (110.85%)
 * Max2opt             55'969.00 (110.22%)      59'275.00 (116.73%)      58'183.00 (114.58%)
 * StdDev2opt          394.39                   882.80                   880.88
 * MeanTime (ms)       32.08                    9.44                     135.20
 * </pre>
 * <p>
 * The percentage next to the values are an indication of the performance of the heuristic.
 * The performance is a percentage of the mean distance compared to the optimal distance.
 * It should never be below 100% and the closer to 100% the better.
 * </p>
 * @author Edwin Häffner
 * @author Arthur Junod
 */
public final class Analyze {
  // Record pour les statistiques que l'on va afficher.
  private record Statistics(double min, double median, double mean,double max, double stdDev,
                            double min2opt, double median2opt, double mean2opt,double max2opt, double stdDev2opt,
                            double meanTime) {}

  // Variables statiques pour la graine aléatoire du RandomTour et du nombre de villes (et donc d'optimisation) que l'on va faire.
  static long RANDOM_SEED;
  static final int NUMBER_CITIES = 50;


  public static void main(String[] args) {
    // Longueurs optimales :
    // pcb442  : 50778
    // att532  : 86729
    // u574    : 36905
    // pcb1173 : 56892
    // nrw1379 : 56638
    // u1817   : 57201

    // Chargement des variables d'environnement pour éviter de devoir recompiler la classe lors de changement de seed.
    // A besoin de la dépendance dotenv-java dans Maven
    try {
      Dotenv dotenv = Dotenv.configure().load();
      RANDOM_SEED = Long.parseLong(dotenv.get("TSP_SEED"),16);
    } catch (Exception e) {
      System.err.println("Error loading environment variables, make sure you have a .env file with TSP_SEED set. Using default value of 0x134DAE9.");
      RANDOM_SEED = 0x134DAE9;
    }

   TspConstructiveHeuristic[] heuristics = {
            new ClosestFirstInsert(),
            new FarthestFirstInsert(),
            new RandomTour(RANDOM_SEED)
    };

    var opt2 = new Improvement2Opt();

    // Tableau des fichiers de données
    String[] files = {"pcb442", "att532", "u574", "pcb1173", "nrw1379", "u1817"};
    long[] optimalDistances = {50778, 86729, 36905, 56892, 56638, 57201};

    System.out.println("Analyzing heuristics...");

    // Boucle sur les fichiers
    for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
      String file = files[fileIndex];

      // Ouvre chaque fichier et fait une analyse des heuristiques
      try {
        TspData data = TspData.fromFile("data/" + file + ".dat");

        System.out.println("\nProcessing dataset: " + file + ".dat (" + data.getNumberOfCities() + " cities)");
        Map<String, Statistics> stats = new LinkedHashMap<>();

        // Ce RandomTour est utilisé pour générer les NUMBER_CITIES villes de départ pour les heuristiques qui en ont besoin.
        var randomTour = new RandomTour(RANDOM_SEED);
        var cities = randomTour.computeTour(data, 0).tour();

        // Boucle sur les heuristiques
        for (var heuristic : heuristics) {
          ArrayList<Long> results = new ArrayList<>();
          ArrayList<Long> resultsWithImprovement = new ArrayList<>();

          long meanValue = 0;
          long meanValueWithImprovement = 0;
          long meanValueTime = 0;
          // Boucle sur les NUMBER_CITIES villes
          for (int i = 0; i < NUMBER_CITIES; ++i) {

            var timeBefore = System.currentTimeMillis();
            // Compute le tour initial avec l'heuristique
            var tourHeuristic = heuristic.computeTour(data, cities.get(i));
            // Compute le tour amélioré grâce 2-opt
            var tour2Opt = opt2.computeTour(tourHeuristic);
            var timeExec = System.currentTimeMillis() - timeBefore;
            // Extraction des longueurs des deux tours
            long length2Opt = tour2Opt.length();
            long lengthHeuristic = tourHeuristic.length();

            results.add(lengthHeuristic);
            resultsWithImprovement.add(length2Opt);

            meanValue += lengthHeuristic;
            meanValueWithImprovement += length2Opt;
            meanValueTime += timeExec;

            // Mise à jour de la barre de progression affichée
            updateProgress(i + 1, NUMBER_CITIES, heuristic.getClass().getSimpleName());
          }

          // Calcul des statistiques
          double mean = (double) meanValue / NUMBER_CITIES;
          double meanImprovement = (double) meanValueWithImprovement / NUMBER_CITIES;
          double medianValue = median(results);
          double medianValueImprovement = median(resultsWithImprovement);
          double stdDevValue = stdDev(results, mean);
          double stdDevValueImprovement = stdDev(resultsWithImprovement, meanImprovement);
          double meanTime = (double) meanValueTime / NUMBER_CITIES;

          stats.put(heuristic.getClass().getSimpleName(), new Statistics(
                  min(results),
                  medianValue,
                  mean,
                  max(results),
                  stdDevValue,
                  min(resultsWithImprovement),
                  medianValueImprovement,
                  meanImprovement,
                  max(resultsWithImprovement),
                  stdDevValueImprovement,
                  meanTime
          ));
        }

        printStatistics(file, stats, optimalDistances[fileIndex]);

      } catch (Exception e) {
        System.err.println("There was an error in processing " + file + ".dat");
        System.err.println(e.getMessage());
        return;
      }
    }
  }

  /**
   * Calcule la médiane d'une liste de valeurs.
   *
   * @param values La liste de valeurs
   * @return La médiane des valeurs
   */
  public static double median(List<Long> values) {
    Collections.sort(values);
    int middle = values.size() / 2;
    if (values.size() % 2 == 0) {
      return (values.get(middle - 1) + values.get(middle)) / 2.0;
    } else {
      return values.get(middle);
    }
  }

  /**
   * Calcule l'écart-type d'une liste de valeurs.
   *
   * @param values La liste de valeurs
   * @param mean La moyenne des valeurs
   * @return L'écart-type des valeurs
   */
  public static double stdDev(List<Long> values, double mean) {
    double sum = 0;
    for (Long value : values) {
      sum += Math.pow(value - mean, 2);
    }
    return Math.sqrt(sum / (values.size() - 1));
  }

  /**
   * Affiche les statistiques pour les heuristiques en format lisible.
   * Généré par ClaudeAI
   *
   * @param filename Le nom du fichier analysé
   * @param heuristicStats Les statistiques pour chaque heuristique
   * @param optimalDistance La meilleure distance possible pour le dataset
   */
  private static void printStatistics(String filename, Map<String, Statistics> heuristicStats, long optimalDistance) {
    int metricWidth = 20;
    int valueWidth = 35;

    // Affichage du header des statistiques
    System.out.println("\nAnalysis for dataset: " + filename);
    System.out.println("Optimal tour length: " + String.format("%,d", optimalDistance));
    System.out.printf("%-" + metricWidth + "s", "Metric");
    for (String heuristic : heuristicStats.keySet()) {
      System.out.printf("%-" + valueWidth + "s", heuristic);
    }
    System.out.println();

    // Séparateur
    System.out.println("-".repeat(metricWidth + (valueWidth * heuristicStats.size())));

    String[] metrics = {"Min", "Median", "Mean", "Max", "StdDev",
            "Min2opt", "Median2opt", "Mean2opt", "Max2opt", "StdDev2opt",
            "MeanTime (ms)"};

    DecimalFormat df = new DecimalFormat("#,##0.00");
    DecimalFormat pctFormat = new DecimalFormat("#,##0.00%");

    for (String metric : metrics) {
      System.out.printf("%-" + metricWidth + "s", metric);
      for (Statistics stats : heuristicStats.values()) {
        double value = switch (metric) {
          case "Min" -> stats.min;
          case "Median" -> stats.median;
          case "Mean" -> stats.mean;
          case "Max" -> stats.max;
          case "StdDev" -> stats.stdDev;
          case "Min2opt" -> stats.min2opt;
          case "Median2opt" -> stats.median2opt;
          case "Mean2opt" -> stats.mean2opt;
          case "Max2opt" -> stats.max2opt;
          case "StdDev2opt" -> stats.stdDev2opt;
          case "MeanTime (ms)" -> stats.meanTime;
          default -> 0.0;
        };

        // Ne pas afficher le pourcentage pour les métriques de temps et d'écart type
        if (metric.equals("MeanTime (ms)") || metric.contains("StdDev")) {
          System.out.printf("%-" + valueWidth + "s", df.format(value));
        } else {
          double percentage = value / optimalDistance;
          System.out.printf("%-" + valueWidth + "s",
                  df.format(value) + " (" + pctFormat.format(percentage) + ")");
        }
      }
      System.out.println();
    }
    System.out.println("\nThe percentage next to the values are an indication of the performance of the heuristic.");
    System.out.println("The performance is a percentage of the distance compared to the optimal distance.\nIt should never be below 100% and the closer to 100% the better.");
  }

  /**
   * Affiche une barre de progression pour suivre l'avancement de l'analyse.
   * Généré par ClaudeAI
   * @param current           La ville actuellement traitée
   * @param total             Le nombre total de villes à traiter
   * @param currentHeuristic  Le nom de l'heuristique actuellement utilisée
   */
  private static void updateProgress(int current, int total, String currentHeuristic) {
    int progressBarWidth = 40;
    double percentage = (double) current / total * 100;
    int completedWidth = progressBarWidth * current / total;

    // Créé la barre de progression
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

    // Affiche la barre de progression et le pourcentage
    System.out.print("\r" + currentHeuristic + " Progress: " + progressBar + " " +
            String.format("%.1f%%", percentage) + "    "); // Ajout de plusieurs espaces pour effacer les anciennes valeurs

    // Ajout d'un retour à la ligne si on a fini
    if (current == total) {
      System.out.println();
    }
  }
}

