import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles saving tasks to the hard disk.
 */
public class Storage {
    private static final Path FILE_PATH = Path.of("data", "sioet.txt");

    /**
     * Saves all tasks to the data file.
     *
     * @param tasks the tasks to save
     */
    public static void save(ArrayList<Task> tasks) {
        try {
            Files.createDirectories(FILE_PATH.getParent());

            List<String> lines = new ArrayList<>();

            for (Task task : tasks) {
                if (task instanceof Todo) {
                    lines.add("T | " + (task.isDone() ? "1" : "0")
                            + " | " + task.getDescription());
                } else if (task instanceof Deadline deadline) {
                    lines.add("D | " + (task.isDone() ? "1" : "0")
                            + " | " + task.getDescription()
                            + " | " + deadline.getBy());
                } else if (task instanceof Event event) {
                    lines.add("E | " + (task.isDone() ? "1" : "0")
                            + " | " + task.getDescription()
                            + " | " + event.getFrom()
                            + " | " + event.getTo());
                }
            }

            Files.write(FILE_PATH, lines);
        } catch (IOException exception) {
            throw new RuntimeException("Could not save tasks.", exception);
        }
    }
}