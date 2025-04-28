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
    public static final String ARROW_UP = "^";      // ↑
    public static final String ARROW_DOWN = "v";    // ↓
    public static final String ARROW_LEFT = "<-";    // ←
    public static final String ARROW_RIGHT = "->";   // →
    
    public static final String ARROW_UP_RIGHT = "/^";   // ↗
    public static final String ARROW_UP_LEFT = "^\\";    // ↖
    public static final String ARROW_DOWN_RIGHT = "\\v"; // ↘
    public static final String ARROW_DOWN_LEFT = "v/";  // ↙

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
    
        // Get distances
        System.out.print("\nUp distance: ");
        double front = scanner.nextDouble();
    
        System.out.print("Left distance: ");
        double left = scanner.nextDouble();
    
        System.out.print("Right distance: ");
        double right = scanner.nextDouble();
    
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
            System.out.println(ANSI_RED + "\nAlert! Contact detected. The robot must adjust its position." + ANSI_RESET);
    
            // Call suggestion method that returns the recommended movement direction
            String movementDirection = suggestMovement(left, right, front, back);
    
            // Show movement visualization
            displayMovementArrow(movementDirection);
    
            // Display robot details using the Robot class
            robot.displayDetails();
    
            // Format the date and time
            String formattedDate = LocalDate.now().toString();
            String formattedTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    
            System.out.println("Date: " + formattedDate + ", Time: " + formattedTime);
        }
    
        // Regenerate random occupancy and recalculate wait times
        System.out.println("\n" + ANSI_BLUE + "=== RELOADING SPOTS ===" + ANSI_RESET);
        for (RestrictedSpots spot : StaticDrinkServing.spots) {
            spot.regenerateOccupancy(); // Regenerate random occupancy
        }
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

        // First, check if all directions are safe (≥ 1m)
        if (safeDirectionCount == 4) {
            System.out.println(ANSI_GREEN + "All directions are safe. No movement needed." + ANSI_RESET);
            recommendedDirection = "NONE";
        }
        
        // Case: Three directions are safe, one is unsafe - move to balance rather than reach safety threshold
        else if (safeDirectionCount == 3) {
            // Check which direction is unsafe
            if (!leftSafe) {
                printContactType("Left", left);
                // Check if the unsafe direction is already close to its maximum (<= 1m)
                if (right <= 1) {
                    System.out.println("Right is already at its maximum possible distance. Moving would reduce overall safety.");
                    recommendedDirection = "NONE";
                } 
                else {
                    // Calculate move distance to achieve better balance
                    double moveDistance = right - 1;
                    System.out.println("Move maximum " + String.format("%.2f", moveDistance) + 
                                      "m to the right to maintain a safe distance on right and maximum the distance of the obstacle from left.");
                    recommendedDirection = "RIGHT";
                    }
            }
            else if (!rightSafe) {
                printContactType("Right", right);
                // Check if the unsafe direction is already close to its maximum (<= 1m)
                if (left <= 1) {
                    System.out.println("Left is already at its maximum possible distance. Moving would reduce overall safety.");
                    recommendedDirection = "NONE";
                } 
                else {
                        // Calculate move distance to achieve better balance
                        double moveDistance = left - 1;
                        System.out.println("Move maximum " + String.format("%.2f", moveDistance) + 
                                          "m to the left to maintain a safe distance on left and maximum the distance of the obstacle from right.");
                        recommendedDirection = "LEFT";
                    }
            }
            else if (!upSafe) {
                printContactType("Up", up);
                // Check if the unsafe direction is already close to its maximum (<= 1m)
                if (down <= 1) {
                    System.out.println("Backward is already at its maximum possible distance. Moving would reduce overall safety.");
                    recommendedDirection = "NONE";
                }
                else {
                        // Calculate move distance to achieve better balance
                        double moveDistance = down - 1;
                        System.out.println("Move maximum " + String.format("%.2f", moveDistance) + 
                                          "m to the back to maintain a safe distance on backward and maximum the distance of the obstacle from front.");
                        recommendedDirection = "DOWN";
                    }
            }
            else { // !downSafe
                printContactType("Down", down);
                // Check if the unsafe direction is already close to its maximum (<= 1m)
                if (up <= 1) {
                    System.out.println("Frontward is already at its maximum possible distance. Moving would reduce overall safety.");
                    recommendedDirection = "NONE";
                }
                else {
                        // Calculate move distance to achieve better balance
                        double moveDistance = up - 1;
                        System.out.println("Move maximum " + String.format("%.2f", moveDistance) + 
                                          "m to the front to maintain a safe distance on frontward and maximum the distance of the obstacle from backward.");
                        recommendedDirection = "UP";
                }
            }
        }
        
        // Case: Two directions are safe - move diagonally along the safe directions
        else if (safeDirectionCount == 2) {
            // Determine diagonal direction using safe directions
            if (leftSafe && upSafe) {
                if (left == 1 || up == 1) {
                    System.out.println("Current position has reached maximum possible distance. Moving would reduce overall safety.");
                    recommendedDirection = "NONE";
                }
                else {
                    double leftMovement = left - 1;
                    double upMovement = up - 1;
                    System.out.println(ANSI_YELLOW + "Safe directions are Leftward and Upward - move diagonally toward them." + ANSI_RESET);
                    System.out.println("Move minimum " + String.format("%.2f", leftMovement) + 
                                      "m leftward and " + String.format("%.2f", upMovement) + "m upward to balance between rightward ("  + String.format("%.2f", right) + 
                                      "m) and downward (" + String.format("%.2f", down) + "m) obstacles.");
                                      recommendedDirection = "UP_LEFT";
                }
            }
            else if (leftSafe && downSafe) {
                if (left == 1 || down == 1) {
                    System.out.println("Current position has reached maximum possible distance. Moving would reduce overall safety.");
                    recommendedDirection = "NONE";
                }
                else {
                    double leftMovement = left - 1;
                    double downMovement = down - 1;
                    System.out.println(ANSI_YELLOW + "Safe directions are Leftward and Backward - move diagonally toward them." + ANSI_RESET);
                    System.out.println("Move minimum " + String.format("%.2f", leftMovement) + 
                                      "m leftward and " + String.format("%.2f", downMovement) + "m frontward to balance between rightward ("  + String.format("%.2f", right) + 
                                      "m) and upward (" + String.format("%.2f", down) + "m) obstacles.");
                                      recommendedDirection = "DOWN_LEFT";
                }
            } 
            else if (rightSafe && upSafe) {
                if (right == 1 || up == 1) {
                    System.out.println("Current position has reached maximum possible distance. Moving would reduce overall safety.");
                    recommendedDirection = "NONE";
                }
                else {
                    double rightMovement = right - 1;
                    double upMovement = up - 1;
                    System.out.println(ANSI_YELLOW + "Safe directions are rightward and frontward - move diagonally toward them." + ANSI_RESET);
                    System.out.println("Move minimum " + String.format("%.2f", rightMovement) + 
                                      "m rightward and " + String.format("%.2f", upMovement) + "m frontward to balance between leftward ("  + String.format("%.2f", right) + 
                                      "m) and backward (" + String.format("%.2f", down) + "m) obstacles.");
                                      recommendedDirection = "UP_RIGHT";
                }
            } 
            else if (rightSafe && downSafe) { // rightSafe && downSafe
                if (right == 1 || down == 1) {
                    System.out.println("Current position has reached maximum possible distance. Moving would reduce overall safety.");
                    recommendedDirection = "NONE";
                }
                else {
                    double rightMovement = right - 1;
                    double downMovement = down - 1;
                    System.out.println(ANSI_YELLOW + "Safe directions are rightward and backward - move diagonally toward them." + ANSI_RESET);
                    System.out.println("Move minimum " + String.format("%.2f", rightMovement) + 
                                      "m rightward and " + String.format("%.2f", downMovement) + "m backward to balance between leftward ("  + String.format("%.2f", right) + 
                                      "m) and frontward (" + String.format("%.2f", down) + "m) obstacles.");
                                      recommendedDirection = "DOWN_RIGHT";
                }
            }
            else if (upSafe && downSafe) {
                if (left == right) {
                    System.out.println("Current position has reached maximum possible distance. Moving would reduce overall safety.");
                    recommendedDirection = "NONE";
                }
                else if (left > right) {
                    double leftMovement = (left - right) / 2;
                    System.out.println("Move maximum " + String.format("%.2f", leftMovement) + 
                    "m to the left to maintain a balance distance from left to right.");
                    recommendedDirection = "LEFT";
                }
                else {
                    double rightMovement = (right - left) / 2;
                    System.out.println("Move maximum " + String.format("%.2f", rightMovement) + 
                    "m to the right to maintain a balance distance from left to right.");
                    recommendedDirection = "RIGHT";
                }
            }
            else if (leftSafe && rightSafe) {
                if (up == down) {
                    System.out.println("Current position has reached maximum possible distance. Moving would reduce overall safety.");
                    recommendedDirection = "NONE";
                }
                else if (up > down) {
                    double upMovement = (up - down) / 2;
                    System.out.println("Move maximum " + String.format("%.2f", upMovement) + 
                    "m to the front to maintain a balance distance from front to back.");
                    recommendedDirection = "UP";
                }
                else {
                    double downMovement = (down - up) / 2;
                    System.out.println("Move maximum " + String.format("%.2f", downMovement) + 
                    "m to the back to maintain a balance distance from front to back.");
                    recommendedDirection = "DOWN";
                }
            }
            else {
                System.out.println(ANSI_RED + "Error: No safe directions available. Unable to suggest movement." + ANSI_RESET);
            }
        }

        // Case: Only one direction is safe - move in that direction
        else if (safeDirectionCount == 1) {
            if (leftSafe) {
                double leftMovement = left - 1;
                System.out.println("Move maximum " + String.format("%.2f", leftMovement) + 
                "m to the left to maximise the safe distance from all directions.");
                recommendedDirection = "LEFT";
            } 
            else if (rightSafe) {
                double rightMovement = right - 1;
                System.out.println("Move maximum " + String.format("%.2f", rightMovement) + 
                "m to the right to maximise the safe distance from all directions.");
                recommendedDirection = "RIGHT";
            } 
            else if (upSafe) {
                double upMovement = up - 1;
                System.out.println("Move maximum " + String.format("%.2f", upMovement) + 
                "m to the front to maximise the safe distance from all directions.");
                recommendedDirection = "UP";
            } 
            else { // downSafe
                double downMovement = down - 1;
                System.out.println("Move maximum " + String.format("%.2f", downMovement) + 
                "m to the back to maximise the safe distance from all directions.");
                recommendedDirection = "DOWN";
            }
        }
        // Case: No directions are safe - find best escape route based on the max distance
        else {
            System.out.println(ANSI_RED + "All directions are unsafe, current position has reached maximum possible distance, moving would reduce overall safety." + ANSI_RESET);
            recommendedDirection = "NONE";
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
            System.out.println("\n" + ANSI_GREEN + "No movement needed. Each directions had maximise the safe distance!" + ANSI_RESET);
        }
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
