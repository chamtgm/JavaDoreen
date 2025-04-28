import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

public class CSVWriter {

    private static final String FILE_PATH = "room_entry_records.csv";

    /**
     * Writes a record to the CSV file.
     * @param record The record to write, as a comma-separated string.
     */
    public static void writeRecord(String record) {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
            writer.append(record).append("\n");
            writer.flush(); // good practice to flush manually for critical writes
        } catch (IOException e) {
            System.out.println("Error writing to CSV file: " + e.getMessage());
        }
    }

    /**
     * Initializes the CSV file with headers if it doesn't already exist or is empty.
     */
    public static void initializeCSV() {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            try (FileWriter writer = new FileWriter(file, true)) {
                writer.append("Room Name,Room Area,Max Capacity,Current Occupancy,Input Up,Input Left,Input Right,Input Down,Output Up,Output Left,Output Right,Output Down,Time Entered,Wait Time\n");
                writer.flush();
                System.out.println("CSV file initialized with headers.");
            } catch (IOException e) {
                System.out.println("Error initializing CSV file: " + e.getMessage());
            }
        }
    }
}
