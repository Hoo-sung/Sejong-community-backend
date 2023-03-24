package sejong.back.exception;

public class DoubleSignUpException extends IllegalArgumentException {

    public DoubleSignUpException() {
        super();
    }

    public DoubleSignUpException(String s) {
        super(s);
    }

    public DoubleSignUpException(String message, Throwable cause) {
        super(message, cause);
    }

    public DoubleSignUpException(Throwable cause) {
        super(cause);
    }


}
