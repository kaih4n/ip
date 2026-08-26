/**
 * Represents one task in the task list.
 * A task has a description and is initially incomplete.
 */
public class Task {
    private final String description;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the task description
     */
    public Task(String description) {
        this.description = description;
    }

    /**
     * Returns this task in the format used in task lists.
     *
     * @return the incomplete-task marker followed by the task description
     */
    @Override
    public String toString() {
        return "[ ] " + description;
    }
}
