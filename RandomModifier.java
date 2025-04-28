import java.util.Random;

public class RandomModifier {
    private static final Random random = new Random();

    /**
     * Generates a random sign (+1 or -1).
     * @return +1 or -1.
     */
    public static int getRandomSign() {
        return random.nextBoolean() ? 1 : -1; // Randomly return +1 or -1
    }

    /**
     * Generates a random number between 1 and 10.
     * @return A random integer between 1 and 10.
     */
    public static int getRandomValue() {
        return random.nextInt(10) + 1; // Random number between 1 and 10
    }
}