package sejong.back.exception;

public class WrongSessionIdException extends IllegalAccessException{

    public WrongSessionIdException() {
        super();
    }

    public WrongSessionIdException(String s) {
        super(s);
    }
}
