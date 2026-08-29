/**
 * Represents a command that can be executed by Sioet.
 */
public abstract class Command {

    /**
     * Executes this command.
     *
     * @param tasks the task list
     * @param ui the user interface
     * @param storage the task storage
     * @throws SioetException if the command cannot be completed
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage)
            throws SioetException;

    /**
     * Returns whether this command should exit Sioet.
     *
     * @return true if Sioet should exit, false otherwise
     */
    public boolean isExit() {
        return false;
    }
}