import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

    /**
     * Loads tasks from the data file.
     *
     * @return the tasks stored in the data file
     */
    public static ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();

        if (!Files.exists(FILE_PATH)) {
            return tasks;
        }

        try (Scanner scanner = new Scanner(FILE_PATH)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(" \\| ");

                String type = parts[0];
                boolean isDone = parts[1].equals("1");
                String description = parts[2];

                Task task;

                if (type.equals("T")) {
                    task = new Todo(description);
                } else if (type.equals("D")) {
                    task = new Deadline(description, parts[3]);
                } else {
                    task = new Event(description, parts[3], parts[4]);
                }

                if (isDone) {
                    task.markAsDone();
                }

                tasks.add(task);
            }
        } catch (IOException exception) {
            throw new RuntimeException("Could not load tasks.", exception);
        }

        return tasks;
    }
}