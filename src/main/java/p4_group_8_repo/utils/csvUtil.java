package p4_group_8_repo.utils;
import java.io.*;
import java.util.*;


public class csvUtil
{

    public static int[][] loadDistanceMatrix(String filePath) {
        List<int[]> matrixList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                int[] row = Arrays.stream(values).mapToInt(Integer::parseInt).toArray();
                matrixList.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return matrixList.toArray(new int[0][0]);
    }

    public static List<String[]> readCSV(String filePath) {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                data.add(line.split(","));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void writeCSV(String filePath, List<String[]> data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String[] row : data) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
