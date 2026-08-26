/**
 * Represents a task that occurs during a specified time interval.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an event task with its description, start time, and end time.
     *
     * @param description the event description
     * @param from the start date or time
     * @param to the end date or time
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    protected String getTaskType() {
        return "E";
    }

    @Override
    protected String getDetails() {
        return " (from: " + from + " to: " + to + ")";
    }
}
