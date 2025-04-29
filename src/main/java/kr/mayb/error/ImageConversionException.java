package kr.mayb.error;

public class ImageConversionException extends RuntimeException {
    public ImageConversionException() {
        this("Fail to compress img");
    }

    public ImageConversionException(String message) {
        super(message);
    }
}
