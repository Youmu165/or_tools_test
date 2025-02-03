package p4_group_8_repo.utils;

import p4_group_8_repo.model.Vehicle;

import java.util.Arrays;
import java.util.List;

public class Constants
{
    public static final double[] LARGE_BOX_DIM = {0.457, 0.457, 0.457};
    public static final double[] MEDIUM_BOX_DIM = {0.457, 0.457, 0.305};
    public static final double[] SMALL_BOX_DIM = {0.457, 0.305, 0.305};

    public static final double LARGE_BOX_VOL = LARGE_BOX_DIM[0] * LARGE_BOX_DIM[1] * LARGE_BOX_DIM[2];
    public static final double MEDIUM_BOX_VOL = MEDIUM_BOX_DIM[0] * MEDIUM_BOX_DIM[1] * MEDIUM_BOX_DIM[2];
    public static final double SMALL_BOX_VOL = SMALL_BOX_DIM[0] * SMALL_BOX_DIM[1] * SMALL_BOX_DIM[2];

    public static final int LARGE_BOX_WT = 40;
    public static final int MEDIUM_BOX_WT = 30;
    public static final int SMALL_BOX_WT = 20;

    public static final int MAX_DISTANCE = 400000;
    public static final int MAX_WEIGHT = 10000;


    public static final List<Vehicle> VEHICLE_TYPES = Arrays.asList(
            new Vehicle("Small Van", 1.7, 1.5, 1.2, 700, 10000),
            new Vehicle("Medium Van", 2.4, 1.7, 1.4, 1000, 10100),
            new Vehicle("Large Van", 3.4, 1.7, 1.7, 1500, 10200),
            new Vehicle("Extra Large Van", 6.1, 2.3, 2.4, 2500, 10300),
            new Vehicle("Van Trailer", 8.0, 2.3, 2.4, 5000, 10400),
            new Vehicle("Lorry", 14.0, 2.8, 2.8, 10000, 10500)
    );

}
