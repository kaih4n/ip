import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Starts the Sioet chatbot application.
 */
public class Sioet {
    private static final String BLUE = "\u001B[34m";
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";
    private static final ArrayList<Task> tasks = Storage.load();
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    /**
     * Displays the Sioet welcome banner.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        String horizontalBorder = "═".repeat(30);
        String banner = "╔" + horizontalBorder + "╗\n"
                + "║%-30s║%n".formatted("      ✦       ･        ✧")
                + bannerLine("   :    ", "╔═╗╦╔═╗╔═╗╔╦╗", "    *")
                + bannerLine("  ﾟ     ", "╚═╗║║ ║║╣  ║", "      ✦")
                + bannerLine("     ✧  ", "╚═╝╩╚═╝╚═╝ ╩", "   ･")
                + "║%-30s║%n".formatted("   *        :       ﾟ     ✧")
                + "╚" + horizontalBorder + "╝\n";
        System.out.println(banner
                           + BLUE + "Hello! I'm Sioet.\n"
                           + "What can I do for you?" + RESET
        );

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(GREEN + "You: " + RESET);
            System.out.flush();

            if (!scanner.hasNextLine()) {
                break;
            }

            String command = scanner.nextLine();

            if (command.equals("bye")) {
                System.out.println(BLUE + "Bye! Hope to see you again soon!" + RESET);
                break;
            }

            try {
                handleCommand(command);
            } catch (SioetException exception) {
                System.out.println(BLUE + "I couldn't do that: " + exception.getMessage() + RESET);
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
        if (command.equals("list")) {
            printTasks();
        } else if (command.equals("mark") || command.startsWith("mark ")) {
            markTasks(command.substring("mark".length()).trim(), true);
        } else if (command.equals("unmark") || command.startsWith("unmark ")) {
            markTasks(command.substring("unmark".length()).trim(), false);
        } else if (command.equals("todo") || command.startsWith("todo ")) {
            addTodo(command.substring("todo".length()).trim());
        } else if (command.equals("deadline") || command.startsWith("deadline ")) {
            addDeadline(command.substring("deadline".length()).trim());
        } else if (command.equals("event") || command.startsWith("event ")) {
            addEvent(command.substring("event".length()).trim());
        } else if (command.equals("delete") || command.startsWith("delete ")) {
            deleteTask(command.substring("delete".length()).trim());
        } else {
            throw new SioetException("I don't recognise that command. Try list, todo, deadline, event, mark, or unmark.");
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
            throw new SioetException("use: deadline DESCRIPTION /by DATE. Example: deadline final project /by 1/2/2034");
        }
        String description = taskText.substring(0, byMarkerIndex).trim();
        String byText = taskText.substring(
                byMarkerIndex + " /by ".length()).trim();

        try {
            LocalDateTime by = LocalDateTime.parse(byText, DATE_TIME_FORMATTER);
            addTask(new Deadline(description, by));
        } catch (DateTimeParseException exception) {
            throw new SioetException(
                    "Please use the date format d/M/yyyy HHmm. "
                            + "Example: 2/12/2019 1800");
        }
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

        try {
            LocalDateTime from = LocalDateTime.parse(fromText, DATE_TIME_FORMATTER);
            LocalDateTime to = LocalDateTime.parse(toText, DATE_TIME_FORMATTER);

            addTask(new Event(description, from, to));
        } catch (DateTimeParseException exception) {
            throw new SioetException(
                    "Please use the date format d/M/yyyy HHmm. "
                            + "Example: event birthday party /from 7/9/2026 1900 /to 7/9/2026 2300");
        }
    }

    /**
     * Stores a task and confirms that it was added.
     *
     * @param task the task to store
     */
    private static void addTask(Task task) {
        tasks.add(task);
        Storage.save(tasks);

        System.out.println(BLUE + "Got it. I've added this task:\n  " + task
                + "\nNow you have " + tasks.size() + " tasks in the list." + RESET);
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

        String message;
        if (taskIndexes.length == 1) {
            message = "Noted. I've removed this task:\n\n";
        } else {
            message = "Noted. I've removed these tasks:\n\n";
        }

        System.out.println(BLUE
                + message
                + deletedTasks.toString().trim()
                + "\nNow you have "
                + tasks.size()
                + " tasks in the list."
                + RESET);
    }


    /**
     * Displays every stored task in the order it was entered.
     */
    private static void printTasks() {
        System.out.println(BLUE + "Here are the tasks in your list:" + RESET);
        for (int index = 0; index < tasks.size(); index++) {
            System.out.println(BLUE + (index + 1) + "." + tasks.get(index) + RESET);
        }
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

        Storage.save(tasks);

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

        String message;
        if (taskIndexes.length == 1) {
            message = shouldMarkDone
                    ? "Nice! I've marked this task as done:\n\n"
                    : "OK, I've marked this task as not done yet:\n\n";
        } else {
            message = shouldMarkDone
                    ? "Nice! I've marked these tasks as done:\n\n"
                    : "OK, I've marked these tasks as not done yet:\n\n";
        }
        System.out.println(BLUE + message + markedTasks.toString().trim() + RESET);
    }

    /**
     * Creates a banner row with the SIOET lettering shown in blue.
     *
     * @param prefix decoration before the lettering
     * @param lettering one row of the SIOET logo
     * @param suffix decoration after the lettering
     * @return a correctly aligned banner row
     */
    private static String bannerLine(String prefix, String lettering, String suffix) {
        String content = prefix + lettering + suffix;
        return "║" + prefix + BLUE + lettering + RESET + suffix
                + " ".repeat(30 - content.length()) + "║\n";
    }
}
