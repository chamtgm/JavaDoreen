public class RoomScorer {

    /**
     * Calculates the score for a given room based on availability, wait time, and area.
     * @param spot The room to calculate the score for.
     * @return The calculated score.
     */
    public static double calculateRoomScore(RestrictedSpots spot) {
        double score = 0;

        // Add bonus if the room is available
        if (spot.currentOccupancy < spot.maxCapacity) {
            score += 50; // Big bonus for availability
        }

        // Subtract penalty based on wait time
        int waitTime = spot.getEstimatedWaitTime();
        score -= 1.0 * waitTime; // 1 point penalty per minute of wait

        // Add bonus based on room area
        score += 0.1 * spot.spotArea; // 0.1 point per square meter

        return score;
    }
}