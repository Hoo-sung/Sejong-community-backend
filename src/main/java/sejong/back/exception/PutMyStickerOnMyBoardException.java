package sejong.back.exception;

public class PutMyStickerOnMyBoardException extends RuntimeException {

    public PutMyStickerOnMyBoardException() {
        super();
    }

    public PutMyStickerOnMyBoardException(String s) {
        super(s);
    }

    public PutMyStickerOnMyBoardException(String message, Throwable cause) {
        super(message, cause);
    }

    public PutMyStickerOnMyBoardException(Throwable cause) {
        super(cause);
    }
}
