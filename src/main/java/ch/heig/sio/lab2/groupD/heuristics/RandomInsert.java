package ch.heig.sio.lab2.groupD.heuristics;

import ch.heig.sio.lab2.display.TspHeuristicObserver;
import ch.heig.sio.lab2.tsp.TspData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * The random heuristic used in the {@link GenericConstructiveHeuristic} to create a tour from it.
 * It contains a list of cities not visited in a random order from which it gets the city to visit.
 * @author Edwin Häffner
 * @author Arthur Junod
 */
public class RandomInsert extends GenericConstructiveHeuristic{
    ArrayList<Integer> citiesToVisit;
    Random rand = new Random();
    int citiesToVisitIndex;


    /**
     * Override the insertLogic() to insert our cities in a random order.
     *
     * @param data              The TspData used to get the data on the cities when inserted
     * @param startCityIndex    The index of the city the tour starts
     * @param observer          The observer used to update the GUI
     */
    @Override
    public void insertLogic(TspData data, int startCityIndex, TspHeuristicObserver observer) {
        generateShuffledCityToVisit(data, startCityIndex);

        while(citiesToVisitIndex < data.getNumberOfCities() - 1){ //nbCities - 1 since we don't count the start city
            insertCity(getUnusedCity(),data);
            observer.update(calculateEdges());
        }
    }

    /**
     * Get a random not visited city.
     *
     * @return a random city that hasn't been visited before
     */
    private int getUnusedCity() {
        return citiesToVisit.get(citiesToVisitIndex++);
    }


    /**
     * Populate the list of cities to visit in a random order.
     *
     * @param data              The TspData to get the number of cities from
     * @param startCityIndex    The city from which the tour starts that will be skipped in the list
     */
    private void generateShuffledCityToVisit(TspData data, int startCityIndex){
        rand.setSeed(0x134DA73);
        int nbOfCities = data.getNumberOfCities();
        citiesToVisitIndex = 0;
        citiesToVisit = new ArrayList<>(nbOfCities);

        for (int i = 0; i < nbOfCities; ++i) {
            if(i != startCityIndex){
                citiesToVisit.add(i);
            }
        }

        Collections.shuffle(citiesToVisit, rand);
    }
}
