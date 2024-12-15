package ch.heig.sio.lab2.groupD.Utilities;

import ch.heig.sio.lab2.tsp.Edge;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterateur utilisé pour parcourir un tableau de villes et créer des arêtes à la volée
 * On l'utilise pour que l'observateur puisse créer les arêtes seulement si besoin
 *
 * @author Edwin Häffner
 * @author Arthur Junod
 */
public class IteratorObserver implements Iterator<Edge> {

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
