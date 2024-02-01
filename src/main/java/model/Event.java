package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Event extends Task {

    protected String fromString;
    protected String toString;
    protected LocalDateTime from;
    protected LocalDateTime to;

    public Event(String description, String from, String to) {
        super(description);
        this.fromString = from;
        this.toString = to;
        this.from = super.parseDateTime(from);
        this.to = super.parseDateTime(to);
    }

    /**
     * {inheritDoc}
     *
     * @return String representation of the event.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DateTimeFormatter.ofPattern("MMM dd yyyy HHmm")) + " to: " + to.format(DateTimeFormatter.ofPattern("MMM dd yyyy HHmm")) + ")";
    }

    /**
     * Used to obtain the string in the format that the event would be saved in.
     * @return String representation of the event that is savable.
     */
    public String fileSavingString() {
        return "E | " + Integer.toString(super.isDone ? 1 : 0) + " | " + super.description + " | " + this.fromString + " | " + this.toString;
    }
}

// event project meeting /from Mon 2pm /to 4pm
//    ____________________________________________________________
//     Got it. I've added this task:
//       [E][ ] project meeting (from: Mon 2pm to: 4pm)