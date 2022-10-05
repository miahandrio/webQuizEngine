package engine.exceptions;

public class BadRegistrationRequestException extends RuntimeException {
    public BadRegistrationRequestException(String message) {
        super(message);
    }
}
