package sejong.back.exception;

public class UnSupportedDeleteStickerException extends RuntimeException {

    public UnSupportedDeleteStickerException() {
        super();
    }

    public UnSupportedDeleteStickerException(String s) {
        super(s);
    }

    public UnSupportedDeleteStickerException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnSupportedDeleteStickerException(Throwable cause) {
        super(cause);
    }

}
