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
                System.out.println(BLUE + "Bye！ Hope to see you again soon!" + RESET);
                break;
            }

            if (command.equals("list")) {
                printTasks();
            } else {
                addTask(command);
            }
        }
    }

    /**
     * Stores a task entered by the user and confirms that it was added.
     *
     * @param task the text to store as a task
     */
    private static void addTask(String task) {
        if (taskCount == MAX_TASKS) {
            System.out.println(BLUE + "Sorry, the task list is full." + RESET);
            return;
        }

        tasks[taskCount] = new Task(task);
        taskCount++;
        System.out.println(BLUE + "added: " + task + RESET);
    }

    /**
     * Displays every stored task in the order it was entered.
     */
    private static void printTasks() {
        for (int index = 0; index < taskCount; index++) {
            System.out.println(BLUE + (index + 1) + ". " + tasks[index] + RESET);
        }
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
