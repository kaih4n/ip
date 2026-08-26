import java.util.Scanner;

/**
 * Starts the Sioet chatbot application.
 */
public class Sioet {
    private static final String BLUE = "\u001B[34m";
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";

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
                System.out.println(BLUE + "Bye. Hope to see you again soon!" + RESET);
                break;
            }

            System.out.println(BLUE + command + RESET);
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
