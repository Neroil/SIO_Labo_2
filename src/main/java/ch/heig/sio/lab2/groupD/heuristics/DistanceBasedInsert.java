package ch.heig.sio.lab2.groupD.heuristics;

import ch.heig.sio.lab2.display.TspHeuristicObserver;
import ch.heig.sio.lab2.groupD.Utilities.OptimizedLinkedList;
import ch.heig.sio.lab2.tsp.TspData;

/**
 * The abstract class used in {@link GenericConstructiveHeuristic} to implement a distance based heuristic (far or close).
 * @author Edwin Häffner
 * @author Arthur Junod
 */
public abstract class DistanceBasedInsert extends GenericConstructiveHeuristic {
    OptimizedLinkedList<CityDistancePair> outsideCycleCitiesDistance;

    /**
     * The insert logic for the distance based heuristic.
     * It starts by populating the list of cities outside the tour and then loop on the logic to add them to the tour until all are added.
     * @param data              The TspData used to get information on the cities
     * @param startCityIndex    The city from which the tour starts
     * @param observer          The observer used to update the GUI
     */
    @Override
    public void insertLogic(TspData data, int startCityIndex, TspHeuristicObserver observer) {
        outsideCycleCitiesDistance = new OptimizedLinkedList<>();

        int selectedDistance = getMinOrMax();
        OptimizedLinkedList.Node<CityDistancePair> selectedCityNode = null;

        //Populate with distance to startCityIndex and the city
        for (int i = 0; i < data.getNumberOfCities(); ++i){
            if(i == startCityIndex) continue;

            int distance = data.getDistance(i,startCityIndex);
            OptimizedLinkedList.Node<CityDistancePair> currNode = outsideCycleCitiesDistance.add(new CityDistancePair(i,distance));

            if (cityDistanceSelection(distance, selectedDistance)){
                selectedDistance = distance;
                selectedCityNode = currNode;
            }
        }

        if(selectedCityNode == null){
            System.out.println("No closest city, make sure you have at least 2 cities");
            return;
        }

        //Main loop logic
        do {
            //Add the selected city
            insertCity(selectedCityNode.getValue().getIndex(),data);
            observer.update(calculateEdges());

            //Remove the city
            outsideCycleCitiesDistance.remove(selectedCityNode);

            //Update the distances and get the new city
            selectedCityNode = updateDistanceAndGetCity(selectedCityNode.getValue(),data);

        } while (!outsideCycleCitiesDistance.isEmpty() && selectedCityNode != null);
    }


    /**
     * Update the distance of all the cities not in the cycle to the cityToCompareTo and return the new city to add.
     * @param cityToCompareTo   The city to compare the distances to, it should be the latest city added to the cycle
     * @param data              The TspData used to get the distances
     * @return                  The new city to then add to the cycle
     */
    private OptimizedLinkedList.Node<CityDistancePair> updateDistanceAndGetCity(CityDistancePair cityToCompareTo, TspData data){
        OptimizedLinkedList.Node<CityDistancePair> currNode = outsideCycleCitiesDistance.getFirst();
        if(currNode == null) return null;

        //Update the distances
        do {
            int currDistance = currNode.getValue().getDistance();
            int maybeNextDistance = data.getDistance(cityToCompareTo.getIndex(),
                    currNode.getValue().getIndex());

            if(maybeNextDistance < currDistance){
                currNode.getValue().setDistance(maybeNextDistance);
            }

            currNode = currNode.getNext();
        } while (currNode != null);

        //Find the city based on cityDistanceSelection() implementation
        OptimizedLinkedList.Node<CityDistancePair> selectedCity = outsideCycleCitiesDistance.getFirst();
        currNode = outsideCycleCitiesDistance.getFirst();
        do {

            if(cityDistanceSelection(currNode.getValue().getDistance(),selectedCity.getValue().getDistance())){
                selectedCity = currNode;
            }

            currNode = currNode.getNext();
        } while (currNode != null);

        return selectedCity;
    }

    /**
     * The abstract method called to select the city to insert in our tour.
     * @param d1    Distance of the new city to test if selected
     * @param d2    Distance of the city already selected
     * @return      A boolean that update replaces the selected city by the tested one if True
     */
    abstract boolean cityDistanceSelection(int d1, int d2);

    /**
     * The abstract method called when initialising the selected distance at the start, useful when using a distance based heuristic.
     * @return  The initial value of the slected distance, here Integer.MAX_VALUE or Integer.MIN_VALUE
     */
    abstract int getMinOrMax();
}
