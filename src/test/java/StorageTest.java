import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests saving and loading tasks to the hard disk.
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

    /**
     * Tests that a todo task is loaded correctly.
     */
    @Test
    public void loadTodo() throws IOException {
        Files.createDirectories(FILE_PATH.getParent());
        Files.writeString(FILE_PATH, "T | 0 | read book");

        ArrayList<Task> tasks = Storage.load();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    /**
     * Tests that a completed todo task is loaded with the correct status.
     */
    @Test
    public void loadCompletedTodo() throws IOException {
        Files.createDirectories(FILE_PATH.getParent());
        Files.writeString(FILE_PATH, "T | 1 | read book");

        ArrayList<Task> tasks = Storage.load();

        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0).isDone());
    }

    /**
     * Tests that a deadline task is loaded correctly.
     */
    @Test
    public void loadDeadline() throws IOException {
        Files.createDirectories(FILE_PATH.getParent());
        Files.writeString(
                FILE_PATH,
                "D | 0 | return book | June 6th"
        );

        ArrayList<Task> tasks = Storage.load();

        assertEquals(1, tasks.size());
        assertEquals(
                "[D][ ] return book (by: June 6th)",
                tasks.get(0).toString()
        );
    }

    /**
     * Tests that an event task is loaded correctly.
     */
    @Test
    public void loadEvent() throws IOException {
        Files.createDirectories(FILE_PATH.getParent());
        Files.writeString(
                FILE_PATH,
                "E | 0 | project meeting | Aug 6th 2pm | Aug 6th 4pm"
        );

        ArrayList<Task> tasks = Storage.load();

        assertEquals(1, tasks.size());
        assertEquals(
                "[E][ ] project meeting (from: Aug 6th 2pm to: Aug 6th 4pm)",
                tasks.get(0).toString()
        );
    }

    /**
     * Tests that multiple tasks are loaded in the correct order
     * with their completion statuses preserved.
     */
    @Test
    public void loadMultipleTasks() throws IOException {
        Files.createDirectories(FILE_PATH.getParent());

        Files.writeString(
                FILE_PATH,
                "T | 1 | read book\n"
                        + "D | 0 | return book | June 6th\n"
                        + "E | 0 | project meeting | Aug 6th 2pm | Aug 6th 4pm"
        );

        ArrayList<Task> tasks = Storage.load();

        assertEquals(3, tasks.size());
        assertTrue(tasks.get(0).isDone());
        assertEquals("[D][ ] return book (by: June 6th)", tasks.get(1).toString());
        assertEquals(
                "[E][ ] project meeting (from: Aug 6th 2pm to: Aug 6th 4pm)",
                tasks.get(2).toString()
        );
    }

    /**
     * Tests that an empty task list is returned when the data file does not exist.
     */
    @Test
    public void loadMissingFile() throws IOException {
        Files.deleteIfExists(FILE_PATH);

        ArrayList<Task> tasks = Storage.load();

        assertTrue(tasks.isEmpty());
    }

    /**
     * Tests that blank lines in the data file are ignored.
     */
    @Test
    public void loadIgnoresBlankLines() throws IOException {
        Files.createDirectories(FILE_PATH.getParent());

        Files.writeString(
                FILE_PATH,
                "\n"
                        + "T | 0 | read book\n"
                        + "\n"
        );

        ArrayList<Task> tasks = Storage.load();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    /**
     * Tests that corrupted task entries are skipped while valid tasks are loaded.
     */
    @Test
    public void loadSkipsCorruptedTask() throws IOException {
        Files.createDirectories(FILE_PATH.getParent());

        Files.writeString(
                FILE_PATH,
                "T | 0 | read book\n"
                        + "THIS IS CORRUPTED\n"
                        + "D | 0 | return book | June 6th"
        );

        ArrayList<Task> tasks = Storage.load();

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals(
                "[D][ ] return book (by: June 6th)",
                tasks.get(1).toString()
        );
    }

    /**
     * Tests that an unknown task type is skipped.
     */
    @Test
    public void loadSkipsUnknownTaskType() throws IOException {
        Files.createDirectories(FILE_PATH.getParent());

        Files.writeString(
                FILE_PATH,
                "T | 0 | read book\n"
                        + "X | 0 | invalid task\n"
                        + "D | 0 | return book | June 6th"
        );

        ArrayList<Task> tasks = Storage.load();

        assertEquals(2, tasks.size());
    }

    /**
     * Tests that task entries with missing or extra fields are skipped.
     */
    @Test
    public void loadSkipsIncorrectFormat() throws IOException {
        Files.createDirectories(FILE_PATH.getParent());

        Files.writeString(
                FILE_PATH,
                "T | 0\n"
                        + "D | 0 | return book\n"
                        + "E | 0 | meeting | 2pm\n"
                        + "T | 0 | valid task"
        );

        ArrayList<Task> tasks = Storage.load();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] valid task", tasks.get(0).toString());
    }
}