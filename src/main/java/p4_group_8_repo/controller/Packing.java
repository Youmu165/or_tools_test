package p4_group_8_repo.controller;

import p4_group_8_repo.model.Vehicle;
import p4_group_8_repo.utils.Constants;
import p4_group_8_repo.utils.csvUtil;

import java.util.*;

public class Packing {
    private static final double EARTH_RADIUS = 6371.0;


    /**
     * Use Haversine to compute the distance between two group of coordinates
     * @param lat1 The latitude of start point
     * @param lon1 The longitude of start point
     * @param lat2 The latitude of end point
     * @param lon2 The longitude of end point
     * @return The distance of two different point (Kilo meters)
     */
    private static double computeDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(radLat1) * Math.cos(radLat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    /**
     * Load cluster information
     * @param clusterData The cluster data in csv format
     * @return The mapping data: cluster ID -> Array list of lats and lons
     */
    private static Map<Integer, double[]> loadClusterCoordinates(List<String[]> clusterData) {
        Map<Integer, double[]> clusterCoordinates = new HashMap<>();

        for (int i = 1; i < clusterData.size(); i++) {
            String[] row = clusterData.get(i);
            try {
                int clusterId = Integer.parseInt(row[0]);
                double latitude = Double.parseDouble(row[1]);
                double longitude = Double.parseDouble(row[2]);
                clusterCoordinates.put(clusterId, new double[]{latitude, longitude});
            } catch (NumberFormatException e) {
                System.err.println("Error parsing cluster data at line " + (i + 1));
            }
        }
        return clusterCoordinates;
    }


    public static void assignVehicles(String routesCSV, String clusterCSV, String vehicleCSV) {
        List<String[]> routesData = csvUtil.readCSV(routesCSV);
        List<String[]> clusterData = csvUtil.readCSV(clusterCSV);

        if (routesData.isEmpty() || clusterData.isEmpty()) {
            System.err.println("ERROR: Missing route or cluster data.");
            return;
        }

        // Load the information of cluster coordinates
        Map<Integer, double[]> clusterCoordinates = loadClusterCoordinates(clusterData);
        if (clusterCoordinates.isEmpty()) {
            System.err.println("ERROR: No valid cluster coordinates loaded.");
            return;
        }



        //The max distance vehicle can travel
        double vehicleCapacity = 500.0;

        //id counter
        int currentVehicleId = 1;

        //The distance the vehicle will travel
        double currentVehicleDistance = 0.0;


        //The path for vehicle
        List<String> currentRouteIds = new ArrayList<>();
        List<String> currentRoutes = new ArrayList<>();

        List<String[]> vehicleAssignments = new ArrayList<>();

        vehicleAssignments.add(new String[]{"vehicle_id", "route_ids", "routes", "vehicle_type"});


        //Select vehicle type
        Vehicle selectedVehicle = Constants.VEHICLE_TYPES.get(0);


        //Assign the route
        for (int i = 1; i < routesData.size(); i++) {
            String[] routeRow = routesData.get(i);
            String routeId = routeRow[0];
            String routePath = routeRow[1];

            //calculate the total distance
            double routeDistance = 0.0;
            String[] clusters = routePath.split("->");
            for (int j = 0; j < clusters.length - 1; j++) {
                try {
                    int fromCluster = Integer.parseInt(clusters[j]);
                    int toCluster = Integer.parseInt(clusters[j + 1]);

                    //Check if the cluster point exist
                    if (!clusterCoordinates.containsKey(fromCluster) || !clusterCoordinates.containsKey(toCluster)) {
                        System.err.println("Cluster id not found in cluster data for route " + routeId);
                        continue;
                    }
                    //Get the coordinate and calculate the distance
                    double[] fromCoord = clusterCoordinates.get(fromCluster);
                    double[] toCoord = clusterCoordinates.get(toCluster);
                    routeDistance += computeDistance(fromCoord[0], fromCoord[1], toCoord[0], toCoord[1]);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing cluster id in route " + routeId);
                }
            }

            // Check if the vehicle is not suitable for the route
            if (currentVehicleDistance + routeDistance > vehicleCapacity) {
                String joinedRouteIds = String.join(",", currentRouteIds);
                String joinedRoutes = String.join(";", currentRoutes);
                vehicleAssignments.add(new String[]{
                        String.valueOf(currentVehicleId),
                        joinedRouteIds,
                        joinedRoutes,
                        selectedVehicle.getType()
                });

                //Initialize a new vehicle
                currentVehicleId++;

                currentVehicleDistance = 0.0;
                currentRouteIds.clear();
                currentRoutes.clear();
            }

            //Add route to current vehicle
            currentRouteIds.add(routeId);
            currentRoutes.add(routePath);
            currentVehicleDistance += routeDistance;
        }


        // Deal with the last unsaved vehicle
        if (!currentRouteIds.isEmpty()) {
            String joinedRouteIds = String.join(",", currentRouteIds);
            String joinedRoutes = String.join(";", currentRoutes);
            vehicleAssignments.add(new String[]{
                    String.valueOf(currentVehicleId),
                    joinedRouteIds,
                    joinedRoutes,
                    selectedVehicle.getType()
            });
        }


        csvUtil.writeCSV(vehicleCSV, vehicleAssignments);
        System.out.println("Vehicles assigned and saved in " + vehicleCSV);
    }
}
