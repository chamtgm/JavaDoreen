import java.util.Random;

/**
 * Represents a restricted dining spot with capacity limits and occupancy tracking.
 * Calculates wait times based on current occupancy and average time spent per person.
 */
public class RestrictedSpots {
    public String spotID, spotName;
    double spotArea;  // in square meters
    int maxCapacity;
    int currentOccupancy;
    int avgTime; // in minutes

    /**
     * Updates the size (area) of the spot and recalculates its maximum capacity.
     * @param newArea The new area of the spot in square meters.
     */
    public void updateSpotSize(double newArea) {
        this.spotArea = newArea;
        this.maxCapacity = (int) (spotArea / 1.0); // Recalculate the max capacity based on new area
    }

    /**
     * Constructs a new RestrictedSpots object.
     * Initializes spot details, calculates initial max capacity based on area,
     * and sets a random initial occupancy, potentially exceeding max capacity.
     * @param id The unique identifier for the spot.
     * @param name The name of the spot.
     * @param area The area of the spot in square meters.
     * @param time The average time a person spends in the spot, in minutes.
     */
    public RestrictedSpots(String id, String name, double area, int time) {
        this.spotID = id;
        this.spotName = name;
        this.spotArea = area;
        this.avgTime = time;
        this.maxCapacity = (int) (spotArea / 1.0); // Assuming 1 meter² per person for social distancing

        // Generate random occupancy between 0 and the adjusted randomOccupancyLimit
        this.currentOccupancy = new Random().nextInt(Math.max(1, maxCapacity + 1));
    }
    
    /**
     * Calculates the estimated wait time in minutes if the spot is full.
     * If the spot is not full, returns 0.
     * The wait time is based on the number of people exceeding capacity (overflow)
     * and the average time spent per person. A minimum wait time of 5 minutes is enforced.
     * @return The estimated wait time in minutes, or 0 if entry is possible.
     */
    public int getEstimatedWaitTime() {
        if (currentOccupancy >= maxCapacity) {
            // Calculate wait time based on average time per person
            return avgTime* (currentOccupancy - maxCapacity + 1);
        }
        else {
            return 0; // No wait time if the room is not full
        }
    }

    /**
     * Displays the detailed information about the spot, including ID, name, area,
     * capacity, current occupancy, and estimated wait time if applicable.
     */
    public void displayInfo() {
        System.out.println("\nSpot ID: " + spotID + ", Name: " + spotName);
        System.out.println("Spot Area: " + spotArea + " m², Max Capacity: " + maxCapacity);
        System.out.println("Current Occupancy: " + currentOccupancy);
        
        if (currentOccupancy > maxCapacity) {
            System.out.println("Estimated Wait Time: " + getEstimatedWaitTime() + " minutes");
        }
    }

}
