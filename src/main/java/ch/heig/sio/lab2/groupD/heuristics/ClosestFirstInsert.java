package ch.heig.sio.lab2.groupD.heuristics;

/**
 * The specification of {@link DistanceBasedInsert} for the closest first heuristic.
 * @author Edwin Häffner
 * @author Arthur Junod
 */
public class ClosestFirstInsert extends DistanceBasedInsert {

    @Override
    boolean cityDistanceSelection(int d1, int d2) {
        return d1 < d2;
    }

    @Override
    int getMinOrMax() {
        return Integer.MAX_VALUE;
    }
}
