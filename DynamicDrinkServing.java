import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class DynamicDrinkServing {

    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_ORANGE = "\u001B[38;5;208m";
    private static final String ANSI_GREEN ="\u001B[0;32m";

    public static void checkDynamicDistancing() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEnter distance (in meters) from people in four directions:");

        System.out.print("Left: ");
        double left = scanner.nextDouble();
        System.out.print("Right: ");
        double right = scanner.nextDouble();
        System.out.print("Front: ");
        double front = scanner.nextDouble();
        System.out.print("Back: ");
        double back = scanner.nextDouble();

        if (left < 0 || right < 0 || front < 0 || back < 0) {
            System.out.println(ANSI_RED + "Error: Distances cannot be negative." + ANSI_RESET);
            return;
        }

        if (left >= 1 && right >= 1 && front >= 1 && back >= 1) {
            System.out.println(ANSI_GREEN + "You are safe in dynamic distancing!" + ANSI_RESET);
        } else {
            System.out.println(ANSI_RED + "\nAlert! Close contact detected. The robot must adjust its position." + ANSI_RESET);

            // Call suggestion method for each direction if needed
            suggestMovement(left, "Left");
            suggestMovement(right, "Right");
            suggestMovement(front, "Front");
            suggestMovement(back, "Back");

            // Print robot details ONLY ONCE after processing all distances
            System.out.println("\nRobot Contact Details");
            System.out.println("Robot ID: 20614522, Robot Name: Ivan Char Cheng Jun");

            // Format the date and time
            String formattedDate = LocalDate.now().toString();
            String formattedTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            System.out.println("Date: " + formattedDate + ", Time: " + formattedTime);
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
