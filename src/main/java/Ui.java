import java.util.Scanner;

/**
 * Handles interactions between Sioet and the user through the console.
 */
public class Ui {
    private static final String BLUE = "\u001B[34m";
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";

    private final Scanner scanner;

    /**
     * Creates a user interface that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays the Sioet welcome banner.
     */
    public void showWelcome() {
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
    }

    /**
     * Creates a banner row with the SIOET lettering shown in blue.
     *
     * @param prefix decoration before the lettering
     * @param lettering one row of the SIOET logo
     * @param suffix decoration after the lettering
     * @return a correctly aligned banner row
     */
    private String bannerLine(String prefix, String lettering, String suffix) {
        String content = prefix + lettering + suffix;
        return "║" + prefix + BLUE + lettering + RESET + suffix
                + " ".repeat(30 - content.length()) + "║\n";
    }

    /**
     * Reads one command from the user after displaying the input prompt.
     *
     * @return the command entered by the user, or {@code null} when input ends
     */
    public String readCommand() {
        System.out.print(GREEN + "You: " + RESET);
        System.out.flush();

        if (!scanner.hasNextLine()) {
            return null;
        }

        return scanner.nextLine();
    }

    /**
     * Displays Sioet's goodbye message.
     */
    public void showBye() {
        System.out.println(BLUE + "Bye! Hope to see you again soon!" + RESET);
    }

    /**
     * Displays an error message for an invalid command or task operation.
     *
     * @param message the error message to display
     */
    public void showError(String message) {
        System.out.println(BLUE + "I couldn't do that: " + message + RESET);
    }

    /**
     * Displays a confirmation after a task is added.
     *
     * @param task the task that was added
     * @param taskCount the number of tasks currently in the list
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(BLUE + "Got it. I've added this task:\n  " + task
                + "\nNow you have " + taskCount + " tasks in the list." + RESET);
    }

    /**
     * Displays a confirmation after one or more tasks are deleted.
     *
     * @param deletedTasks the tasks that were deleted
     * @param numberOfDeletedTasks the number of tasks deleted
     * @param taskCount the number of tasks remaining
     */
    public void showTasksDeleted(String deletedTasks, int numberOfDeletedTasks, int taskCount) {
        String message = numberOfDeletedTasks == 1
                ? "Noted. I've removed this task:\n\n"
                : "Noted. I've removed these tasks:\n\n";

        System.out.println(BLUE
                + message
                + deletedTasks.trim()
                + "\nNow you have "
                + taskCount
                + " tasks in the list."
                + RESET);
    }

    /**
     * Displays every task in the task list.
     *
     * @param tasks the tasks to display
     */
    public void showTasks(TaskList tasks) {
        System.out.println(BLUE + "Here are the tasks in your list:" + RESET);
        for (int index = 0; index < tasks.size(); index++) {
            System.out.println(BLUE + (index + 1) + "." + tasks.get(index) + RESET);
        }
    }

    /**
     * Displays a confirmation after marking or unmarking tasks.
     *
     * @param markedTasks the tasks that were changed
     * @param numberOfMarkedTasks the number of tasks changed
     * @param shouldMarkDone whether the tasks were marked as done
     */
    public void showTasksMarked(String markedTasks, int numberOfMarkedTasks,
                                boolean shouldMarkDone) {
        String message;

        if (numberOfMarkedTasks == 1) {
            message = shouldMarkDone
                    ? "Nice! I've marked this task as done:\n\n"
                    : "OK, I've marked this task as not done yet:\n\n";
        } else {
            message = shouldMarkDone
                    ? "Nice! I've marked these tasks as done:\n\n"
                    : "OK, I've marked these tasks as not done yet:\n\n";
        }

        System.out.println(BLUE + message + markedTasks.trim() + RESET);
    }
}