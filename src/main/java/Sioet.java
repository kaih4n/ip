/**
 * Starts the Sioet chatbot application.
 */
public class Sioet {
    /**
     * Displays the Sioet welcome banner.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        String horizontalBorder = "═".repeat(30);
        String banner = "╔" + horizontalBorder + "╗\n"
                + "║%-30s║%n".formatted("      ✦       ･        ✧")
                + "║%-30s║%n".formatted("   :    ╔═╗╦╔═╗╔═╗╔╦╗    *")
                + "║%-30s║%n".formatted("  ﾟ     ╚═╗║║ ║║╣  ║      ✦")
                + "║%-30s║%n".formatted("     ✧  ╚═╝╩╚═╝╚═╝ ╩   ･")
                + "║%-30s║%n".formatted("   *        :       ﾟ     ✧")
                + "╚" + horizontalBorder + "╝\n";
        System.out.println(banner
                           + "Hello! I'm Sioet.\n"
                           + "What can I do for you?\n\n"
                           + "--------------------------------\n"
                           + "Bye. Hope to see you again soon!"
        );

    }
}
