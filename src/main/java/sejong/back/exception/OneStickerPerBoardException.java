package sejong.back.exception;

public class OneStickerPerBoardException extends RuntimeException{

    public OneStickerPerBoardException() {
        super();
    }

    public OneStickerPerBoardException(String s) {
        super(s);
    }

    public OneStickerPerBoardException(String message, Throwable cause) {
        super(message, cause);
    }

    public OneStickerPerBoardException(Throwable cause) {
        super(cause);
    }
}
