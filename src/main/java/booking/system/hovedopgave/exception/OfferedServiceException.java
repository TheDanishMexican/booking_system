package booking.system.hovedopgave.exception;

public class OfferedServiceException extends RuntimeException {

    public OfferedServiceException(String message) {
        super(message);
    }

    public OfferedServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

