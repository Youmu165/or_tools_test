package p4_group_8_repo.controller;

import p4_group_8_repo.model.Vehicle;
import p4_group_8_repo.utils.Constants;
import p4_group_8_repo.utils.csvUtil;

import java.util.*;

public class Packing {

    private static double computeDistance(double lat1, double lon1, double lat2, double lon2) {
        return Math.sqrt(Math.pow(lat2 - lat1, 2) + Math.pow(lon2 - lon1, 2));
    }

    public static void assignVehicles(String routesCSV, String clusterCSV, String vehicleCSV) {
        List<String[]> routesData = csvUtil.readCSV(routesCSV);
        List<String[]> clusterData = csvUtil.readCSV(clusterCSV);

        if (routesData.isEmpty() || clusterData.isEmpty()) {
            System.err.println("ERROR: Missing route or cluster data.");
            return;
        }

        List<String[]> assignedVehicles = new ArrayList<>();
        assignedVehicles.add(new String[]{"route_id", "vehicle_id", "route", "vehicle_type"});

        int vehicleId = 101;

        for (int i = 1; i < routesData.size(); i++) {
            int routeId = Integer.parseInt(routesData.get(i)[0]);
            String[] clusters = routesData.get(i)[1].split("->");

            double totalDistance = 0;
            for (int j = 0; j < clusters.length - 1; j++) {
                int fromCluster = Integer.parseInt(clusters[j]);
                int toCluster = Integer.parseInt(clusters[j + 1]);
                totalDistance += computeDistance(fromCluster, toCluster, fromCluster + 1, toCluster + 1);
            }

            if (totalDistance > Constants.MAX_DISTANCE) {
                vehicleId++;
            }

            Vehicle selectedVehicle = Constants.VEHICLE_TYPES.get(0);
            assignedVehicles.add(new String[]{String.valueOf(routeId), String.valueOf(vehicleId), routesData.get(i)[1], selectedVehicle.getType()});
        }

        csvUtil.writeCSV(vehicleCSV, assignedVehicles);
        System.out.println("Vehicles assigned and saved in " + vehicleCSV);
    }
}
