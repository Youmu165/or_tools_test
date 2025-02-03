package p4_group_8_repo.model;

public class Vehicle
{
    private String type;
    private double length;
    private double width;
    private double height;
    private int maxWeight;
    private int price;
    private double maxVolume;

    public Vehicle(String type, double length, double width, double height, int maxWeight, int price) {
        this.type = type;
        this.length = length;
        this.width = width;
        this.height = height;
        this.maxWeight = maxWeight;
        this.price = price;
        this.maxVolume = length * width * height;
    }

    public String getType() { return type; }
    public double getLength() { return length; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public int getMaxWeight() { return maxWeight; }
    public int getPrice() { return price; }
    public double getMaxVolume() { return maxVolume; }
}
