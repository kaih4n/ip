import java.time.LocalDateTime;
import java.util.Arrays;

public class Sioet {
    private static final TaskList tasks = Storage.load();
    private static final Ui ui = new Ui();

    /**
     * Starts the Sioet chatbot application.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        ui.showWelcome();

        while (true) {
            String command = ui.readCommand();

            if (command == null) {
                break;
            }

            if (command.equals("bye")) {
                ui.showBye();
                break;
            }

            try {
                handleCommand(command);
            } catch (SioetException exception) {
                ui.showError(exception.getMessage());
            }
        }
    }

    /**
     * Interprets and carries out one user command.
     *
     * @param command the command entered by the user
     * @throws SioetException if the command is unknown or contains invalid details
     */
    private static void handleCommand(String command) throws SioetException {
        String commandType = Parser.parseCommand(command);
        String arguments = Parser.getArguments(command);

        if (commandType.equals("list")) {
            printTasks();
        } else if (commandType.equals("mark")) {
            markTasks(arguments, true);
        } else if (commandType.equals("unmark")) {
            markTasks(arguments, false);
        } else if (commandType.equals("todo")) {
            addTodo(arguments);
        } else if (commandType.equals("deadline")) {
            addDeadline(arguments);
        } else if (commandType.equals("event")) {
            addEvent(arguments);
        } else if (commandType.equals("delete")) {
            deleteTask(arguments);
        }
    }

    /**
     * Creates and stores a todo task.
     *
     * @param description the todo description
     */
    private static void addTodo(String description) throws SioetException {
        if (description.isBlank()) {
            throw new SioetException("use: todo DESCRIPTION. Example: todo read chapter 5");
        }
        addTask(new Todo(description));
    }

    /**
     * Creates and stores a deadline task from text in the form {@code description /by due date}.
     *
     * @param taskText the deadline details entered by the user
     */
    private static void addDeadline(String taskText) throws SioetException {
        int byMarkerIndex = taskText.indexOf(" /by ");
        if (byMarkerIndex < 1 || byMarkerIndex + " /by ".length() >= taskText.length()) {
            throw new SioetException("use: deadline DESCRIPTION /by DATE. Example: deadline final project /by 1/2/2034 0000");
        }
        String description = taskText.substring(0, byMarkerIndex).trim();
        String byText = taskText.substring(
                byMarkerIndex + " /by ".length()).trim();

        LocalDateTime by = Parser.parseDateTime(byText);
        addTask(new Deadline(description, by));
    }

    /**
     * Creates and stores an event task from text in the form
     * {@code description /from start /to end}.
     *
     * @param taskText the event details entered by the user
     */
    private static void addEvent(String taskText) throws SioetException {
        int fromMarkerIndex = taskText.indexOf(" /from ");
        int toMarkerIndex = taskText.indexOf(" /to ");

        if (fromMarkerIndex < 1
                || toMarkerIndex <= fromMarkerIndex + " /from ".length()
                || toMarkerIndex + " /to ".length() >= taskText.length()) {
            throw new SioetException(
                    "use: event DESCRIPTION /from START /to END. "
                            + "Example: event birthday party /from 7/9/2026 1900 /to 7/9/2026 2300");
        }

        String description = taskText.substring(0, fromMarkerIndex).trim();
        String fromText = taskText.substring(
                fromMarkerIndex + " /from ".length(), toMarkerIndex).trim();
        String toText = taskText.substring(
                toMarkerIndex + " /to ".length()).trim();

        LocalDateTime from = Parser.parseDateTime(fromText);
        LocalDateTime to = Parser.parseDateTime(toText);

        addTask(new Event(description, from, to));
    }

    /**
     * Stores a task and confirms that it was added.
     *
     * @param task the task to store
     */
    private static void addTask(Task task) {
        tasks.add(task);
        Storage.save(tasks);

        ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Deletes one or more tasks using their one-based list numbers.
     *
     * @param taskNumbersText comma-separated task numbers supplied after a command
     */
    private static void deleteTask(String taskNumbersText) throws SioetException {
        if (taskNumbersText.isBlank()) {
            throw new SioetException("Provide at least one task number. Example: delete 1, 2");
        }

        String[] taskNumberTexts = taskNumbersText.split(",", -1);
        int[] taskIndexes = new int[taskNumberTexts.length];

        for (int index = 0; index < taskNumberTexts.length; index++) {
            try {
                taskIndexes[index] =
                        Integer.parseInt(taskNumberTexts[index].trim()) - 1;
            } catch (NumberFormatException exception) {
                throw new SioetException("Task numbers must be whole numbers separated by commas.");
            }

            if (taskIndexes[index] < 0
                    || taskIndexes[index] >= tasks.size()) {
                throw new SioetException("One or more task numbers are not in your list.");
            }
        }

        StringBuilder deletedTasks = new StringBuilder();

        /*
         * Delete from the largest index to the smallest index.
         * This prevents deleting one task from changing the index
         * of another task that we still need to delete.
         */
        Arrays.sort(taskIndexes);

        for (int index = taskIndexes.length - 1; index >= 0; index--) {
            Task deletedTask = tasks.remove(taskIndexes[index]);
            deletedTasks.insert(0, deletedTask + "\n");
        }

        Storage.save(tasks);
        ui.showTasksDeleted(deletedTasks.toString(), taskIndexes.length, tasks.size());
    }


    /**
     * Displays every stored task in the order it was entered.
     */
    private static void printTasks() {
        ui.showTasks(tasks);
    }

    /**
     * Changes one or more tasks' completion states using their one-based list numbers.
     *
     * @param taskNumbersText comma-separated task numbers supplied after a command
     * @param shouldMarkDone whether the task should be completed
     */
    private static void markTasks(String taskNumbersText, boolean shouldMarkDone) throws SioetException {
        if (taskNumbersText.isBlank()) {
            throw new SioetException("Provide at least one task number. Example: mark 1, 2");
        }
        String[] taskNumberTexts = taskNumbersText.split(",", -1);
        int[] taskIndexes = new int[taskNumberTexts.length];

        for (int index = 0; index < taskNumberTexts.length; index++) {
            try {
                taskIndexes[index] = Integer.parseInt(taskNumberTexts[index].trim()) - 1;
            } catch (NumberFormatException exception) {
                throw new SioetException("Task numbers must be whole numbers separated by commas.");
            }

            if (taskIndexes[index] < 0 || taskIndexes[index] >= tasks.size()) {
                throw new SioetException("One or more task numbers are not in your list.");
            }
        }

        StringBuilder markedTasks = new StringBuilder();
        for (int taskIndex : taskIndexes) {
            Task task = tasks.get(taskIndex);
            if (shouldMarkDone) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }
            markedTasks.append(task).append('\n');
        }

        Storage.save(tasks);

        ui.showTasksMarked(
                markedTasks.toString(),
                taskIndexes.length,
                shouldMarkDone
        );
    }
}
