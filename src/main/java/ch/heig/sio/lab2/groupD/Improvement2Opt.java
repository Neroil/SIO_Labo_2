package ch.heig.sio.lab2.groupD;

import ch.heig.sio.lab2.tsp.TspData;
import ch.heig.sio.lab2.tsp.TspImprovementHeuristic;
import ch.heig.sio.lab2.tsp.TspTour;

/*
Pour vérifier si un échange est améliorant, il faut prendre les deux arêtes qu'on change et on vérifie si la distance
de ces deux arcs est moins bonne que les deux nouveaux arcs. Je ne sais pas quoi dire de plus.

Changement de la structure de notre tournée est au pire O(N)

Chaque itération complète de 2Opt est en O(n²)

Échange en O(1)

Pour le changement de sens ne pas s'embêter pour l'instant avec une structure de données complexe (à regarder si on a le temps)
 */
public class Improvement2Opt implements TspImprovementHeuristic {

    private int nbOfIteration;

    Improvement2Opt(int nbOfIteration){
        this.nbOfIteration = nbOfIteration;
    }

    @Override
    public TspTour computeTour(TspTour initialTour) {

        //Data du tour utilisé pour le calcul des distances par exemple.
        TspData tourData = initialTour.data();
        
        int tourNbVertices = initialTour.tour().size();
        
        int[] tourCopy = initialTour.tour().copy();

        long tourLength = initialTour.length();
        long newTourLength = Long.MAX_VALUE;


        do {
            //A faire jusqu'à que la distance n'a pas changé
            // i est le sommet de départ du premier arc de la 2-opt
            for (int i = 0; i < tourNbVertices; ++i) {
                var firstVertexTo = (i + 1) % tourNbVertices;
                var firstArcDistance = tourData.getDistance(i, firstVertexTo);
                // j est le sommet de départ du deuxième arc de la 2-opt
                for (int j = (i + 2) % tourNbVertices; j < tourNbVertices && j > i + 1; ++j) {
                    var secondVertexTo = (j + 1) % tourNbVertices;
                    var secondArcDistance = tourData.getDistance(j, secondVertexTo);
                    if (isDistanceImproved(tourData, i, firstVertexTo, j, secondVertexTo)){
                        newTourLength =
                    }
                }
            }
        } while (tourLength != newTourLength);
            
        return null;
    }

    private int[] invertTour(int[] tour, int start, int end){
        return null;
    }

    // A DEGAGER ET METTRE DANS LA BOUCLE PRINCIPALE
    private boolean isDistanceImproved (TspData data, int v1, int v2, int v3, int v4){
        int initialDistance = data.computeDistance(v1,v2) + data.computeDistance(v3,v4);
        int newDistance = data.computeDistance(v1,v3) + data.computeDistance(v2,v4);



        return newDistance < initialDistance;
    }


}
