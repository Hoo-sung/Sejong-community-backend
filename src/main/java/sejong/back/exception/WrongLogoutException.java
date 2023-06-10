package sejong.back.exception;

public class WrongLogoutException extends RuntimeException{

    public WrongLogoutException() {
        super();
    }

    public WrongLogoutException(String s) {
        super(s);
    }

    public WrongLogoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongLogoutException(Throwable cause) {
        super(cause);
    }
}
