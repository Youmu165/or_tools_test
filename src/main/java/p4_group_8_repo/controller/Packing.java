package p4_group_8_repo.controller;


import p4_group_8_repo.model.Vehicle;
import p4_group_8_repo.utils.Constants;
import p4_group_8_repo.utils.csvUtil;
import p4_group_8_repo.utils.xmlGenerator;

import java.util.*;

public class Packing
{
    public static void assignVehicles(String routesCSV, String cargoCSV, String vehicleCSV, String vehicleXML) {
        List<String[]> routesData = csvUtil.readCSV(routesCSV);
        List<String[]> cargoData = csvUtil.readCSV(cargoCSV);
        if (routesData.isEmpty() || cargoData.isEmpty()) {
            System.err.println("ERROR: Missing data for vehicle assignment.");
            return;
        }

        Map<Integer, int[]> clusterCargo = new HashMap<>();
        for (int i = 1; i < cargoData.size(); i++) {
            int clusterId = Integer.parseInt(cargoData.get(i)[0]);
            int large = Integer.parseInt(cargoData.get(i)[1]);
            int medium = Integer.parseInt(cargoData.get(i)[2]);
            int small = Integer.parseInt(cargoData.get(i)[3]);

            clusterCargo.put(clusterId, new int[]{large, medium, small});
        }


        List<String[]> assignedVehicles = new ArrayList<>();
        assignedVehicles.add(new String[]{"vehicle_id", "large_boxes", "medium_boxes", "small_boxes"});

        List<Map<String, Object>> vehicleRoutes = new ArrayList<>();
        int vehicleId = 1;
        Map<Integer, List<Integer>> vehicleLoads = new HashMap<>();


        for (int i = 1; i < routesData.size(); i++) {
            int fromCluster = Integer.parseInt(routesData.get(i)[0]);
            int toCluster = Integer.parseInt(routesData.get(i)[1]);

            if (!clusterCargo.containsKey(fromCluster)) continue;
            int[] cargo = clusterCargo.get(fromCluster);


            boolean assigned = false;
            for (Map.Entry<Integer, List<Integer>> entry : vehicleLoads.entrySet()) {
                int existingVehicleId = entry.getKey();
                List<Integer> assignedClusters = entry.getValue();

                int totalLarge = cargo[0], totalMedium = cargo[1], totalSmall = cargo[2];
                for (int cluster : assignedClusters) {
                    if (clusterCargo.containsKey(cluster)) {
                        int[] existingCargo = clusterCargo.get(cluster);
                        totalLarge += existingCargo[0];
                        totalMedium += existingCargo[1];
                        totalSmall += existingCargo[2];
                    }
                }

                int totalWeight = totalLarge * Constants.LARGE_BOX_WT +
                        totalMedium * Constants.MEDIUM_BOX_WT +
                        totalSmall * Constants.SMALL_BOX_WT;

                for (Vehicle v : Constants.VEHICLE_TYPES) {
                    if (totalWeight <= v.getMaxWeight()) {
                        assignedClusters.add(fromCluster);
                        assigned = true;
                        break;
                    }
                }

                if (assigned) break;
            }


            if (!assigned) {
                for (Vehicle v : Constants.VEHICLE_TYPES) {
                    int totalWeight = cargo[0] * Constants.LARGE_BOX_WT +
                            cargo[1] * Constants.MEDIUM_BOX_WT +
                            cargo[2] * Constants.SMALL_BOX_WT;

                    if (totalWeight <= v.getMaxWeight()) {
                        assignedVehicles.add(new String[]{String.valueOf(vehicleId), String.valueOf(cargo[0]), String.valueOf(cargo[1]), String.valueOf(cargo[2])});


                        List<Integer> newVehicleClusters = new ArrayList<>();
                        newVehicleClusters.add(fromCluster);
                        vehicleLoads.put(vehicleId, newVehicleClusters);


                        Map<String, Object> vehicleInfo = new HashMap<>();
                        vehicleInfo.put("vehicle_id", vehicleId);
                        vehicleInfo.put("route", Arrays.asList(fromCluster, toCluster));
                        vehicleInfo.put("cargo", cargo);
                        vehicleRoutes.add(vehicleInfo);

                        vehicleId++;
                        break;
                    }
                }
            }
        }

        csvUtil.writeCSV(vehicleCSV, assignedVehicles);
        System.out.println("Vehicles assigned and saved in " + vehicleCSV);


        xmlGenerator.generateVehicleXML(vehicleXML, vehicleRoutes);
    }
}
