package sioet.command;

import sioet.task.TaskList;
import sioet.task.Task;
import sioet.task.Event;
import sioet.ui.Ui;
import sioet.storage.Storage;
import sioet.SioetException;

import java.time.LocalDateTime;

/**
 * Represents the command that adds an event task.
 */
public class EventCommand extends Command {
    private final String taskText;

    /**
     * Creates an event command.
     *
     * @param taskText the event details
     */
    public EventCommand(String taskText) {
        this.taskText = taskText;
    }

    /**
     * Executes the event command.
     *
     * @param tasks the task list
     * @param ui the user interface
     * @param storage the task storage
     * @throws SioetException if the event format is invalid
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws SioetException {
        int fromMarkerIndex = taskText.indexOf(" /from ");
        int toMarkerIndex = taskText.indexOf(" /to ");

        if (fromMarkerIndex < 1
                || toMarkerIndex <= fromMarkerIndex + " /from ".length()
                || toMarkerIndex + " /to ".length() >= taskText.length()) {
            throw new SioetException(
                    "use: event DESCRIPTION /from START /to END. "
                            + "Example: event birthday party /from "
                            + "7/9/2026 1900 /to 7/9/2026 2300");
        }

        String description = taskText.substring(0, fromMarkerIndex).trim();
        String fromText = taskText.substring(
                fromMarkerIndex + " /from ".length(), toMarkerIndex).trim();
        String toText = taskText.substring(
                toMarkerIndex + " /to ".length()).trim();

        LocalDateTime from = Parser.parseDateTime(fromText);
        LocalDateTime to = Parser.parseDateTime(toText);

        Task task = new Event(description, from, to);
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }
}