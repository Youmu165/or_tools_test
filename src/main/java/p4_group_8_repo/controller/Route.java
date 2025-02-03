package p4_group_8_repo.controller;

import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import p4_group_8_repo.utils.csvUtil;

import java.util.ArrayList;
import java.util.List;

public class Route {

    /**
     *  Generate the route and save it in csv file
     * @param clusterCSV The path to cluster data
     * @param routesCSV The path to route data
     */
    public static void generateRoutes(String clusterCSV, String routesCSV) {
        List<String[]> clusterData = csvUtil.readCSV(clusterCSV);
        if (clusterData.size() <= 1) {
            System.err.println("ERROR: Not enough clusters to compute routes.");
            return;
        }

        // Get the target data
        List<double[]> coordinates = new ArrayList<>();
        for (int i = 1; i < clusterData.size(); i++) {
            coordinates.add(new double[]{
                    Double.parseDouble(clusterData.get(i)[1]),//latitude
                    Double.parseDouble(clusterData.get(i)[2])//longitude
            });
        }

        //create a distance matrix
        int size = coordinates.size();
        double[][] distanceMatrix = new double[size][size];
        EuclideanDistance distanceCalculator = new EuclideanDistance();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                distanceMatrix[i][j] = (i == j) ? 0 : distanceCalculator.compute(coordinates.get(i), coordinates.get(j));
            }
        }

        //Use or-tools to solve the TSP problem
        List<Integer> optimalRoute = solveTSP(distanceMatrix);
        if (optimalRoute == null) {
            System.err.println("ERROR: No optimal route found.");
            return;
        }

        // Output
        List<String[]> routeData = new ArrayList<>();
        routeData.add(new String[]{"route_id", "route"});


        //Divide the entire path into multiple sub-path
        int routeId = 1;
        for (int i = 0; i < optimalRoute.size(); i += 5) {
            StringBuilder routeStr = new StringBuilder();
            for (int j = i; j < Math.min(i + 5, optimalRoute.size()); j++) {
                if (j > i) routeStr.append("->");
                routeStr.append(optimalRoute.get(j)); //Add cluster ID
            }
            routeData.add(new String[]{String.valueOf(routeId++), routeStr.toString()});
        }

        // Write the csv file
        csvUtil.writeCSV(routesCSV, routeData);
        System.out.println("Routes computed successfully and saved in " + routesCSV);
    }

    /**
     * Use or-tools to solve the TSP problem
     * @param distanceMatrix The distance matrix
     * @return The route
     */
    public static List<Integer> solveTSP(double[][] distanceMatrix) {
        Loader.loadNativeLibraries();
        int numNodes = distanceMatrix.length;
        RoutingIndexManager manager = new RoutingIndexManager(numNodes, 1, 0);
        RoutingModel routing = new RoutingModel(manager);


        int transitCallbackIndex = routing.registerTransitCallback((long fromIndex, long toIndex) -> {
            int fromNode = manager.indexToNode(fromIndex);
            int toNode = manager.indexToNode(toIndex);
            return (long) Math.round(distanceMatrix[fromNode][toNode]);
        });

        routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);
        RoutingSearchParameters searchParameters =
                main.defaultRoutingSearchParameters()
                        .toBuilder()
                        .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
                        .build();

        Assignment solution = routing.solveWithParameters(searchParameters);
        if (solution == null) {
            System.err.println("ERROR: No optimal route found.");
            return null;
        }

        List<Integer> route = new ArrayList<>();
        long index = routing.start(0);
        while (!routing.isEnd(index)) {
            route.add(manager.indexToNode(index));
            index = solution.value(routing.nextVar(index));
        }
        route.add(manager.indexToNode(index));
        return route;
    }
}


/*
 *
 *     Maybe we can simply divide the route to five parts so that we no longer to consider the vehicle assigning problem
 *        /
      ∧ _ ∧　　　　∧ _ ∧  Good Idea
 　　　(　･∀･)　　d(ﾟдﾟ　)  /
 -------〇--〇--------〇-----

 */