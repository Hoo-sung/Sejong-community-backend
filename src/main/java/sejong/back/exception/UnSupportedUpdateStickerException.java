package sejong.back.exception;

public class UnSupportedUpdateStickerException extends  RuntimeException{

    public UnSupportedUpdateStickerException() {
        super();
    }

    public UnSupportedUpdateStickerException(String s) {
        super(s);
    }

    public UnSupportedUpdateStickerException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnSupportedUpdateStickerException(Throwable cause) {
        super(cause);
    }
}
