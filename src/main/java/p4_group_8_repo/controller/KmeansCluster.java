package p4_group_8_repo.controller;

import org.apache.commons.math3.ml.clustering.*;
import p4_group_8_repo.utils.Constants;
import p4_group_8_repo.utils.csvUtil;

import java.util.*;

public class KmeansCluster {


    public static List<CentroidCluster<ClusterableLocation>> performClustering(List<ClusterableLocation> clusterableLocations, int numClusters) {
            KMeansPlusPlusClusterer<ClusterableLocation> clusterer = new KMeansPlusPlusClusterer<>(numClusters, 100);//100 rounds

            return clusterer.cluster(clusterableLocations);
    }

    /**
     * ClusterableLocation represents a data point for K-means++ clustering.
     */
    static class ClusterableLocation implements Clusterable {
        private final double[] point;

        //private final double[] point;
        private final int large;
        private final int medium;
        private final int small;

        public ClusterableLocation(double lat, double lon, int large, int medium, int small) {
            this.point = new double[]{lat, lon};
            this.large = large;
            this.medium = medium;
            this.small = small;
        }

        @Override
        public double[] getPoint() {
            return point;
        }

        public int getLarge() {
            return large;
        }

        public int getMedium() {
            return medium;
        }

        public int getSmall() {
            return small;
        }
    }

    /**
     * Reads data from a CSV file, performs clustering, and saves the results and cargo information.
     *
     * @param inputCSV  Path to the input CSV file containing coordinates.
     * @param outputCSV Path to the output CSV file where clustered data will be stored.
     * @param numClusters The expected number of clusters.
     */
    public static void clusterOrders(String inputCSV, String outputCSV, int numClusters) {
        // Read coordinates from the CSV file.
        List<String[]> rawData = csvUtil.readCSV(inputCSV);
        List<double[]> coordinates = new ArrayList<>();
        List<ClusterableLocation> clusterableLocations = new ArrayList<>();



        // Skip the header row and parse coordinates.
        for (int i = 1; i < rawData.size(); i++) {
            String[] row = rawData.get(i);
            double lat = Double.parseDouble(row[0]);
            double lon = Double.parseDouble(row[1]);
            int large = Integer.parseInt(row[2]);
            int medium = Integer.parseInt(row[3]);
            int small = Integer.parseInt(row[4]);
            clusterableLocations.add(new ClusterableLocation(lat, lon, large, medium, small));
        }

        // Perform K-means++ clustering.
        List<CentroidCluster<ClusterableLocation>> clusters = performClustering(clusterableLocations, numClusters);

        // Prepare the CSV output with headers.
        List<String[]> clusterData = new ArrayList<>();
        clusterData.add(new String[]{"cluster_id", "latitude", "longitude", "large_boxes", "medium_boxes", "small_boxes", "total_weight", "total_volume"});

        // Process each cluster to compute center points and generate cargo data.
        int clusterId = 0;


        for (Cluster<ClusterableLocation> cluster : clusters) {
            double sumLat = 0, sumLon = 0;
            int totalLarge = 0, totalMedium = 0, totalSmall = 0;

            // Aggregate coordinates and randomly assign cargo.
            for (ClusterableLocation point : cluster.getPoints()) {
                double[] coords = point.getPoint();
                sumLat += coords[0];
                sumLon += coords[1];

                totalLarge += point.getLarge();
                totalMedium += point.getMedium();
                totalSmall += point.getSmall();
                // Randomly generate cargo counts for this cluster
//                totalLarge += random.nextInt(10) + 1;
//                totalMedium += random.nextInt(20) + 1;
//                totalSmall += random.nextInt(30) + 1;
            }

            // Compute cluster center.
            int numPoints = cluster.getPoints().size();
            double avgLat = sumLat / numPoints;
            double avgLon = sumLon / numPoints;

            // Compute total weight and volume.
            int totalWeight = (totalLarge * Constants.LARGE_BOX_WT) +
                    (totalMedium * Constants.MEDIUM_BOX_WT) +
                    (totalSmall * Constants.SMALL_BOX_WT);

            double totalVolume = (totalLarge * Constants.LARGE_BOX_VOL) +
                    (totalMedium * Constants.MEDIUM_BOX_VOL) +
                    (totalSmall * Constants.SMALL_BOX_VOL);

            // Add the cluster data (with cargo information) to the CSV output.
            clusterData.add(new String[]{
                    String.valueOf(clusterId),
                    String.valueOf(avgLat),
                    String.valueOf(avgLon),
                    String.valueOf(totalLarge),
                    String.valueOf(totalMedium),
                    String.valueOf(totalSmall),
                    String.valueOf(totalWeight),
                    String.valueOf(totalVolume)
            });

            clusterId++;
        }

        // Write the clustered data to CSV.
        csvUtil.writeCSV(outputCSV, clusterData);
        System.out.println("Clustering completed and saved in " + outputCSV);
    }
}
