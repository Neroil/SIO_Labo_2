package ch.heig.sio.lab2.groupD.Utilities;


/**
 * A simple pair class that allows to only have basics methods.
 * @param <T>   The type of the first value of the pair
 * @param <U>   The type of the second value of the pair
 * @author Edwin Häffner
 * @author Arthur Junod
 */
public class Pair<T,U>{
    private T firstValue;
    private U secondValue;

    public Pair(T v1, U v2){
        firstValue = v1;
        secondValue = v2;
    }

    public T getFirst() {
        return firstValue;
    }

    public U getSecond(){
        return secondValue;
    }

    public void setFirst(T val){
        firstValue = val;
    }

    public void setSecond(U val){
        secondValue = val;
    }
}
