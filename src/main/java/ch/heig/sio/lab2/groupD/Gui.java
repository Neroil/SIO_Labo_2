package ch.heig.sio.lab2.groupD;

import ch.heig.sio.lab2.display.HeuristicComboItem;
import ch.heig.sio.lab2.display.ObservableTspConstructiveHeuristic;
import ch.heig.sio.lab2.display.ObservableTspImprovementHeuristic;
import ch.heig.sio.lab2.display.TspSolverGui;
import ch.heig.sio.lab2.groupD.heuristics.ClosestFirstInsert;
import ch.heig.sio.lab2.groupD.heuristics.FarthestFirstInsert;
import ch.heig.sio.lab2.groupD.heuristics.RandomInsert;
import ch.heig.sio.lab2.tsp.RandomTour;
import com.formdev.flatlaf.FlatLightLaf;


public final class Gui {
  public static void main(String[] args) {
    ObservableTspConstructiveHeuristic[] constructiveHeuristics = {
        new HeuristicComboItem.Constructive("Random tour", new RandomTour()),
            new HeuristicComboItem.Constructive("Closest First", new ClosestFirstInsert()),
            new HeuristicComboItem.Constructive("Farthest First", new FarthestFirstInsert()),
            new HeuristicComboItem.Constructive("Random Insert", new RandomInsert())
    };

    ObservableTspImprovementHeuristic[] improvementHeuristics = {
        // Add the new improvement heuristic
            new HeuristicComboItem.Improvement("2-optimisation", new Improvement2Opt())
    };

    // May not work on all platforms, comment out if necessary
    System.setProperty("sun.java2d.opengl", "true");
    FlatLightLaf.setup();

    new TspSolverGui(1400, 800, "TSP solver", constructiveHeuristics, improvementHeuristics);
  }
}

