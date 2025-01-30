package p4_group_8_repo.controller;
import java.util.*;

import org.apache.commons.math3.ml.clustering.*;
import p4_group_8_repo.utils.csvUtil;

public class KmeansCluster
{
    public static List<CentroidCluster<ClusterableLocation>> performClustering(List<double[]> coordinates, int numClusters) {
        KMeansPlusPlusClusterer<ClusterableLocation> clusterer = new KMeansPlusPlusClusterer<>(numClusters, 100);
        List<ClusterableLocation> clusterInput = new ArrayList<>();

        for (double[] coord : coordinates) {
            clusterInput.add(new ClusterableLocation(coord));
        }

        return clusterer.cluster(clusterInput);
    }



    static class ClusterableLocation implements Clusterable {
        private final double[] points;

        public ClusterableLocation(double[] points) {
            this.points = points;
        }

        @Override
        public double[] getPoint() {
            return points;
        }
    }


    public static void clusterOrders(String inputCSV, String outputCSV, int numClusters) {
        List<String[]> rawData = csvUtil.readCSV(inputCSV);
        List<double[]> coordinates = new ArrayList<>();

        for (int i = 1; i < rawData.size(); i++) {
            double lat = Double.parseDouble(rawData.get(i)[0]);
            double lon = Double.parseDouble(rawData.get(i)[1]);
            coordinates.add(new double[]{lat, lon});
        }

        List<CentroidCluster<ClusterableLocation>> clusters = performClustering(coordinates, numClusters);
        List<String[]> clusterData = new ArrayList<>();
        clusterData.add(new String[]{"cluster_id", "latitude", "longitude"});

        int clusterId = 0;
        for (Cluster<ClusterableLocation> cluster : clusters) {
            double sumLat = 0, sumLon = 0;
            for (ClusterableLocation point : cluster.getPoints()) {
                sumLat += point.getPoint()[0];
                sumLon += point.getPoint()[1];
            }
            double avgLat = sumLat / cluster.getPoints().size();
            double avgLon = sumLon / cluster.getPoints().size();

            clusterData.add(new String[]{
                    String.valueOf(clusterId),
                    String.valueOf(avgLat),
                    String.valueOf(avgLon)
            });
            clusterId++;
        }

        csvUtil.writeCSV(outputCSV, clusterData);
        System.out.println("Clustering completed and saved in " + outputCSV);
    }
//    public static void main(String[] args) {
//        List<String[]> rawData = csvUtil.readCSV("src/main/resources/input_data.csv");
//        List<double[]> coordinates = new ArrayList<>();
//
//        for (int i = 1; i < rawData.size(); i++) {
//            double lat = Double.parseDouble(rawData.get(i)[0]);
//            double lon = Double.parseDouble(rawData.get(i)[1]);
//            coordinates.add(new double[]{lat, lon});
//        }
//
//        List<CentroidCluster<ClusterableLocation>> clusters = performClustering(coordinates, Math.min(coordinates.size(), 20));
//
//        System.out.println("Generated " + clusters.size() + " clusters.");
//    }
}
