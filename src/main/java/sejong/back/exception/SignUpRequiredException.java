package sejong.back.exception;

public class SignUpRequiredException extends RuntimeException {

    public SignUpRequiredException() {
        super();
    }

    public SignUpRequiredException(String s) {
        super(s);
    }

    public SignUpRequiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public SignUpRequiredException(Throwable cause) {
        super(cause);
    }
}
