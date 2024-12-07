package ch.heig.sio.lab2.groupD;

import ch.heig.sio.lab2.display.HeuristicComboItem;
import ch.heig.sio.lab2.display.ObservableTspConstructiveHeuristic;
import ch.heig.sio.lab2.display.ObservableTspImprovementHeuristic;
import ch.heig.sio.lab2.display.TspSolverGui;
import ch.heig.sio.lab2.tsp.RandomTour;
import com.formdev.flatlaf.FlatLightLaf;

public final class Gui {
  public static void main(String[] args) {
    ObservableTspConstructiveHeuristic[] constructiveHeuristics = {
        new HeuristicComboItem.Constructive("Random tour", new RandomTour()),
        // Add the constructive heuristics
    };

    ObservableTspImprovementHeuristic[] improvementHeuristics = {
        // Add the new improvement heuristic
            new HeuristicComboItem.Improvement("2-optimisation", new Improvement2Opt(50))
    };

    // May not work on all platforms, comment out if necessary
    System.setProperty("sun.java2d.opengl", "true");
    FlatLightLaf.setup();

    new TspSolverGui(1400, 800, "TSP solver", constructiveHeuristics, improvementHeuristics);
  }
}

