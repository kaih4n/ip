import java.util.Scanner;

/**
 * Starts the Sioet chatbot application.
 */
public class Sioet {
    private static final int MAX_TASKS = 100;
    private static final String BLUE = "\u001B[34m";
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";
    private static final Task[] tasks = new Task[MAX_TASKS];
    private static int taskCount = 0;

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
                System.out.println(BLUE + "Bye！dead Hope to see you again soon!" + RESET);
                break;
            }

            if (command.equals("list")) {
                printTasks();
            } else if (command.startsWith("mark ")) {
                markTasks(command.substring("mark ".length()), true);
            } else if (command.startsWith("unmark ")) {
                markTasks(command.substring("unmark ".length()), false);
            } else if (command.startsWith("todo ")) {
                addTodo(command.substring("todo ".length()));
            } else if (command.startsWith("deadline ")) {
                addDeadline(command.substring("deadline ".length()));
            } else if (command.startsWith("event ")) {
                addEvent(command.substring("event ".length()));
            } else {
                System.out.println(BLUE + "I don't understand that command yet." + RESET);
            }
        }
    }

    /**
     * Creates and stores a todo task.
     *
     * @param description the todo description
     */
    private static void addTodo(String description) {
        if (description.isBlank()) {
            System.out.println(BLUE + "Please provide a description for the todo." + RESET);
            return;
        }
        addTask(new Todo(description.trim()));
    }

    /**
     * Creates and stores a deadline task from text in the form {@code description /by due date}.
     *
     * @param taskText the deadline details entered by the user
     */
    private static void addDeadline(String taskText) {
        int byMarkerIndex = taskText.indexOf(" /by ");
        if (byMarkerIndex < 1 || byMarkerIndex + " /by ".length() >= taskText.length()) {
            System.out.println(BLUE + "Use: deadline DESCRIPTION /by DATE" + RESET);
            return;
        }
        String description = taskText.substring(0, byMarkerIndex).trim();
        String by = taskText.substring(byMarkerIndex + " /by ".length()).trim();
        addTask(new Deadline(description, by));
    }

    /**
     * Creates and stores an event task from text in the form
     * {@code description /from start /to end}.
     *
     * @param taskText the event details entered by the user
     */
    private static void addEvent(String taskText) {
        int fromMarkerIndex = taskText.indexOf(" /from ");
        int toMarkerIndex = taskText.indexOf(" /to ");
        if (fromMarkerIndex < 1 || toMarkerIndex <= fromMarkerIndex + " /from ".length()
                || toMarkerIndex + " /to ".length() >= taskText.length()) {
            System.out.println(BLUE + "Use: event DESCRIPTION /from START /to END" + RESET);
            return;
        }
        String description = taskText.substring(0, fromMarkerIndex).trim();
        String from = taskText.substring(fromMarkerIndex + " /from ".length(), toMarkerIndex).trim();
        String to = taskText.substring(toMarkerIndex + " /to ".length()).trim();
        addTask(new Event(description, from, to));
    }

    /**
     * Stores a task and confirms that it was added.
     *
     * @param task the task to store
     */
    private static void addTask(Task task) {
        if (taskCount == MAX_TASKS) {
            System.out.println(BLUE + "Sorry, the task list is full." + RESET);
            return;
        }

        tasks[taskCount] = task;
        taskCount++;
        System.out.println(BLUE + "Got it. I've added this task:\n  " + task
                + "\nNow you have " + taskCount + " tasks in the list." + RESET);
    }

    /**
     * Displays every stored task in the order it was entered.
     */
    private static void printTasks() {
        System.out.println(BLUE + "Here are the tasks in your list:" + RESET);
        for (int index = 0; index < taskCount; index++) {
            System.out.println(BLUE + (index + 1) + "." + tasks[index] + RESET);
        }
    }

    /**
     * Changes one or more tasks' completion states using their one-based list numbers.
     *
     * @param taskNumbersText comma-separated task numbers supplied after a command
     * @param shouldMarkDone whether the task should be completed
     */
    private static void markTasks(String taskNumbersText, boolean shouldMarkDone) {
        String[] taskNumberTexts = taskNumbersText.split(",", -1);
        int[] taskIndexes = new int[taskNumberTexts.length];

        for (int index = 0; index < taskNumberTexts.length; index++) {
            try {
                taskIndexes[index] = Integer.parseInt(taskNumberTexts[index].trim()) - 1;
            } catch (NumberFormatException exception) {
                System.out.println(BLUE + "Please provide valid task numbers separated by commas." + RESET);
                return;
            }

            if (taskIndexes[index] < 0 || taskIndexes[index] >= taskCount) {
                System.out.println(BLUE + "One or more task numbers do not exist." + RESET);
                return;
            }
        }

        StringBuilder markedTasks = new StringBuilder();
        for (int taskIndex : taskIndexes) {
            Task task = tasks[taskIndex];
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
