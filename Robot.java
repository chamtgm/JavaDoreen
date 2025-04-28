public class Robot {
    private String id;
    private String name;
    private int priorityLevel;
    private boolean isActive;

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE = "\u001B[34m";

    // Constructor
    public Robot(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // Method to display robot details
    public void displayDetails() {
        System.out.println("\n" + ANSI_BLUE + "=== ROBOT DETAILS ===" + ANSI_RESET);
        System.out.println("Robot ID: " + id);
        System.out.println("Robot Name: " + name);
    }
}