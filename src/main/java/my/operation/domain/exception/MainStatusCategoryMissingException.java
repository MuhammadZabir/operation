package my.operation.domain.exception;

public class MainStatusCategoryMissingException extends OperationException {
    public MainStatusCategoryMissingException() {
        super();
    }

    public MainStatusCategoryMissingException(String message) {
        super(message);
    }

    public MainStatusCategoryMissingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MainStatusCategoryMissingException(Throwable cause) {
        super(cause);
    }
}
