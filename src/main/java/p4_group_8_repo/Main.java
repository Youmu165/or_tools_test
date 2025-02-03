package p4_group_8_repo;

import p4_group_8_repo.controller.KmeansCluster;
import p4_group_8_repo.controller.Packing;
import p4_group_8_repo.controller.Route;
import p4_group_8_repo.utils.DataGenerator;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Starting Logistics Optimization System...");

        // Step 1: Generate test data
        System.out.println("Step 1: Generating test data...");
        DataGenerator.generateData();

        // Step 2: Cluster orders
        System.out.println("Step 2: Clustering orders...");
        KmeansCluster.clusterOrders(
                "src/main/resources/input_data.csv",
                "src/main/resources/cluster_coordinates.csv",
                20
        );

        // Step 3: Compute optimized routes with multiple stops
        System.out.println("Step 3: Computing optimized routes...");
        Route.generateRoutes(
                "src/main/resources/cluster_coordinates.csv",
                "src/main/resources/routes.csv"
        );

        // Step 4: Assign vehicles, ensuring distance constraints
        System.out.println("Step 4: Assigning vehicles while optimizing fleet usage...");
        Packing.assignVehicles(
                "src/main/resources/routes.csv",
                "src/main/resources/cluster_coordinates.csv",
                "src/main/resources/vehicle_assignments.csv"
        );

        System.out.println("All tasks completed!");
    }
}
