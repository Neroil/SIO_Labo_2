package ch.heig.sio.lab2.groupD;

import ch.heig.sio.lab2.display.ObservableTspImprovementHeuristic;
import ch.heig.sio.lab2.display.TspHeuristicObserver;
import ch.heig.sio.lab2.groupD.Utilities.IteratorObserver;
import ch.heig.sio.lab2.tsp.TspData;
import ch.heig.sio.lab2.tsp.TspTour;

/**
 * <p>
 * Cette classe implémente l'heuristique d'amélioration 2-opt pour le problème du voyageur de commerce.
 * Pour trouver cette amélioration, la fonction computeTour() parcourt toutes les combinaisons possibles de deux arêtes
 * dans le tour initial et les échange si cela permet de réduire la longueur du tour. On utilise la stratégie du "best fit".
 * C'est-à-dire qu'on force l'algorithme à parcourir toutes les arêtes du tour pour trouver la meilleure amélioration possible.
 * </p>
 * <p>
 * computeTour() continue de tourner tant qu'une amélioration est trouvée. Si aucune amélioration n'est trouvée, l'algorithme
 * s'arrête et retourne le tour amélioré.
 * </p>
 * <p>
 * La complexité de cet algorithme est de O(q*n^2) où q est le nombre d'améliorations trouvées et n le nombre de sommets.
 * On ne peut pas savoir à l'avance combien d'améliorations seront trouvées.
 * </p>
 *
 * @author Edwin Häffner
 * @author Arthur Junod
 */
public class Improvement2Opt implements ObservableTspImprovementHeuristic {

    /**
     * Cette fonction permet de 2-optimiser un tour donné.
     * Elle tourne en boucle jusqu'à ne plus pouvoir améliorer ce tour et va toujours prendre la meilleure 2-optimisation
     * possible à chaque itération.
     *
     * @param initialTour   Le tour à améliorer
     * @param observer      L'Observer utilisé pour mettre à jour visuellement l'application
     * @return              Un nouveau tour qui est 2-optimisé
     */
    @Override
    public TspTour computeTour(TspTour initialTour, TspHeuristicObserver observer) {

        //Copie des données du tour initial
        final TspData tourData = initialTour.data();
        final int tourNbVertices = initialTour.tour().size();
        final int[] tourCopy = initialTour.tour().copy();

        long tourLength = initialTour.length();
        long oldTourLength;

        //Boucle principale de l'algorithme améliorant. Il s'arrête lorsque plus aucune amélioration n'est trouvée.
        do {
            oldTourLength = tourLength; //Sauvegarde de la longueur du tour avant amélioration

            //Mise des variables de la meilleure amélioration à -1 pour éviter un warning
            int bestI = -1;
            int bestJ = -1;
            long bestImprovement = 0;

            // On s'arrête deux sommets avant la fin, car les arcs qui suivent ont déjà été traité.
            for (int i = 0; i < tourNbVertices - 2; i++) {
                int i1 = i + 1;
                int currentEdge1 = tourData.getDistance(tourCopy[i], tourCopy[i1]);

                for (int j = i + 2; j < tourNbVertices; j++) {
                    int j1 = j + 1;
                    // Dans le cas où j1 atteint la fin du tour, on le fait pointer vers le premier sommet
                    // Comme j est toujours < tourNbVertices, j1 ne peut valoir que tourNbVertices
                    // Cette approche est plus performante qu'utiliser un modulo
                    if (j1 == tourNbVertices) {
                        j1 = 0;
                    }

                    //Calcul de l'amélioration possible
                    long currentCost = currentEdge1 + tourData.getDistance(tourCopy[j], tourCopy[j1]);
                    long newCost = tourData.getDistance(tourCopy[i], tourCopy[j]) +
                            tourData.getDistance(tourCopy[i1], tourCopy[j1]);
                    long improvement = currentCost - newCost;

                    //Si une amélioration est trouvée, on garde sa valeur et on indique quels sommets à inverser
                    if (improvement > bestImprovement) {
                        bestImprovement = improvement;
                        bestI = i;
                        bestJ = j;
                    }
                }
            }

            if (bestImprovement > 0) {
                // Inversion des sommets entre i+1 et j en utilisant une approche "deux pointeurs"
                // qui se déplacent l'un vers l'autre jusqu'à se rencontrer
                int i = bestI + 1;
                int j = bestJ;
                int temp;
                //Swap des sommets entre i et j (i et j inclus)
                for (;i < j; ++i){
                    temp = tourCopy[i];
                    tourCopy[i] = tourCopy[j];
                    tourCopy[j] = temp;
                    --j;
                }
                tourLength -= bestImprovement; //Mise à jour de la longueur du tour

                observer.update(new IteratorObserver(tourCopy));
            }

        } while (oldTourLength != tourLength); //True tant qu'on trouve une nouvelle amélioration

        return new TspTour(tourData, tourCopy, tourLength);
    }
}