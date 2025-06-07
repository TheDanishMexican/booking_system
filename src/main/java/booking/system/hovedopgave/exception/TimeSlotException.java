package booking.system.hovedopgave.exception;

public class TimeSlotException extends RuntimeException {

    public TimeSlotException(String message) {
        super(message);
    }

    public TimeSlotException(String message, Throwable cause) {
        super(message, cause);
    }
}

