import java.time.LocalDateTime;
import java.util.Arrays;

public class Sioet {
    private static final Storage storage = new Storage();
    private static final TaskList tasks = storage.load();
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

            try {
                Command parsedCommand = Parser.parse(command);
                parsedCommand.execute(tasks, ui, storage);

                if (parsedCommand.isExit()) {
                    break;
                }
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
        Command parsedCommand = Parser.parse(command);
        parsedCommand.execute(tasks, ui, storage);    }

}
