package p4_group_8_repo.model;

public class Cargo
{
    private double latitude;
    private double longitude;
    private int largeBoxes;
    private int mediumBoxes;
    private int smallBoxes;

    public Cargo(double latitude, double longitude, int largeBoxes, int mediumBoxes, int smallBoxes) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.largeBoxes = largeBoxes;
        this.mediumBoxes = mediumBoxes;
        this.smallBoxes = smallBoxes;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getLargeBoxes() {
        return largeBoxes;
    }

    public int getMediumBoxes() {
        return mediumBoxes;
    }

    public int getSmallBoxes() {
        return smallBoxes;
    }
}
