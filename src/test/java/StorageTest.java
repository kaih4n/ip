import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests saving tasks to the hard disk.
 */
public class StorageTest {
    private static final Path FILE_PATH = Path.of("data", "sioet.txt");

    /**
     * Removes the saved file after each test.
     */
    @AfterEach
    public void cleanUp() throws IOException {
        Files.deleteIfExists(FILE_PATH);
    }

    /**
     * Tests that a todo task is saved correctly.
     */
    @Test
    public void saveTodo() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));

        Storage.save(tasks);

        assertTrue(Files.exists(FILE_PATH));
        assertEquals(
                "T | 0 | read book",
                Files.readString(FILE_PATH).trim()
        );
    }

    /**
     * Tests that a completed todo task is saved correctly.
     */
    @Test
    public void saveCompletedTodo() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();

        Todo todo = new Todo("read book");
        todo.markAsDone();
        tasks.add(todo);

        Storage.save(tasks);

        assertEquals(
                "T | 1 | read book",
                Files.readString(FILE_PATH).trim()
        );
    }

    /**
     * Tests that a deadline task is saved correctly.
     */
    @Test
    public void saveDeadline() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Deadline("return book", "June 6th"));

        Storage.save(tasks);

        assertEquals(
                "D | 0 | return book | June 6th",
                Files.readString(FILE_PATH).trim()
        );
    }

    /**
     * Tests that an event task is saved correctly.
     */
    @Test
    public void saveEvent() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Event("project meeting", "Aug 6th 2pm", "Aug 6th 4pm"));

        Storage.save(tasks);

        assertEquals(
                "E | 0 | project meeting | Aug 6th 2pm | Aug 6th 4pm",
                Files.readString(FILE_PATH).trim()
        );
    }

    /**
     * Tests that multiple tasks are saved in the correct order.
     */
    @Test
    public void saveMultipleTasks() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();

        Todo todo = new Todo("read book");
        todo.markAsDone();

        tasks.add(todo);
        tasks.add(new Deadline("return book", "June 6th"));
        tasks.add(new Event("project meeting", "Aug 6th 2pm", "Aug 6th 4pm"));

        Storage.save(tasks);

        assertEquals(
                "T | 1 | read book\n"
                        + "D | 0 | return book | June 6th\n"
                        + "E | 0 | project meeting | Aug 6th 2pm | Aug 6th 4pm",
                Files.readString(FILE_PATH).trim()
        );
    }
}