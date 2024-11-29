package ch.heig.sio.lab2.groupD.heuristics;

/**
 * The specification for {@link DistanceBasedInsert} for the farthest first heuristic.
 * @author Edwin Häffner
 * @author Arthur Junod
 */
public class FarthestFirstInsert extends DistanceBasedInsert{
    @Override
    boolean cityDistanceSelection(int d1, int d2) {
        return d1 > d2;
    }

    @Override
    int getMinOrMax() {
        return Integer.MIN_VALUE;
    }
}
