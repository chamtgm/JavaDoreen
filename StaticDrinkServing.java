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
                // Simple wait time calculation
                int simpleWaitTime = selectedSpot.currentOccupancy - selectedSpot.maxCapacity;
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
                // Room is full - calculate simple waiting time as difference between current and max
                int simpleWaitTime = spot.currentOccupancy - spot.maxCapacity;
                
                // For display purposes, show equal values for current and max occupancy
                System.out.printf("%d. %s - %s %s\n", 
                    (i + 1), 
                    spot.spotName, 
                    ANSI_RED + "FULL (" + spot.maxCapacity + "/" + spot.maxCapacity + ")" + ANSI_RESET,
                    ANSI_ORANGE + "Est. Wait: " + simpleWaitTime + " min" + ANSI_RESET);
            }
        }
    }
}
