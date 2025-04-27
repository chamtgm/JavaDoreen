import java.util.Random;

public class RestrictedSpots {
    public String spotID, spotName;
    double spotArea;  // in square meters
    int maxCapacity;
    int currentOccupancy;
    int avgTime; // in minutes

    public void updateSpotSize(double newArea){
        this.spotArea = newArea;
        this.maxCapacity = (int) (spotArea/1.0); // Recalculate the max capacity based on new area
        
        // We no longer automatically reduce occupancy to match max capacity
        // This allows for overflow calculations
    }

    public RestrictedSpots(String id, String name, double area, int time) {
        this.spotID = id;
        this.spotName = name;
        this.spotArea = area;
        this.avgTime = time;
        this.maxCapacity = (int) (spotArea / 1.0); // Assuming 1 meter² per person for social distancing
        
        // Modified line - add 50 to the original area before calculating random occupancy
        int randomOccupancyLimit = (int) ((spotArea + 25) / 1.0);
        
        // Note: We're not limiting to maxCapacity here to allow for overflow scenarios
        // Generate random occupancy between 0 and randomOccupancyLimit
        this.currentOccupancy = new Random().nextInt(randomOccupancyLimit + 1);
    }

    public boolean canEnter() {
        return currentOccupancy < maxCapacity;
    }
    
    // Add method to get wait time estimate
    public int getEstimatedWaitTime() {
        if (canEnter()) return 0;
        
        // Calculate wait based on how many people need to leave before entry is possible
        int overflow = currentOccupancy - maxCapacity + 1; // +1 to account for the robot wanting to enter
        
        // Formula: (overflow / maxCapacity) * avgTime gives proportional wait time
        int waitEstimate = (int) Math.ceil((double) overflow * avgTime / maxCapacity);
        
        // Ensure a minimum reasonable wait time
        return Math.max(waitEstimate, 5);
    }

    public void displayInfo() {
        System.out.println("\nSpot ID: " + spotID + ", Name: " + spotName);
        System.out.println("Spot Area: " + spotArea + " m², Max Capacity: " + maxCapacity);
        System.out.println("Current Occupancy: " + currentOccupancy);
        
        if (!canEnter()) {
            System.out.println("Estimated Wait Time: " + getEstimatedWaitTime() + " minutes");
        }
    }
}
