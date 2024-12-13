package ch.heig.sio.lab2.groupD.Utilities;

import ch.heig.sio.lab2.tsp.Edge;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class IteratorObserver implements Iterator<Edge> {
    //Faire un iterator qui prend la tournée et qui pourra créer les arrête quand on appelle next à la volée -> afin de ne pas être appelé si pas besoin
    public IteratorObserver(int[] tour){
        this.tour = tour;
        this.length = tour.length;
    }

    @Override
    public boolean hasNext() {
        return index < length;
    }

    @Override
    public Edge next() {
        if(hasNext()){
            return new Edge(tour[index], tour[++index % length]);
        } else {
            throw new NoSuchElementException();
        }
    }

    private final int[] tour;
    private final int length;
    private int index = 0;
}
