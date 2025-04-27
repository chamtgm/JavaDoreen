public class Robot {
    private String id;
    private String name;
    private int priorityLevel; // 1-5, with 5 being highest priority
    private boolean hasEmergencyOverride;
    
    public Robot(String id, String name, int priorityLevel, boolean hasEmergencyOverride) {
        this.id = id;
        this.name = name;
        this.priorityLevel = priorityLevel;
        this.hasEmergencyOverride = hasEmergencyOverride;
    }
    
    public boolean canBypassQueue() {
        return priorityLevel >= 4 || hasEmergencyOverride;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public String getName() { return name; }
    public int getPriorityLevel() { return priorityLevel; }
    public boolean hasEmergencyOverride() { return hasEmergencyOverride; }
}