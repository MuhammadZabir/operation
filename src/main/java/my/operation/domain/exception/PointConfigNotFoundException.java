package my.operation.domain.exception;

public class PointConfigNotFoundException extends OperationException {
    public PointConfigNotFoundException() {
        super();
    }

    public PointConfigNotFoundException(String message) {
        super(message);
    }

    public PointConfigNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PointConfigNotFoundException(Throwable cause) {
        super(cause);
    }
}
