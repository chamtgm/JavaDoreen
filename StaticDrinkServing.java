import java.util.Scanner;

public class StaticDrinkServing {

    //ANSI color codes

    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_ORANGE = "\u001B[38;5;208m";
    private static final String ANSI_GREEN ="\u001B[0;32m";

    private static Scanner scanner = new Scanner(System.in);
    public static RestrictedSpots[] spots = {
        new RestrictedSpots("S1", "Dining Foyer", 50, 30),
        new RestrictedSpots("S2", "Main Dining Hall", 20, 45),
        new RestrictedSpots("S3", "Dining Room One", 35, 25),
        new RestrictedSpots("S4", "Dining Room Two", 40, 20),
        new RestrictedSpots("S5", "Family Dining Room", 15, 60)
    };

    public static void main(String[] args) {
        System.out.println("Welcome to the Drink Serving Robotic System!");
        while (true) {
            System.out.println("\nAvailable Spots:");
            for (int i = 0; i < spots.length; i++) {
                System.out.println((i + 1) + ". " + spots[i].spotName);
            }
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

            String customizeChoice;
            while (true) {
                System.out.print("\nDo you want to customize the room size? (yes/no): ");
                customizeChoice = scanner.next().toLowerCase();

                if (customizeChoice.equals("yes") || customizeChoice.equals("no")){
                    break;
                } else{
                    System.out.println(ANSI_RED + "Invalid input! Only \"yes\" or \"no\" is allowed." + ANSI_RESET);
                }
            }

            if (customizeChoice.equals("yes")){
                System.out.print("Enter the new room size in square meters: ");
                if (scanner.hasNextDouble()){
                    double newSize = scanner.nextDouble();
                    if (newSize > 0){
                        selectedSpot.updateSpotSize(newSize);
                        System.out.println(ANSI_GREEN + "Room size updated successfully!" + ANSI_RESET);
                        selectedSpot.displayInfo(); //Update the new room size
                    }else{
                        System.out.println(ANSI_RED + "Room size invalid, please try again." + ANSI_RESET);
                    }
                } else{
                    System.out.println(ANSI_RED + "Invalid input. Room size not updated." + ANSI_RESET);
                    scanner.next(); // Clear invalid input
                }
            }

            if (selectedSpot.canEnter()) {
                System.out.println(ANSI_GREEN + "Entrance permitted. Proceeding to dynamic distancing check..." + ANSI_RESET);
                DynamicDrinkServing.checkDynamicDistancing();
            } else {
                System.out.println(ANSI_ORANGE + "Spot is full! Expected wait time: " + selectedSpot.avgTime + " minutes." + ANSI_RESET);
                
                String waitChoice;
                while (true) {
                    System.out.print("Do you want to wait? (yes/no): ");
                    waitChoice = scanner.next().toLowerCase(); // Convert input to lowercase

                    if (waitChoice.equals("yes")) {
                        System.out.println(ANSI_ORANGE + "Robot is now allowed to enter after waiting." + ANSI_RESET);
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
}
