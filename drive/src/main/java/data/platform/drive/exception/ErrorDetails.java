package data.platform.drive.exception;

import data.platform.drive.enums.ErrorCode;

import java.time.OffsetDateTime;

record ErrorDetails(
        OffsetDateTime timestamp,
        String message,
        String path,
        ErrorCode errorCode
) {
}
