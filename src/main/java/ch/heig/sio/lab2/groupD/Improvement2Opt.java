package ch.heig.sio.lab2.groupD;

import ch.heig.sio.lab2.display.ObservableTspConstructiveHeuristic;
import ch.heig.sio.lab2.display.ObservableTspImprovementHeuristic;
import ch.heig.sio.lab2.display.TspHeuristicObserver;
import ch.heig.sio.lab2.groupD.Utilities.OptimizedLinkedList;
import ch.heig.sio.lab2.tsp.Edge;
import ch.heig.sio.lab2.tsp.TspData;
import ch.heig.sio.lab2.tsp.TspImprovementHeuristic;
import ch.heig.sio.lab2.tsp.TspTour;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
Pour vérifier si un échange est améliorant, il faut prendre les deux arêtes qu'on change et on vérifie si la distance
de ces deux arcs est moins bonne que les deux nouveaux arcs. Je ne sais pas quoi dire de plus.

Changement de la structure de notre tournée est au pire O(N)

Chaque itération complète de 2Opt est en O(n²)

Échange en O(1)

Pour le changement de sens ne pas s'embêter pour l'instant avec une structure de données complexe (à regarder si on a le temps)
 */
public class Improvement2Opt implements ObservableTspImprovementHeuristic {

    private int nbOfIteration = 0;

    Improvement2Opt(int nbOfIteration){
        this.nbOfIteration = nbOfIteration;
    }

    @Override
    public TspTour computeTour(TspTour initialTour,TspHeuristicObserver observer) {

        //Data du tour utilisé pour le calcul des distances par exemple.
        TspData tourData = initialTour.data();
        
        int tourNbVertices = initialTour.tour().size();
        
        int[] tourCopy = initialTour.tour().copy();

        long tourLength = initialTour.length();
        long oldTourLength;
        long newTourLength = Long.MAX_VALUE;
        int bestFirstVertexFrom = 0;
        int bestSecondVertexFrom = 0;
        int bestDist = Integer.MAX_VALUE;

        do {
            bestDist = Integer.MAX_VALUE;
            oldTourLength = tourLength;

            //A faire jusqu'à que la distance n'a pas changé
            // i est le sommet de départ du premier arc de la 2-opt
            for (int i = 0; i < tourNbVertices; ++i) {
                var firstVertexTo = (i + 1) % tourNbVertices;
                var firstVertexDistance = tourData.getDistance(tourCopy[i], tourCopy[firstVertexTo]);
                // j est le sommet de départ du deuxième arc de la 2-opt
                for (int j = (i + 2) % tourNbVertices; j < tourNbVertices && j > i + 1; ++j) {
                    var secondVertexTo = (j + 1) % tourNbVertices;
                    var currentDist = tourData.getDistance(tourCopy[j], tourCopy[secondVertexTo]) + firstVertexDistance;
                    var newDist = tourData.getDistance(tourCopy[i], tourCopy[j]) + tourData.getDistance(tourCopy[secondVertexTo], tourCopy[firstVertexTo]);

                    if (newDist < currentDist) {
                        System.out.println(newDist + ", " + currentDist);
                        System.out.println();
                        if(newDist < bestDist){
                            bestFirstVertexFrom = i;
                            bestSecondVertexFrom = j;
                            newTourLength = tourLength - currentDist + newDist;
                            bestDist = newDist;
                            System.out.println("newTourLength :" + newTourLength);
                        }
                    }
                }
            }

            System.out.println("TourLength update : " + tourLength + ", " + newTourLength);
            if(tourLength > newTourLength){
                //Mettre à jour le tour en entier
                tourLength = newTourLength;

                //Inversion du tour
                int i = bestFirstVertexFrom + 1;
                int j = bestSecondVertexFrom;
                int temp;
                for (;i < j; ++i){
                    temp = tourCopy[i];
                    tourCopy[i] = tourCopy[j];
                    tourCopy[j] = temp;
                    --j;
                }
            }

            nbOfIteration--;

            //Faire un iterator qui prend la tournée et qui pourra créer les arrête quand on appelle next à la volée -> afin de ne pas être appelé si pas besoin
            //Update the observer TODO LE FAIRE A LA
            ArrayList<Edge> edges = new ArrayList<>();
            for(int i = 0; i < tourNbVertices; ++i){
                edges.add(new Edge(tourCopy[i],tourCopy[(i+1) % tourNbVertices]));
            }

            observer.update(edges.iterator());

        } while (oldTourLength != newTourLength || nbOfIteration > 0);


        return new TspTour(tourData, tourCopy, newTourLength);
    }


}
