package ch.heig.sio.lab2.groupD.Utilities;

import ch.heig.sio.lab2.tsp.Edge;

import java.util.Iterator;

public class IteratorObserver implements Iterator<Edge> {
    //Faire un iterator qui prend la tournée et qui pourra créer les arrête quand on appelle next à la volée -> afin de ne pas être appelé si pas besoin
    IteratorObserver (int[] tour){

    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Edge next() {
        return null;
    }
}
