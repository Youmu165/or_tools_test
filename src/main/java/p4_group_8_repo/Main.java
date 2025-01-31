package p4_group_8_repo;
import p4_group_8_repo.controller.KmeansCluster;
import p4_group_8_repo.controller.Packing;
import p4_group_8_repo.controller.Route;

import p4_group_8_repo.utils.csvUtil;
import p4_group_8_repo.utils.Constants;
import p4_group_8_repo.utils.DataGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Starting Logistics Optimization System...");


        System.out.println("Step 1: Generating test data...");
        DataGenerator.generateData();


        System.out.println("Step 2: Clustering orders...");
        KmeansCluster.clusterOrders("src/main/resources/input_data.csv", "src/main/resources/cluster_coordinates.csv", 20);


        System.out.println("Step 3: Computing optimal routes between clusters...");
        Route.generateRoutes("src/main/resources/cluster_coordinates.csv", "src/main/resources/routes.csv");


        System.out.println("Step 4: Assigning cargo to clusters...");
        Route.assignCargoToClusters("src/main/resources/input_data.csv", "src/main/resources/assigned_cargo.csv");

        System.out.println("Step 5: Assigning vehicles and generating XML...");
        Packing.assignVehicles("src/main/resources/routes.csv", "src/main/resources/assigned_cargo.csv", "src/main/resources/vehicle_types.csv", "src/main/resources/vehicle_routes.xml");

        System.out.println("All tasks completed!");
    }
}