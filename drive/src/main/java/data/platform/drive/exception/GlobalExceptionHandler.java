package data.platform.drive.exception;

import data.platform.drive.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

import static java.time.OffsetDateTime.now;
import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Map<Class<? extends Exception>, ErrorCode> ERROR_CODE_MAP = Map.of(
            ResourceNotFoundException.class, ErrorCode.NOT_FOUND,
            ResourceAlreadyExistsException.class, ErrorCode.ALREADY_EXISTS,
            DriveException.class, ErrorCode.DRIVE_ERROR,
            InvalidDestinationException.class, ErrorCode.INVALID_DESTINATION,
            FileSizeExceededException.class, ErrorCode.FILE_SIZE_EXCEEDED,
            SourceAndDestinationSameException.class, ErrorCode.SAME_SOURCE_AND_DESTINATION
    );

    private static final Map<Class<? extends Exception>, HttpStatus> STATUS_MAP = Map.of(
            ResourceNotFoundException.class, NOT_FOUND,
            ResourceAlreadyExistsException.class, CONFLICT,
            DriveException.class, INTERNAL_SERVER_ERROR,
            InvalidDestinationException.class, BAD_REQUEST,
            FileSizeExceededException.class, PAYLOAD_TOO_LARGE,
            SourceAndDestinationSameException.class, BAD_REQUEST
    );

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleException(Exception ex, WebRequest request) {
        ErrorCode errorCode = ERROR_CODE_MAP.getOrDefault(ex.getClass(), ErrorCode.INTERNAL_SERVER_ERROR);
        HttpStatus status = STATUS_MAP.getOrDefault(ex.getClass(), INTERNAL_SERVER_ERROR);

        var errorDetails = new ErrorDetails(
                now(),
                ex.getMessage(),
                request.getDescription(false),
                errorCode
        );

        return ResponseEntity.status(status)
                             .body(errorDetails);
    }
}