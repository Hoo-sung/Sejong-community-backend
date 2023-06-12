package sejong.back.exception;

public class UnSupportedDeleteTreeException extends RuntimeException {

    public UnSupportedDeleteTreeException() {
        super();
    }

    public UnSupportedDeleteTreeException(String s) {
        super(s);
    }

    public UnSupportedDeleteTreeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnSupportedDeleteTreeException(Throwable cause) {
        super(cause);
    }
}
