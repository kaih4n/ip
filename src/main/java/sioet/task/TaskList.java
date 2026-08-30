package sioet.task;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Represents the list of tasks managed by sioet.ui.Sioet.
 */
public class TaskList implements Iterable<Task> {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the given tasks.
     *
     * @param tasks the initial tasks
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the task list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Gets a task at the specified index.
     *
     * @param index the zero-based index of the task
     * @return the task at the specified index
     */
    public Task get(int index) {
        return tasks.get(index);
    }


    /**
     * Returns the number of tasks in the list.
     *
     * @return the number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Removes and returns the task at the specified index.
     *
     * @param index the zero-based index of the task
     * @return the removed task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns an iterator over the tasks.
     *
     * @return an iterator over the tasks
     */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
