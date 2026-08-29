import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Parses user commands entered into Sioet.
 */
public class Parser {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    /**
     * Parses the command entered by the user.
     *
     * @param command the command entered by the user
     * @return the parsed command
     * @throws SioetException if the command is not recognised
     */
    public static String parseCommand(String command) throws SioetException {
        if (command.equals("list")) {
            return "list";
        } else if (command.equals("mark") || command.startsWith("mark ")) {
            return "mark";
        } else if (command.equals("unmark") || command.startsWith("unmark ")) {
            return "unmark";
        } else if (command.equals("todo") || command.startsWith("todo ")) {
            return "todo";
        } else if (command.equals("deadline") || command.startsWith("deadline ")) {
            return "deadline";
        } else if (command.equals("event") || command.startsWith("event ")) {
            return "event";
        } else if (command.equals("delete") || command.startsWith("delete ")) {
            return "delete";
        } else {
            throw new SioetException(
                    "I don't recognise that command. Try list, todo, deadline, "
                            + "event, mark, or unmark."
            );
        }
    }

    /**
     * Extracts the argument portion of a command.
     *
     * @param command the complete command
     * @return the text after the command word
     */
    public static String getArguments(String command) {
        int spaceIndex = command.indexOf(" ");

        if (spaceIndex == -1) {
            return "";
        }

        return command.substring(spaceIndex + 1).trim();
    }

    /**
     * Parses a date and time from the Sioet date format.
     *
     * @param text the date and time text
     * @return the parsed date and time
     * @throws SioetException if the date format is invalid
     */
    public static LocalDateTime parseDateTime(String text) throws SioetException {
        try {
            return LocalDateTime.parse(text, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException exception) {
            throw new SioetException(
                    "Please use the date format d/M/yyyy HHmm. "
                            + "Example: 2/12/2019 1800");
        }
    }
}