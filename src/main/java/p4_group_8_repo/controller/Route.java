package p4_group_8_repo.controller;

import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import com.google.protobuf.Duration;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import p4_group_8_repo.utils.csvUtil;
//import com.google.ortools.constraintsolver.BoolTriState;

import java.util.ArrayList;
import java.util.List;

public class Route {
    public static void generateRoutes(String clusterCSV, String routesCSV) {
        List<String[]> clusterData = csvUtil.readCSV(clusterCSV);
        if (clusterData.size() <= 1) {
            System.err.println(" ERROR: Not enough clusters to compute routes.");
            return;
        }

        List<double[]> coordinates = new ArrayList<>();
        for (int i = 1; i < clusterData.size(); i++) {
            coordinates.add(new double[]{
                    Double.parseDouble(clusterData.get(i)[1]),
                    Double.parseDouble(clusterData.get(i)[2])
            });
        }

        //calculate the matrix of distance

        int size = coordinates.size();
        double[][] distanceMatrix = new double[size][size];
        EuclideanDistance distanceCalculator = new EuclideanDistance();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                distanceMatrix[i][j] = (i == j) ? 0 : distanceCalculator.compute(coordinates.get(i), coordinates.get(j));
            }
        }
        double maxDistance = 0.0;
        for (int i = 0; i < distanceMatrix.length; i++) {
            for (int j = 0; j < distanceMatrix[i].length; j++) {
                if (distanceMatrix[i][j] > maxDistance) {
                    maxDistance = distanceMatrix[i][j];
                }
            }
        }
        if (maxDistance > 0) {
            for (int i = 0; i < distanceMatrix.length; i++) {
                for (int j = 0; j < distanceMatrix[i].length; j++) {
                    distanceMatrix[i][j] = (distanceMatrix[i][j] / maxDistance) * 1000;
                }
            }
        }
//        for (int i = 0; i < distanceMatrix.length; i++) {
//            for (int j = 0; j < distanceMatrix[i].length; j++) {
//                System.out.print(distanceMatrix[i][j] + " ");
//            }
//            System.out.println();
//        }

        List<Integer> optimalRoute = solveTSP(distanceMatrix);

        if (optimalRoute == null) {
            System.err.println(" ERROR: Not enough optimal routes to compute routes.");
            return;
        }

        List <String[]> routeData = new ArrayList<>();
        routeData.add(new String[]{"from_cluster", "to_cluster", "distance"});

        for (int i = 0; i < optimalRoute.size() - 1; i++) {
            int from = optimalRoute.get(i);
            int to = optimalRoute.get(i + 1);
            routeData.add(new String[]{String.valueOf(from), String.valueOf(to), String.valueOf(distanceMatrix[from][to])});

        }

        csvUtil.writeCSV(routesCSV, routeData);
        System.out.println("Routes computed successfully.");
//        List<String[]> routeData = new ArrayList<>();
//        routeData.add(new String[]{"from_cluster", "to_cluster", "distance"});
//        for (int i = 0; i < size; i++) {
//            for (int j = i + 1; j < size; j++) {
//                routeData.add(new String[]{String.valueOf(i), String.valueOf(j), String.valueOf(distanceMatrix[i][j])});
//            }
//        }
//        csvUtil.writeCSV(routesCSV, routeData);
    }

    public static List<Integer> solveTSP(double[][] distanceMatrix) {
        Loader.loadNativeLibraries();

        int numNodes = distanceMatrix.length;
        RoutingIndexManager manager = new RoutingIndexManager(numNodes, 1, 0);
        RoutingModel routing = new RoutingModel(manager);

        int transitCallbackIndex = routing.registerTransitCallback((long fromIndex, long toIndex) ->{
            int fromNode = manager.indexToNode(fromIndex);
            int toNode = manager.indexToNode(toIndex);
            return (long) Math.round(distanceMatrix[fromNode][toNode]);

                });


        routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);
        for (int i = 1; i < numNodes; i++) {
            routing.addDisjunction(new long[]{manager.nodeToIndex(i)}, 1000000);
        }

        //routing.addVariableMinimizedByFinalizer(routing.costVar());
       // routing.setAllowEmptyRoutes(true);
        RoutingSearchParameters searchParameters =
                main.defaultRoutingSearchParameters()
                        .toBuilder()
                        .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
                        .build();


        Assignment solution = routing.solveWithParameters(searchParameters);
        if (solution == null) {
            System.err.println("ERROR: No optimal route found. OR-Tools failed to solve TSP.");
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

    public static void assignCargoToClusters(String inputCSV, String cargoCSV) {
        List<String[]> inputData = csvUtil.readCSV(inputCSV);
        if (inputData.isEmpty()) {
            System.err.println(" ERROR: No cargo data found.");
            return;
        }

        List<String[]> cargoData = new ArrayList<>();
        cargoData.add(new String[]{"cluster_id", "large_boxes", "medium_boxes", "small_boxes"});

        for (int i = 1; i < inputData.size(); i++) {
            int clusterId = (int) (Math.random() * 20);
            int large = Integer.parseInt(inputData.get(i)[2]);
            int medium = Integer.parseInt(inputData.get(i)[3]);
            int small = Integer.parseInt(inputData.get(i)[4]);

            cargoData.add(new String[]{String.valueOf(clusterId), String.valueOf(large), String.valueOf(medium), String.valueOf(small)});
        }

        csvUtil.writeCSV(cargoCSV, cargoData);
    }
}
