package sejong.back.exception;

public class UnSupportedUpdateTreeException extends RuntimeException {

    public UnSupportedUpdateTreeException() {
        super();
    }

    public UnSupportedUpdateTreeException(String s) {
        super(s);
    }

    public UnSupportedUpdateTreeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnSupportedUpdateTreeException(Throwable cause) {
        super(cause);
    }
}
