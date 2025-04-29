// Ng Yoong Shen 20609660
// Ivan Char Cheng Jun 20614522
// Cham Jin Jie 20611325

import java.util.Scanner;
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
        new RestrictedSpots("S1", "Dining Foyer", 120, 1),
        new RestrictedSpots("S2", "Main Dining Hall", 300, 1),
        new RestrictedSpots("S3", "Dining Room One", 215, 1),
        new RestrictedSpots("S4", "Dining Room Two", 150, 1),
        new RestrictedSpots("S5", "Family Dining Room", 250, 1)
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
        
        // Initialize the CSV file
        CSVWriter.initializeCSV();
        
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

            if (selectedSpot.currentOccupancy < selectedSpot.maxCapacity) {
                System.out.println(ANSI_GREEN + "\nEntrance permitted for " + selectedSpot.spotName + ". Proceeding to dynamic distancing check..." + ANSI_RESET);
                // Assuming Robot class exists and you have an instance
                Robot defaultRobot = new Robot("20614522", "Ivan Char Cheng Jun"); 
                DynamicDrinkServing.checkDynamicDistancing(defaultRobot, selectedSpot); // Pass robot instance
            } 
            else {
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
                        Robot defaultRobot = new Robot("20614522", "Ivan Char Cheng Jun");
                        DynamicDrinkServing.checkDynamicDistancing(defaultRobot, selectedSpot); // Pass robot instance
                        break;
                    } 
                    else if (waitChoice.equals("no")) {
                        System.out.println("Select another spot.");
                        break;
                    } 
                    else {
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
                else if (confirm.equals("no")) {
                    System.out.println(ANSI_RED + "Configuration not completed. Please finish before proceeding." + ANSI_RESET);
                }
                else{
                    System.out.println(ANSI_RED + "Invalid input! Please enter 'yes' or 'no'." + ANSI_RESET);
                }
            } 
            else if (choice >= 1 && choice <= 5) {
                RestrictedSpots selectedSpot = spots[choice - 1];
                System.out.println("\nSelected: " + selectedSpot.spotName + " (Current size: " + selectedSpot.spotArea + " m²)");
                
                System.out.print("Enter new size in square meters: ");
                if (scanner.hasNextDouble()) {
                    double newSize = scanner.nextDouble();
                    if (newSize > 0 && newSize <= 1000) {
                        selectedSpot.updateSpotSize(newSize);
                        System.out.println(ANSI_GREEN + "Room size updated successfully!" + ANSI_RESET);
                    } 
                    else if (newSize > 1000) {
                        System.out.println(ANSI_RED + "Room size must be within 1m² to 1000m²." + ANSI_RESET);
                    }
                    else {
                        System.out.println(ANSI_RED + "Room size must be positive. No changes made." + ANSI_RESET);
                    }
                } else {
                    System.out.println(ANSI_RED + "Invalid input. Room size not updated." + ANSI_RESET);
                    scanner.next(); // Clear invalid input
                }
                
                // Display updated table
                displayRoomSizesTable();
            } 
            else {
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
    
    public static void displayAvailableSpots() {
        // Define column widths (visible characters)
        int numWidth = 3;
        int nameWidth = 21;
        int occupancyWidth = 14;
        int waitTimeWidth = 19;

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
            
            if (spot.currentOccupancy < spot.maxCapacity) {
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
    
    public static void suggestEntrySequence() {
        System.out.println("\n" + ANSI_BLUE + "--- ADVANCED AI SUGGESTED ENTRY SEQUENCE ---" + ANSI_RESET);
    
        // Create a copy of spots array to sort without modifying the original
        RestrictedSpots[] sortedSpots = spots.clone();
    
        // Sort spots by room score (highest score first)
        for (int i = 0; i < sortedSpots.length - 1; i++) {
            for (int j = 0; j < sortedSpots.length - i - 1; j++) {
                double score1 = RoomScorer.calculateRoomScore(sortedSpots[j]);
                double score2 = RoomScorer.calculateRoomScore(sortedSpots[j + 1]);
    
                if (score1 < score2) {
                    // Swap spots
                    RestrictedSpots temp = sortedSpots[j];
                    sortedSpots[j] = sortedSpots[j + 1];
                    sortedSpots[j + 1] = temp;
                }
            }
        }
    
        // Display the sorted rooms with their scores
        System.out.println("Recommended order to enter based on multiple factors:");
        for (int i = 0; i < sortedSpots.length; i++) {
            RestrictedSpots spot = sortedSpots[i];
            double score = RoomScorer.calculateRoomScore(spot);
            System.out.printf("%d. %-21s - Score: %.1f\n", (i + 1), spot.spotName, score);
        }
        System.out.println(ANSI_BLUE + "----------------------------------" + ANSI_RESET);
    }
}