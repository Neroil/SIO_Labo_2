package ch.heig.sio.lab2.groupD.heuristics;

import ch.heig.sio.lab2.groupD.Utilities.Pair;

/**
 * The specification of {@link Pair} for one city and a distance.
 * @author Edwin Häffner
 * @author Arthur Junod
 */
public class CityDistancePair extends Pair<Integer,Integer> {

    public CityDistancePair(int index, int distance) {
        super(index, distance);
    }

    public int getIndex(){
        return super.getFirst();
    }

    public int getDistance(){
        return super.getSecond();
    }

    public void setDistance(int distance){
        super.setSecond(distance);
    }

}
