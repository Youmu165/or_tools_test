package p4_group_8_repo.controller;

import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import p4_group_8_repo.utils.csvUtil;

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

        int size = coordinates.size();
        double[][] distanceMatrix = new double[size][size];
        EuclideanDistance distanceCalculator = new EuclideanDistance();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                distanceMatrix[i][j] = (i == j) ? 0 : distanceCalculator.compute(coordinates.get(i), coordinates.get(j));
            }
        }

        List<String[]> routeData = new ArrayList<>();
        routeData.add(new String[]{"from_cluster", "to_cluster", "distance"});
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                routeData.add(new String[]{String.valueOf(i), String.valueOf(j), String.valueOf(distanceMatrix[i][j])});
            }
        }
        csvUtil.writeCSV(routesCSV, routeData);
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
