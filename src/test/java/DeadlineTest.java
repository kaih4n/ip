import org.junit.jupiter.api.Test;
import sioet.task.Deadline;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeadlineTest {

    @Test
    public void toString_returnsCorrectDeadlineFormat() {
        Deadline deadline = new Deadline(
                "return book",
                LocalDateTime.of(2026, 6, 6, 23, 59)
        );

        assertEquals(
                "[D][ ] return book (by: Jun 06 2026, 11:59 PM)",
                deadline.toString()
        );
    }

    @Test
    public void toString_completedDeadline_showsCompletedStatus() {
        Deadline deadline = new Deadline(
                "return book",
                LocalDateTime.of(2026, 6, 6, 23, 59)
        );

        deadline.markAsDone();

        assertEquals(
                "[D][X] return book (by: Jun 06 2026, 11:59 PM)",
                deadline.toString()
        );
    }
}

