package data.platform.drive.exception;

public class FileSizeExceededException extends RuntimeException {

    public FileSizeExceededException(String message) {
        super(message);
    }
}
