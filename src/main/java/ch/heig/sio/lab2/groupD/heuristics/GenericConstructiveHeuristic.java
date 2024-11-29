package ch.heig.sio.lab2.groupD.heuristics;

import ch.heig.sio.lab2.display.ObservableTspConstructiveHeuristic;
import ch.heig.sio.lab2.display.TspHeuristicObserver;
import ch.heig.sio.lab2.groupD.Utilities.OptimizedLinkedList;
import ch.heig.sio.lab2.tsp.Edge;
import ch.heig.sio.lab2.tsp.TspData;
import ch.heig.sio.lab2.tsp.TspTour;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * The abstract class for the heuristic implemented.
 * It contains the logic for inserting a city in the tour and updating the GUI.
 * @author Edwin Häffner
 * @author Arthur Junod
 */
public abstract class GenericConstructiveHeuristic implements ObservableTspConstructiveHeuristic {


    OptimizedLinkedList<Integer> cycleCities;
    int distance;

    /**
     * Compute a tour using the chosen heuristic in the subclass.
     * @param data              Data of problem instance
     * @param startCityIndex    Starting city
     * @param observer          Observer to notify of the progress
     * @return                  The computed tour
     */
    @Override
    public TspTour computeTour(TspData data, int startCityIndex, TspHeuristicObserver observer) {

        distance = 0;
        int nbCities = data.getNumberOfCities();

        cycleCities = new OptimizedLinkedList<>();
        int[] finalCycle = new int[nbCities];

        //Adding the start city
        cycleCities.add(startCityIndex);

        //Call the insertLogic of the subclass
        insertLogic(data, startCityIndex, observer);

        //Populate the finalCycle array
        OptimizedLinkedList.Node<Integer> currNode = cycleCities.getFirst();
        for(int i = 0; i < nbCities; ++i){
            finalCycle[i] = currNode.getValue();
            currNode = currNode.getNext();
        }

        return new TspTour(data, finalCycle, distance);
    }

    public abstract void insertLogic(TspData data, int startCityIndex, TspHeuristicObserver observer);

    /**
     * Calculate the edges of the tour, used to display the tour in the GUI.
     * @return An iterator over the edges of the tour
     */
    public Iterator<Edge> calculateEdges() {
        List<Edge> res = new ArrayList<>();
        OptimizedLinkedList.Node<Integer> currentNode = cycleCities.getFirst();

        if (currentNode == null) {
            return res.iterator();
        }

        OptimizedLinkedList.Node<Integer> firstNode = currentNode;

        // Loop over the cities in the tour to create the edges
        do {
            OptimizedLinkedList.Node<Integer> nextNode = currentNode.getNext();
            if (nextNode == null) {
                nextNode = firstNode; // Loop back to the start
            }
            res.add(new Edge(currentNode.getValue(), nextNode.getValue()));
            currentNode = nextNode;

        } while (currentNode != firstNode);

        return res.iterator();
    }

    /**
     * Insert a city at the optimal position in the tour.
     * @param index the index of the city to insert
     * @param data  the TspData object containing the distances
     */
    public void insertCity(int index, TspData data) {

        // Find the shortest distance between two already existing vertices
        int bestDistance = Integer.MAX_VALUE;
        OptimizedLinkedList.Node<Integer> bestNode = null;

        OptimizedLinkedList.Node<Integer> currentNode = cycleCities.getFirst();
        if (currentNode == null) {
            return; // No city in the tour yet
        }

        OptimizedLinkedList.Node<Integer> firstNode = currentNode;
        do {

            OptimizedLinkedList.Node<Integer> nextNode = currentNode.getNext();
            if (nextNode == null) {
                nextNode = firstNode; // Loop back to the start
            }

            int calculatedDist = data.getDistance(currentNode.getValue(), index) +
                    data.getDistance(index, nextNode.getValue()) -
                    data.getDistance(currentNode.getValue(), nextNode.getValue());

            // If the distance is better, update the best distance and node
            if (calculatedDist < bestDistance) {
                bestDistance = calculatedDist;
                bestNode = currentNode;
            }

            currentNode = nextNode;
        } while (currentNode != firstNode);

        // The best distance has been found, insert the city now
        if (bestNode != null) {
            cycleCities.insertAfter(bestNode, index);
            distance += bestDistance;
        }
    }

}
