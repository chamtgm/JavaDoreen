import java.util.Scanner;

public class StaticDrinkServing {

    //ANSI color codes
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_ORANGE = "\u001B[38;5;208m";
    private static final String ANSI_GREEN ="\u001B[0;32m";
    private static final String ANSI_BLUE = "\u001B[34m";

    private static Scanner scanner = new Scanner(System.in);
    public static RestrictedSpots[] spots = {
        new RestrictedSpots("S1", "Dining Foyer", 120, 30),
        new RestrictedSpots("S2", "Main Dining Hall", 300, 45),
        new RestrictedSpots("S3", "Dining Room One", 215, 25),
        new RestrictedSpots("S4", "Dining Room Two", 150, 20),
        new RestrictedSpots("S5", "Family Dining Room", 250, 60)
    };

    public static void main(String[] args) {
        System.out.println(ANSI_BLUE + "Welcome to the Drink Serving Robotic System!" + ANSI_RESET);
        
        // Initial room size configuration
        configureRoomSizes();
        
        while (true) {
            displayAvailableSpots();
            suggestEntrySequence(); // Add this line here
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
            selectedSpot.displayInfo();

            if (selectedSpot.canEnter()) {
                System.out.println(ANSI_GREEN + "Entrance permitted. Proceeding to dynamic distancing check..." + ANSI_RESET);
                DynamicDrinkServing.checkDynamicDistancing();
            } else {
                // Use the getEstimatedWaitTime method for consistency
                int simpleWaitTime = selectedSpot.getEstimatedWaitTime();
                System.out.println(ANSI_ORANGE + "Spot is full! Estimated wait time: " + simpleWaitTime + " minutes." + ANSI_RESET);
                
                String waitChoice;
                while (true) {
                    System.out.print("Do you want to wait? (yes/no): ");
                    waitChoice = scanner.next().toLowerCase();

                    if (waitChoice.equals("yes")) {
                        System.out.println(ANSI_ORANGE + "Robot is now allowed to enter after waiting " + simpleWaitTime + " minutes." + ANSI_RESET);
                        DynamicDrinkServing.checkDynamicDistancing();
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
        System.out.println("\n" + ANSI_BLUE + "╔════════════════════════════════════════════════╗");
        System.out.println("║             AVAILABLE DINING SPOTS             ║");
        System.out.println("╠═════╦═════════╦════════════════════╦═══════════╣");
        System.out.println("║  #  ║ Spot ID ║        Name        ║   Area    ║");
        System.out.println("╠═════╬═════════╬════════════════════╬═══════════╣" + ANSI_RESET);
        
        for (int i = 0; i < spots.length; i++) {
            RestrictedSpots spot = spots[i];
            System.out.printf("  %2d      %-3s     %-19s   %5.1f m² \n", 
                             (i + 1), 
                             spot.spotID, 
                             spot.spotName, 
                             spot.spotArea);
        }
        
        System.out.println(ANSI_BLUE + "╚═════╩═════════╩════════════════════╩═══════════╝" + ANSI_RESET);
    }
    
    private static void displayAvailableSpots() {
        // Display the table first
        displayRoomSizesTable();
        
        // Then show occupancy information with wait time calculation
        System.out.println("\nCurrent Occupancy Status:");
        for (int i = 0; i < spots.length; i++) {
            RestrictedSpots spot = spots[i];
            
            if (spot.canEnter()) {
                // Room is available
                System.out.printf("%d. %s - %s\n", 
                    (i + 1), 
                    spot.spotName, 
                    ANSI_GREEN + "AVAILABLE (" + spot.currentOccupancy + "/" + spot.maxCapacity + ")" + ANSI_RESET);
            } else {
                // Use the same getEstimatedWaitTime method for consistency
                int waitTime = spot.getEstimatedWaitTime();
                
                // For display purposes, show equal values for current and max occupancy
                System.out.printf("%d. %s - %s %s\n", 
                    (i + 1), 
                    spot.spotName, 
                    ANSI_RED + "FULL (" + spot.maxCapacity + "/" + spot.maxCapacity + ")" + ANSI_RESET,
                    ANSI_ORANGE + "Est. Wait: " + waitTime + " min" + ANSI_RESET);
            }
        }
    }
    
    private static void suggestEntrySequence() {
        System.out.println("\n" + ANSI_BLUE + "=== SUGGESTED ENTRY SEQUENCE ===" + ANSI_RESET);
        
        // Create a copy of spots array to sort without modifying original
        RestrictedSpots[] sortedSpots = spots.clone();
        
        // Sort spots by availability first, then by wait time
        for (int i = 0; i < sortedSpots.length - 1; i++) {
            for (int j = 0; j < sortedSpots.length - i - 1; j++) {
                // Get availability status
                boolean spot1Available = sortedSpots[j].canEnter();
                boolean spot2Available = sortedSpots[j+1].canEnter();
                
                // Calculate wait times
                int waitTime1 = spot1Available ? 0 : sortedSpots[j].getEstimatedWaitTime();
                int waitTime2 = spot2Available ? 0 : sortedSpots[j+1].getEstimatedWaitTime();
                
                // First sort by availability (available rooms first)
                if (spot1Available && !spot2Available) {
                    // Keep spot1 before spot2, do nothing
                    continue;
                } else if (!spot1Available && spot2Available) {
                    // Swap to put available spot first
                    RestrictedSpots temp = sortedSpots[j];
                    sortedSpots[j] = sortedSpots[j+1];
                    sortedSpots[j+1] = temp;
                } 
                // If both have same availability status, sort by wait time
                else if (waitTime1 > waitTime2) {
                    RestrictedSpots temp = sortedSpots[j];
                    sortedSpots[j] = sortedSpots[j+1];
                    sortedSpots[j+1] = temp;
                }
            }
        }
        
        // Display the sorted rooms
        System.out.println("Based on current occupancy, here's the recommended sequence to visit rooms:");
        System.out.println("\n" + ANSI_BLUE + "╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                     ROOM ENTRY SUGGESTION                     ║");
        System.out.println("╠═════╦═══════════════════════╦═══════════╦═════════════════════╣");
        System.out.println("║ Seq ║         Name          ║   Status  ║     Wait Time       ║");
        System.out.println("╠═════╬═══════════════════════╬═══════════╬═════════════════════╣" + ANSI_RESET);
        
        for (int i = 0; i < sortedSpots.length; i++) {
            RestrictedSpots spot = sortedSpots[i];
            String status;
            String waitTime;
            
            if (spot.canEnter()) {
                status = ANSI_GREEN + "AVAILABLE" + ANSI_RESET;
                waitTime = ANSI_GREEN + "No wait" + ANSI_RESET;
            } else {
                status = ANSI_RED + "FULL" + ANSI_RESET;
                // Use the same method as in displayAvailableSpots
                int waitTimeValue = spot.getEstimatedWaitTime();
                waitTime = ANSI_ORANGE + waitTimeValue + " minutes" + ANSI_RESET;
            }
            
            System.out.printf("   %d    %-21s   %-24s   %-19s\n", 
                            (i + 1), 
                            spot.spotName, 
                            status,
                            waitTime);
        }
        
        System.out.println(ANSI_BLUE + "╚═════╩═══════════════════════╩═══════════╩═════════════════════╝" + ANSI_RESET);
    }
}