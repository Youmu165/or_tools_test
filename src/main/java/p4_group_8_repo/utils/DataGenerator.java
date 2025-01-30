package p4_group_8_repo.utils;

import java.io.*;
import java.util.Random;

public class DataGenerator
{

    // all coordinates in UK main land
    private static final double [] [] Locations = {
            {51.509865, -0.118092},
            {52.489471, -1.898575},
            {55.860916, -4.251433},
            {53.400002, -2.983333},
            {53.801277, -1.548567},
            {53.383331, -1.466667},
            {55.953251, -3.188267},
            {51.454514, -2.587910},
            {53.483959, -2.244644},
            {52.633331, -1.133333},
            {52.408054, -1.510556},
            {53.767750, -0.335827},
            {51.481583, -3.179090},
            {53.799999, -1.750000},
            {53.480759, -2.242631},
            {53.002666, -2.179404},
            {52.591370, -2.110748},
            {50.376289, -4.143841},
            {52.950001, -1.150000},
            {50.909698, -1.404351},
            {51.454006, -0.978400},
            {52.922530, -1.474620},
            {52.512791, -2.081000},
            {52.240479, -0.902656},
            {50.805832, -1.087222},
            {51.878671, -0.420025},
            {54.966667, -1.600000},
            {53.763201, -2.703090},
            {51.361760, -0.194100},
            {52.040623, -0.759417},
            {57.149651, -2.099075},
            {54.906101, -1.381130},
            {52.630886, 1.297355},
            {52.586214, -1.982919},
            {51.621441, -3.943646},
            {50.720806, -1.904755},
            {51.537820, 0.714800},
            {51.568535, -1.772232},
            {56.462002, -2.970700},
            {53.645792, -1.785035},
            {50.715050, -1.987248},
            {51.752022, -1.257677},
            {54.574227, -1.234956},
            {53.817505, -3.035675},
            {53.576866, -2.429632},
            {52.059170, 1.155450},
            {52.676600, -2.449260},
            {53.958332, -1.080278},
            {52.519200, -1.995200},
            {52.573921, -0.250830}

    };

    //number of boxes in each location

    private static final int LARGE_BOX_MAX = 5;
    private static final int MEDIUM_BOX_MAX = 5;
    private static final int SMALL_BOX_MAX = 5;

    //the route to the csv file
    private static final String OUTPUT_CSV = "src/main/resources/input_data.csv";


    //this method will generate data by randomly assign packages in uk
    public static void generateData() throws IOException {
        Random rand = new Random();
        //start the writer
        try (BufferedWriter writer =  new BufferedWriter(new FileWriter(OUTPUT_CSV)))
        {
            writer.write("latitude,longitude,large,medium,small\\n");
            for (double[] location : Locations)
            {
                //generate the number of boxes in each location
                int large = rand.nextInt(LARGE_BOX_MAX + 1);
                int medium = rand.nextInt(MEDIUM_BOX_MAX + 1);
                int small = rand.nextInt(SMALL_BOX_MAX + 1);

                writer.write(location[0] + "," + location[1] + "," + large + "," + medium + "," + small + "\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();

        }
    }

}
