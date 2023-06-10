package sejong.back.exception;

public class WrongSessionIdException extends RuntimeException{

    public WrongSessionIdException() {
        super();
    }

    public WrongSessionIdException(String s) {
        super(s);
    }
}
