package sejong.back.exception;

public class WrongLoginException extends RuntimeException {

    public WrongLoginException() {
        super();
    }

    public WrongLoginException(String s) {
        super(s);
    }

    public WrongLoginException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongLoginException(Throwable cause) {
        super(cause);
    }
}
