import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class DynamicDrinkServing {

    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_ORANGE = "\u001B[38;5;208m";
    private static final String ANSI_GREEN ="\u001B[0;32m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    public static void checkDynamicDistancing(Robot robot) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n" + ANSI_BLUE + "=== DYNAMIC DISTANCING CHECK ===" + ANSI_RESET);
        System.out.println("Please enter the distance (in meters) from people in four directions:");
        
        // Display visual robot position
        System.out.println("\n                 " + ANSI_YELLOW + "[Up]" + ANSI_RESET);
        System.out.println("\n\n\n");
        System.out.println(ANSI_YELLOW + "[Left]" + ANSI_RESET + "           " + ANSI_GREEN + "[R]" + ANSI_RESET + "           " + ANSI_YELLOW + "[Right]" + ANSI_RESET);
        System.out.println("\n\n\n");
        System.out.println("                " + ANSI_YELLOW + "[Down]" + ANSI_RESET);
        
        // Get front/up distance
        System.out.print("\nUp distance: ");
        double front = scanner.nextDouble();
        
        // Get left distance
        System.out.print("Left distance: ");
        double left = scanner.nextDouble();
        
        // Get right distance
        System.out.print("Right distance: ");
        double right = scanner.nextDouble();
        
        // Get back/down distance
        System.out.print("Down distance: ");
        double back = scanner.nextDouble();

        if (left < 0 || right < 0 || front < 0 || back < 0) {
            System.out.println(ANSI_RED + "Error: Distances cannot be negative." + ANSI_RESET);
            return;
        }

        // Display the distances in the visual format
        System.out.println("\nDistances entered:");
        System.out.println("                " + formatDistance(front) + " m");
        System.out.println(formatDistance(left) + " m        " + ANSI_GREEN + "[R]" + ANSI_RESET + "        " + formatDistance(right) + " m");
        System.out.println("                " + formatDistance(back) + " m");

        if (left >= 1 && right >= 1 && front >= 1 && back >= 1) {
            System.out.println(ANSI_GREEN + "\nYou are safe in dynamic distancing!" + ANSI_RESET);
        } else {
            System.out.println(ANSI_RED + "\nAlert! Close contact detected. The robot must adjust its position." + ANSI_RESET);

            // Call suggestion method for each direction if needed
            suggestMovement(left, "Left");
            suggestMovement(right, "Right");
            suggestMovement(front, "Up");
            suggestMovement(back, "Down");

            // Print robot details ONLY ONCE after processing all distances
            System.out.println("\nRobot Contact Details");
            System.out.println("Robot ID: " + robot.getId() + ", Robot Name: " + robot.getName() + 
                              ", Priority Level: " + robot.getPriorityLevel());

            // Format the date and time
            String formattedDate = LocalDate.now().toString();
            String formattedTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            
            System.out.println("Date: " + formattedDate + ", Time: " + formattedTime);
        }
    }
    
    // Overload for backward compatibility
    public static void checkDynamicDistancing() {
        checkDynamicDistancing(new Robot("20614522", "Ivan Char Cheng Jun", 3, false));
    }
    
    // Helper method to format and color the distance based on safety
    private static String formatDistance(double distance) {
        String formattedDistance = String.format("%.1f", distance);
        
        if (distance >= 1.0) {
            return ANSI_GREEN + formattedDistance + ANSI_RESET;
        } else if (distance >= 0.6) {
            return ANSI_YELLOW + formattedDistance + ANSI_RESET;
        } else if (distance >= 0.3) {
            return ANSI_ORANGE + formattedDistance + ANSI_RESET;
        } else {
            return ANSI_RED + formattedDistance + ANSI_RESET;
        }
    }

    private static void suggestMovement(double distance, String direction) {
        if (distance >= 1) 
            return;

        String contactType;
        if (distance <= 0.30) {
            contactType = ANSI_RED + "CLOSE CONTACT" + ANSI_RESET;
        } else if (distance <= 0.60) {
            contactType = ANSI_ORANGE + "CASUAL CONTACT" + ANSI_RESET;
        } else {
            contactType = ANSI_GREEN + "DISTANCE CONTACT" + ANSI_RESET;
        }

        System.out.println("Direction: " + direction + ", Distance: " + distance + "m, Contact Type: " + contactType);

        // Suggest movement to maintain distancing
        double moveAway = 1.0 - distance;
        System.out.println("Move " + String.format("%.1f", moveAway) + "m away from " + direction + " to maintain distancing.");
    }
}
