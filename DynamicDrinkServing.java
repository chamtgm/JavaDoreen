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

    // Add new constants for arrow characters
    private static final String ARROW_UP = "↑";
    private static final String ARROW_DOWN = "↓";
    private static final String ARROW_LEFT = "←";
    private static final String ARROW_RIGHT = "→";
    private static final String ARROW_UP_RIGHT = "↗";
    private static final String ARROW_UP_LEFT = "↖";
    private static final String ARROW_DOWN_RIGHT = "↘";
    private static final String ARROW_DOWN_LEFT = "↙";

    public static void checkDynamicDistancing(Robot robot) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n" + ANSI_BLUE + "=== DYNAMIC DISTANCING CHECK ===" + ANSI_RESET);
        System.out.println("Please enter the distance (in meters) from people in four directions:");
        
        // Display visual robot position
        System.out.println("\n                 " + ANSI_YELLOW + "[Up]" + ANSI_RESET);
        System.out.println("\n");
        System.out.println(ANSI_YELLOW + "[Left]" + ANSI_RESET + "           " + ANSI_GREEN + "[R]" + ANSI_RESET + "           " + ANSI_YELLOW + "[Right]" + ANSI_RESET);
        System.out.println("\n");
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
        System.out.println("\n             " + formatDistance(front) + " m");
        System.out.println("\n");
        System.out.println(formatDistance(left) + " m        " + ANSI_GREEN + "[R]" + ANSI_RESET + "        " + formatDistance(right) + " m");
        System.out.println("\n");
        System.out.println("             " + formatDistance(back) + " m");

        if (left >= 1 && right >= 1 && front >= 1 && back >= 1) {
            System.out.println(ANSI_GREEN + "\nYou are safe in dynamic distancing!" + ANSI_RESET);
        } else {
            System.out.println(ANSI_RED + "\nAlert! Close contact detected. The robot must adjust its position." + ANSI_RESET);

            // Call suggestion method that returns the recommended movement direction
            String movementDirection = suggestMovement(left, right, front, back);
            
            // Show movement visualization
            displayMovementArrow(movementDirection);

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

    // Updated method that returns the movement direction
    private static String suggestMovement(double left, double right, double up, double down) {
        // First, check if all directions are safe (≥ 1m)
        if (left >= 1 && right >= 1 && up >= 1 && down >= 1) {
            System.out.println(ANSI_GREEN + "All directions are safe. No movement needed." + ANSI_RESET);
            return "NONE";
        }
        
        String recommendedDirection = "NONE";
        
        // Check individual directions and keep track of safe directions
        boolean leftSafe = left >= 1;
        boolean rightSafe = right >= 1;
        boolean upSafe = up >= 1;
        boolean downSafe = down >= 1;
        
        // Count safe directions
        int safeDirectionCount = (leftSafe ? 1 : 0) + (rightSafe ? 1 : 0) + 
                                 (upSafe ? 1 : 0) + (downSafe ? 1 : 0);
        
        System.out.println("Safe directions: " + safeDirectionCount + " out of 4");
        
        // Case: Three directions are safe, one is unsafe - move in the opposite direction of the unsafe one
        if (safeDirectionCount == 3) {
            if (!leftSafe) {
                printContactType("Left", left);
                double moveDistance = 1.0 - left;
                System.out.println("Move " + String.format("%.2f", moveDistance) + "m to the right.");
                recommendedDirection = "RIGHT";
            } else if (!rightSafe) {
                printContactType("Right", right);
                double moveDistance = 1.0 - right;
                System.out.println("Move " + String.format("%.2f", moveDistance) + "m to the left.");
                recommendedDirection = "LEFT";
            } else if (!upSafe) {
                printContactType("Up", up);
                double moveDistance = 1.0 - up;
                System.out.println("Move " + String.format("%.2f", moveDistance) + "m downward.");
                recommendedDirection = "DOWN";
            } else { // !downSafe
                printContactType("Down", down);
                double moveDistance = 1.0 - down;
                System.out.println("Move " + String.format("%.2f", moveDistance) + "m upward.");
                recommendedDirection = "UP";
            }
        }
        // Case: Two directions are safe - move diagonally along the safe directions
        else if (safeDirectionCount == 2) {
            // Horizontal and vertical directions both have one safe option
            if ((leftSafe || rightSafe) && (upSafe || downSafe)) {
                // Determine diagonal direction using safe directions
                if (leftSafe && upSafe) {
                    System.out.println("Safe directions are Left and Up - move diagonally toward them.");
                    recommendedDirection = "UP_LEFT";
                } else if (leftSafe && downSafe) {
                    System.out.println("Safe directions are Left and Down - move diagonally toward them.");
                    recommendedDirection = "DOWN_LEFT";
                } else if (rightSafe && upSafe) {
                    System.out.println("Safe directions are Right and Up - move diagonally toward them.");
                    recommendedDirection = "UP_RIGHT";
                } else { // rightSafe && downSafe
                    System.out.println("Safe directions are Right and Down - move diagonally toward them.");
                    recommendedDirection = "DOWN_RIGHT";
                }
                System.out.println(ANSI_GREEN + "Diagonal movement recommended using safe directions." + ANSI_RESET);
            }
            // Both safe directions are on same axis - move straight in the safer direction
            else if (leftSafe && rightSafe) {
                // Both horizontal directions are safe, vertical is unsafe
                // Choose the safest vertical direction to move in
                if (up >= down) {
                    System.out.println("Move upward where more space is available.");
                    recommendedDirection = "UP";
                } else {
                    System.out.println("Move downward where more space is available.");
                    recommendedDirection = "DOWN";
                }
            } else { // upSafe && downSafe
                // Both vertical directions are safe, horizontal is unsafe
                // Choose the safest horizontal direction
                if (left >= right) {
                    System.out.println("Move left where more space is available.");
                    recommendedDirection = "LEFT";
                } else {
                    System.out.println("Move right where more space is available.");
                    recommendedDirection = "RIGHT";
                }
            }
        }
        // Case: Only one direction is safe - move in that direction
        else if (safeDirectionCount == 1) {
            if (leftSafe) {
                System.out.println("Only left direction is safe. Move that way.");
                recommendedDirection = "LEFT";
            } else if (rightSafe) {
                System.out.println("Only right direction is safe. Move that way.");
                recommendedDirection = "RIGHT";
            } else if (upSafe) {
                System.out.println("Only up direction is safe. Move that way.");
                recommendedDirection = "UP";
            } else { // downSafe
                System.out.println("Only down direction is safe. Move that way.");
                recommendedDirection = "DOWN";
            }
        }
        // Case: No directions are safe - find best escape route based on the max distance
        else {
            System.out.println(ANSI_RED + "All directions are unsafe. Finding best escape route." + ANSI_RESET);
            
            // Find direction with maximum available distance
            double maxDistance = Math.max(Math.max(left, right), Math.max(up, down));
            
            if (maxDistance == left) {
                System.out.println("Left has the most space. Move that way.");
                recommendedDirection = "LEFT";
            } else if (maxDistance == right) {
                System.out.println("Right has the most space. Move that way.");
                recommendedDirection = "RIGHT";
            } else if (maxDistance == up) {
                System.out.println("Up has the most space. Move that way.");
                recommendedDirection = "UP";
            } else { // maxDistance == down
                System.out.println("Down has the most space. Move that way.");
                recommendedDirection = "DOWN";
            }
            
            // Calculate how much to move to achieve safety
            double moveDistance = 1.0 - maxDistance;
            System.out.println("Move at least " + String.format("%.2f", moveDistance) + 
                              "m to achieve minimum safe distance.");
        }
        
        return recommendedDirection;
    }

    // New method to display a visual arrow indicating movement direction
    private static void displayMovementArrow(String direction) {
        System.out.println("\n" + ANSI_BLUE + "=== RECOMMENDED MOVEMENT ===" + ANSI_RESET);
        
        // Initialize the visual grid with empty spaces
        String[][] grid = new String[7][7];
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                grid[i][j] = "   ";
            }
        }
        
        // Place robot at center
        grid[3][3] = " " + ANSI_GREEN + "R" + ANSI_RESET + " ";
        
        // Add arrow in the appropriate direction
        switch (direction) {
            case "UP":
                grid[1][3] = " " + ANSI_YELLOW + ARROW_UP + ANSI_RESET + " ";
                break;
            case "DOWN":
                grid[5][3] = " " + ANSI_YELLOW + ARROW_DOWN + ANSI_RESET + " ";
                break;
            case "LEFT":
                grid[3][1] = " " + ANSI_YELLOW + ARROW_LEFT + ANSI_RESET + " ";
                break;
            case "RIGHT":
                grid[3][5] = " " + ANSI_YELLOW + ARROW_RIGHT + ANSI_RESET + " ";
                break;
            case "UP_RIGHT":
                grid[1][5] = " " + ANSI_YELLOW + ARROW_UP_RIGHT + ANSI_RESET + " ";
                break;
            case "UP_LEFT":
                grid[1][1] = " " + ANSI_YELLOW + ARROW_UP_LEFT + ANSI_RESET + " ";
                break;
            case "DOWN_RIGHT":
                grid[5][5] = " " + ANSI_YELLOW + ARROW_DOWN_RIGHT + ANSI_RESET + " ";
                break;
            case "DOWN_LEFT":
                grid[5][1] = " " + ANSI_YELLOW + ARROW_DOWN_LEFT + ANSI_RESET + " ";
                break;
            case "NONE":
                // No arrow needed - safe position
                break;
        }
        
        // Display the grid
        System.out.println();
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                System.out.print(grid[i][j]);
            }
            System.out.println();
        }
        
        // Display movement instruction text
        if (!direction.equals("NONE")) {
            String directionText = direction.replace("_", " ");
            System.out.println("\n" + ANSI_GREEN + "Move in the " + directionText.toLowerCase() + 
                " direction as shown by the " + ANSI_YELLOW + "arrow" + ANSI_GREEN + "." + ANSI_RESET);
        } else {
            System.out.println("\n" + ANSI_GREEN + "No movement needed. Current position is safe." + ANSI_RESET);
        }
    }

    private static void calculateOptimalMovement(String direction, double constraint1, double constraint2) {
        // Calculate how far the robot needs to move to maintain a 1m safe distance using Pythagorean theorem
        // The constraint is the shorter distance (less than 1m)
        // We need to determine how far to move in the other direction to achieve a 1m hypotenuse
        
        // If constraint1 (the average of the unsafe directions) is very small, we need to move further
        double moveDistance;
        
        if (constraint1 <= 0.3) {
            // Very close - need to move more aggressively
            moveDistance = 0.95; // Almost 1m to get away quickly
        } else {
            // Calculate using pythagorean theorem: a² + b² = c² where c = 1 (our safe distance)
            // We know a = constraint1, and we need to find b
            // b = √(c² - a²) = √(1² - constraint1²)
            moveDistance = Math.sqrt(1 - constraint1 * constraint1);
        }
        
        // Round to 2 decimal places for readability
        moveDistance = Math.round(moveDistance * 100) / 100.0;
        
        System.out.println(ANSI_GREEN + "Recommended movement: " + ANSI_RESET + 
            "Move " + String.format("%.2f", moveDistance) + "m " + direction.toLowerCase() + 
            " to maintain safe distance based on constraints.");
        
        System.out.println("This will create a " + String.format("%.2f", 
            Math.sqrt(constraint1*constraint1 + moveDistance*moveDistance)) + 
            "m diagonal distance (target: 1.00m)");
    }

    private static void printContactType(String direction, double distance) {
        String contactType;
        if (distance <= 0.30) {
            contactType = ANSI_RED + "CLOSE CONTACT" + ANSI_RESET;
        } else if (distance <= 0.60) {
            contactType = ANSI_ORANGE + "CASUAL CONTACT" + ANSI_RESET;
        } else {
            contactType = ANSI_YELLOW + "DISTANCE CONTACT" + ANSI_RESET;
        }
        
        System.out.println("Direction: " + direction + ", Distance: " + 
            String.format("%.2f", distance) + "m, Contact Type: " + contactType);
    }
}
