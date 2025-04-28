import java.io.FileWriter;
import java.io.IOException;

public class CSVWriter {

    private static final String FILE_PATH = "room_entry_records.csv";

    /**
     * Writes a record to the CSV file.
     * @param record The record to write, as a comma-separated string.
     */
    public static void writeRecord(String record) {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
            writer.append(record).append("\n");
        } catch (IOException e) {
            System.out.println("Error writing to CSV file: " + e.getMessage());
        }
    }

    /**
     * Initializes the CSV file with headers if it doesn't already exist.
     */
    public static void initializeCSV() {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
            // Check if the file is empty and write headers
            if (new java.io.File(FILE_PATH).length() == 0) {
                writer.append("Room Name,Room Area,Max Capacity,Current Occupancy,Input Up,Input Left,Input Right,Input Down,Output Up,Output Left,Output Right,Output Down,Time Entered,Wait Time\n");
            }
        } catch (IOException e) {
            System.out.println("Error initializing CSV file: " + e.getMessage());
        }
    }
}