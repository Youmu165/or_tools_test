package p4_group_8_repo.controller;

import org.apache.commons.math3.ml.clustering.*;
import p4_group_8_repo.utils.Constants;
import p4_group_8_repo.utils.csvUtil;

import java.util.*;

public class KmeansCluster {

    /**
     * Perform K-means++ clustering on coordinate data.
     *
     * @param coordinates The coordinates that will be transformed into clusters.
     * @param numClusters The number of clusters.
     * @return The result of clustering, each cluster contains several ClusterableLocation.
     */
    public static List<CentroidCluster<ClusterableLocation>> performClustering(List<double[]> coordinates, int numClusters) {
        // Use K-means++ to create clusters, with a maximum of 100 optimization iterations.
        KMeansPlusPlusClusterer<ClusterableLocation> clusterer = new KMeansPlusPlusClusterer<>(numClusters, 100);
        List<ClusterableLocation> clusterInput = new ArrayList<>();

        // Convert coordinates into ClusterableLocation instances.
        for (double[] coord : coordinates) {
            clusterInput.add(new ClusterableLocation(coord));
        }

        // Perform clustering and return results.
        return clusterer.cluster(clusterInput);
    }

    /**
     * ClusterableLocation represents a data point for K-means++ clustering.
     */
    static class ClusterableLocation implements Clusterable {
        private final double[] points;

        /**
         * Constructor for a clusterable location.
         *
         * @param points The coordinate points (latitude, longitude).
         */
        public ClusterableLocation(double[] points) {
            this.points = points;
        }

        @Override
        public double[] getPoint() {
            return points;
        }
    }

    /**
     * Reads data from a CSV file, performs clustering, and saves the results along with cargo information.
     *
     * @param inputCSV  Path to the input CSV file containing coordinates.
     * @param outputCSV Path to the output CSV file where clustered data will be stored.
     * @param numClusters The expected number of clusters.
     */
    public static void clusterOrders(String inputCSV, String outputCSV, int numClusters) {
        // Read coordinates from the CSV file.
        List<String[]> rawData = csvUtil.readCSV(inputCSV);
        List<double[]> coordinates = new ArrayList<>();

        // Skip the header row and parse coordinates.
        for (int i = 1; i < rawData.size(); i++) {
            double lat = Double.parseDouble(rawData.get(i)[0]);
            double lon = Double.parseDouble(rawData.get(i)[1]);
            coordinates.add(new double[]{lat, lon});
        }

        // Perform K-means++ clustering.
        List<CentroidCluster<ClusterableLocation>> clusters = performClustering(coordinates, numClusters);

        // Prepare the CSV output with headers.
        List<String[]> clusterData = new ArrayList<>();
        clusterData.add(new String[]{"cluster_id", "latitude", "longitude", "large_boxes", "medium_boxes", "small_boxes", "total_weight", "total_volume"});

        // Process each cluster to compute center points and generate cargo data.
        int clusterId = 0;
        Random random = new Random(); // Random generator for cargo data

        for (Cluster<ClusterableLocation> cluster : clusters) {
            double sumLat = 0, sumLon = 0;
            int totalLarge = 0, totalMedium = 0, totalSmall = 0;

            // Aggregate coordinates and randomly assign cargo.
            for (ClusterableLocation point : cluster.getPoints()) {
                sumLat += point.getPoint()[0];
                sumLon += point.getPoint()[1];

                // Randomly generate cargo counts for this cluster
                totalLarge += random.nextInt(10) + 1; // 1 to 10 large boxes
                totalMedium += random.nextInt(20) + 1; // 1 to 20 medium boxes
                totalSmall += random.nextInt(30) + 1; // 1 to 30 small boxes
            }

            // Compute cluster center.
            double avgLat = sumLat / cluster.getPoints().size();
            double avgLon = sumLon / cluster.getPoints().size();

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
