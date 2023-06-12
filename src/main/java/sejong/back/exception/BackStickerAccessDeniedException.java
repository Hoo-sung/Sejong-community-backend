package sejong.back.exception;

public class BackStickerAccessDeniedException extends RuntimeException {

    public BackStickerAccessDeniedException() {
        super();
    }

    public BackStickerAccessDeniedException(String s) {
        super(s);
    }

    public BackStickerAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public BackStickerAccessDeniedException(Throwable cause) {
        super(cause);
    }
}
