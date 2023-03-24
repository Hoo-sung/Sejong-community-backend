package sejong.back.exception;

public class WrongSignUpException extends IllegalArgumentException{

    public WrongSignUpException() {
        super();
    }

    public WrongSignUpException(String s) {
        super(s);
    }

    public WrongSignUpException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongSignUpException(Throwable cause) {
        super(cause);
    }
}
