import java.util.Random;

public class RestrictedSpots {
    public String spotID, spotName;
    double spotArea;  // in square meters
    int maxCapacity;
    int currentOccupancy;
    int avgTime; // in minutes

    public void updateSpotSize(double newArea){
        this.spotArea = newArea;
        this.maxCapacity = (int) (spotArea/1.0); // Recalculate the max capacity based on new area of each room
        if (this.currentOccupancy > this.maxCapacity){
            this.currentOccupancy = this.maxCapacity; // Make the current occupancy at max if occupancy exceed the max capacity
        }

    }

    public RestrictedSpots(String id, String name, double area, int time) {
        this.spotID = id;
        this.spotName = name;
        this.spotArea = area;
        this.avgTime = time;
        this.maxCapacity = (int) (spotArea / 1.0); // Assuming 1 meter² per person for social distancing
        this.currentOccupancy = new Random().nextInt(maxCapacity + 1); // Random people count
    }

    public boolean canEnter() {
        return currentOccupancy < maxCapacity;
    }

    public void displayInfo() {
        System.out.println("\nSpot ID: " + spotID + ", Name: " + spotName);
        System.out.println("Spot Area: " + spotArea + " m², Max Capacity: " + maxCapacity);
        System.out.println("Current Occupancy: " + currentOccupancy);
    }
}
