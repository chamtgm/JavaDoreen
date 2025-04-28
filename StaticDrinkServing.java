import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StaticDrinkServing {

    //ANSI color codes
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_ORANGE = "\u001B[38;5;208m";
    private static final String ANSI_GREEN ="\u001B[0;32m";
    private static final String ANSI_BLUE = "\u001B[34m";

    // Pattern to match ANSI escape codes
    private static final Pattern ANSI_PATTERN = Pattern.compile("\u001B\\[[;\\d]*m");

    private static Scanner scanner = new Scanner(System.in);
    public static RestrictedSpots[] spots = {
        new RestrictedSpots("S1", "Dining Foyer", 120, 30),
        new RestrictedSpots("S2", "Main Dining Hall", 300, 45),
        new RestrictedSpots("S3", "Dining Room One", 215, 25),
        new RestrictedSpots("S4", "Dining Room Two", 150, 20),
        new RestrictedSpots("S5", "Family Dining Room", 250, 60)
    };

    // Helper function to get the visible length of a string (stripping ANSI codes)
    private static int getVisibleLength(String str) {
        if (str == null) {
            return 0;
        }
        return ANSI_PATTERN.matcher(str).replaceAll("").length();
    }

    // Helper function to create a padded string, accounting for ANSI codes
    private static String padRight(String str, int visibleWidth) {
        int visibleLength = getVisibleLength(str);
        int paddingNeeded = Math.max(0, visibleWidth - visibleLength);
        return str + " ".repeat(paddingNeeded);
    }


    public static void main(String[] args) {
        System.out.println(ANSI_BLUE + "Welcome to the Drink Serving Robotic System!" + ANSI_RESET);
        
        // Initial room size configuration
        configureRoomSizes();
        
        while (true) {
            displayAvailableSpots(); // Display current status first
            suggestEntrySequence(); // Then display the suggestion
            System.out.print("\nSelect a spot (1-5) or 0 to exit: ");
            
            if (!scanner.hasNextInt()) { 
                System.out.println(ANSI_RED + "Invalid choice! Please enter a number between 1 and 5, or 0 to exit." + ANSI_RESET);
                scanner.next(); // Clear invalid input
                continue;
            }

            int choice = scanner.nextInt();

            if (choice == 0) break;
            if (choice < 1 || choice > 5) {
                System.out.println(ANSI_RED + "Invalid choice! Try again." + ANSI_RESET);
                continue;
            }

            RestrictedSpots selectedSpot = spots[choice - 1];
            // No need to display info here again, as it's shown in the table
            // selectedSpot.displayInfo(); 

            if (selectedSpot.canEnter()) {
                System.out.println(ANSI_GREEN + "\nEntrance permitted for " + selectedSpot.spotName + ". Proceeding to dynamic distancing check..." + ANSI_RESET);
                // Assuming Robot class exists and you have an instance
                Robot defaultRobot = new Robot("R001", "DefaultBot", 3, false); 
                DynamicDrinkServing.checkDynamicDistancing(defaultRobot); // Pass robot instance
            } else {
                // Use the getEstimatedWaitTime method for consistency
                int simpleWaitTime = selectedSpot.getEstimatedWaitTime();
                System.out.println(ANSI_ORANGE + "\n" + selectedSpot.spotName + " is full! Estimated wait time: " + simpleWaitTime + " minutes." + ANSI_RESET);
                
                String waitChoice;
                while (true) {
                    System.out.print("Do you want to wait? (yes/no): ");
                    waitChoice = scanner.next().toLowerCase();

                    if (waitChoice.equals("yes")) {
                        System.out.println(ANSI_ORANGE + "Robot is now allowed to enter " + selectedSpot.spotName + " after waiting " + simpleWaitTime + " minutes." + ANSI_RESET);
                         // Assuming Robot class exists and you have an instance
                        Robot defaultRobot = new Robot("R001", "DefaultBot", 3, false);
                        DynamicDrinkServing.checkDynamicDistancing(defaultRobot); // Pass robot instance
                        break;
                    } else if (waitChoice.equals("no")) {
                        System.out.println("Select another spot.");
                        break;
                    } else {
                        System.out.println(ANSI_RED + "Invalid input! Please try again." + ANSI_RESET);
                    }
                }
            }
        }
        System.out.println("System exited. Goodbye!");
    }
    
    private static void configureRoomSizes() {
        System.out.println("\n" + ANSI_BLUE + "=== INITIAL ROOM SIZE CONFIGURATION ===" + ANSI_RESET);
        System.out.println("You must configure the sizes of all dining areas before proceeding.");
        
        // Display just the table for initial configuration
        displayRoomSizesTable();
        
        boolean configDone = false;
        
        while (!configDone) {
            System.out.println("\nRoom Size Configuration Options:");
            System.out.println("1-5: Select a room to modify its size");
            System.out.println("0: Finish configuration and proceed");
            
            System.out.print("\nEnter your choice: ");
            
            if (!scanner.hasNextInt()) {
                System.out.println(ANSI_RED + "Invalid input! Please enter a number." + ANSI_RESET);
                scanner.next(); // Clear invalid input
                continue;
            }
            
            int choice = scanner.nextInt();
            
            if (choice == 0) {
                // Ask for confirmation to finish configuration
                System.out.print("Are you sure you want to finish configuration? (yes/no): ");
                String confirm = scanner.next().toLowerCase();
                
                if (confirm.equals("yes")) {
                    configDone = true;
                    System.out.println(ANSI_GREEN + "Room configuration completed!" + ANSI_RESET);
                }
            } else if (choice >= 1 && choice <= 5) {
                RestrictedSpots selectedSpot = spots[choice - 1];
                System.out.println("\nSelected: " + selectedSpot.spotName + " (Current size: " + selectedSpot.spotArea + " m²)");
                
                System.out.print("Enter new size in square meters: ");
                if (scanner.hasNextDouble()) {
                    double newSize = scanner.nextDouble();
                    if (newSize > 0) {
                        selectedSpot.updateSpotSize(newSize);
                        System.out.println(ANSI_GREEN + "Room size updated successfully!" + ANSI_RESET);
                    } else {
                        System.out.println(ANSI_RED + "Room size must be positive. No changes made." + ANSI_RESET);
                    }
                } else {
                    System.out.println(ANSI_RED + "Invalid input. Room size not updated." + ANSI_RESET);
                    scanner.next(); // Clear invalid input
                }
                
                // Display updated table
                displayRoomSizesTable();
            } else {
                System.out.println(ANSI_RED + "Invalid choice! Please select 0-5." + ANSI_RESET);
            }
        }
    }
    
    private static void displayRoomSizesTable() {
        // Define column widths (visible characters)
        int numWidth = 3;
        int idWidth = 7;
        int nameWidth = 18;
        int areaWidth = 9;

        System.out.println("\n" + ANSI_BLUE + "╔═════╦═════════╦════════════════════╦═══════════╗");
        System.out.println("║             AVAILABLE DINING SPOTS             ║");
        System.out.println("╠═════╬═════════╬════════════════════╬═══════════╣");
        System.out.printf("║ %-" + numWidth + "s ║ %-" + idWidth + "s ║ %-" + nameWidth + "s ║ %-" + areaWidth + "s ║\n", "#", "Spot ID", "Name", "Area");
        System.out.println("╠═════╬═════════╬════════════════════╬═══════════╣" + ANSI_RESET);
        
        for (int i = 0; i < spots.length; i++) {
            RestrictedSpots spot = spots[i];
            String areaStr = String.format("%.1f m²", spot.spotArea);
            // Use padRight for consistent formatting
            System.out.printf("║ %s ║ %s ║ %s ║ %s ║\n", 
                             padRight(String.valueOf(i + 1), numWidth), 
                             padRight(spot.spotID, idWidth), 
                             padRight(spot.spotName, nameWidth), 
                             padRight(areaStr, areaWidth));
        }
        
        System.out.println(ANSI_BLUE + "╚═════╩═════════╩════════════════════╩═══════════╝" + ANSI_RESET);
    }
    
    private static void displayAvailableSpots() {
        // Define column widths (visible characters)
        int numWidth = 3;
        int nameWidth = 21;
        int occupancyWidth = 14; // e.g., "XXX/XXX" or "FULL"
        int waitTimeWidth = 19; // e.g., "No wait" or "XX minutes"

        // Display the table header
        System.out.println("\n" + ANSI_BLUE + "=== CURRENT OCCUPANCY STATUS ===" + ANSI_RESET);
        System.out.println(ANSI_BLUE + "╔═════╦═══════════════════════╦════════════════╦═════════════════════╗");
        System.out.printf("║ %-" + numWidth + "s ║ %-" + nameWidth + "s ║ %-" + occupancyWidth + "s ║ %-" + waitTimeWidth + "s ║\n", "#", "Name", "Occupancy", "Wait Time");
        System.out.println("╠═════╬═══════════════════════╬════════════════╬═════════════════════╣" + ANSI_RESET);

        // Then show occupancy information with wait time calculation in table format
        for (int i = 0; i < spots.length; i++) {
            RestrictedSpots spot = spots[i];
            String occupancyStatus;
            String waitTimeStr; // Renamed to avoid conflict
            
            if (spot.canEnter()) {
                // Room is available
                occupancyStatus = String.format("%s%d/%d%s", ANSI_GREEN, spot.currentOccupancy, spot.maxCapacity, ANSI_RESET);
                waitTimeStr = ANSI_GREEN + "No wait" + ANSI_RESET;
            } else {
                // Room is full or over capacity
                // Option 2: Show max/max when full
                occupancyStatus = String.format("%s%d/%d%s", ANSI_RED, spot.maxCapacity, spot.maxCapacity, ANSI_RESET); 
                
                int waitTimeValue = spot.getEstimatedWaitTime();
                waitTimeStr = ANSI_ORANGE + waitTimeValue + " minutes" + ANSI_RESET;
            }
            
            // Use padRight helper for consistent padding regardless of ANSI codes
            System.out.printf("║ %s ║ %s ║ %s ║ %s ║\n", 
                             padRight(String.valueOf(i + 1), numWidth), 
                             padRight(spot.spotName, nameWidth), 
                             padRight(occupancyStatus, occupancyWidth),
                             padRight(waitTimeStr, waitTimeWidth));
        }
        // Display the table footer
        System.out.println(ANSI_BLUE + "╚═════╩═══════════════════════╩════════════════╩═════════════════════╝" + ANSI_RESET);
    }
    
    private static void suggestEntrySequence() {
        System.out.println("\n" + ANSI_BLUE + "--- SUGGESTED ENTRY SEQUENCE ---" + ANSI_RESET); // Changed header style
        
        // Create a copy of spots array to sort without modifying original
        RestrictedSpots[] sortedSpots = spots.clone();
        
        // Sort spots by availability first, then by wait time (Bubble Sort)
        for (int i = 0; i < sortedSpots.length - 1; i++) {
            for (int j = 0; j < sortedSpots.length - i - 1; j++) {
                // Get availability status
                boolean spot1Available = sortedSpots[j].canEnter();
                boolean spot2Available = sortedSpots[j+1].canEnter();
                
                // Calculate wait times
                int waitTime1 = spot1Available ? 0 : sortedSpots[j].getEstimatedWaitTime();
                int waitTime2 = spot2Available ? 0 : sortedSpots[j+1].getEstimatedWaitTime();
                
                boolean swap = false;
                // Condition 1: Spot 1 is full, Spot 2 is available -> Swap
                if (!spot1Available && spot2Available) {
                    swap = true;
                } 
                // Condition 2: Both have same availability, but Spot 1 has longer wait time -> Swap
                else if (spot1Available == spot2Available && waitTime1 > waitTime2) {
                     swap = true;
                }

                if (swap) {
                    RestrictedSpots temp = sortedSpots[j];
                    sortedSpots[j] = sortedSpots[j+1];
                    sortedSpots[j+1] = temp;
                }
            }
        }
        
        // Display the sorted rooms as a numbered list
        System.out.println("Based on current occupancy, here's the recommended sequence:");
        
        for (int i = 0; i < sortedSpots.length; i++) {
            RestrictedSpots spot = sortedSpots[i];
            String status;
            String waitTimeInfo; // Renamed
            
            if (spot.canEnter()) {
                status = ANSI_GREEN + "AVAILABLE" + ANSI_RESET;
                waitTimeInfo = "(" + ANSI_GREEN + "No wait" + ANSI_RESET + ")";
            } else {
                status = ANSI_RED + "FULL" + ANSI_RESET;
                int waitTimeValue = spot.getEstimatedWaitTime();
                waitTimeInfo = "(Wait: " + ANSI_ORANGE + waitTimeValue + " minutes" + ANSI_RESET + ")";
            }
            
            // Print as a numbered list item
            System.out.printf("%d. %-21s: %s %s\n", 
                            (i + 1), 
                            spot.spotName, 
                            status,
                            waitTimeInfo);
        }
        System.out.println(ANSI_BLUE + "----------------------------------" + ANSI_RESET); // Footer line
    }
}