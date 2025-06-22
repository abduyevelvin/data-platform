package data.platform.drive.util;

import data.platform.drive.exception.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class FileValidationUtil {

    public static void validateSourceFile(Path source, String srcPath) {
        if (!Files.isRegularFile(source)) {
            log.error("Source file {} does not exist or is not a file", source);
            throw new ResourceNotFoundException("Source file does not exist: " + srcPath);
        }
    }

    public static void validateFileSize(Path source, long maxFileSize) throws IOException {
        var size = Files.size(source);
        if (size > maxFileSize) {
            log.error("File size exceeds maximum limit: {} ({} bytes)", source, size);
            throw new FileSizeExceededException(
                    "File size exceeds maximum limit: " + (maxFileSize / (1024 * 1024)) + " MB");
        }
    }

    public static void validateDestinationDir(Path destinationDir) {
        if (!Files.isDirectory(destinationDir)) {
            log.error("Destination directory {} does not exist", destinationDir);
            throw new InvalidDestinationException("Destination is not a directory: " + destinationDir);
        }
    }

    public static void checkDestinationFile(Path source, Path destinationFile) {
        if (!Files.exists(destinationFile)) return;
        try {
            if (Files.isSameFile(source, destinationFile)) {
                log.warn("Source and destination refer to the same file: {}", source);
                throw new SourceAndDestinationSameException(String.format(
                        "Source: %s and destination: %s refer to the same file.",
                        source, destinationFile
                ));
            } else {
                log.warn("Destination file {} already exists", destinationFile);
                throw new ResourceAlreadyExistsException(
                        "File already exists in destination: " + destinationFile);
            }
        } catch (IOException e) {
            log.error("Error while comparing source and destination files", e);
            throw new DriveException("Failed to compare source and destination paths");
        }
    }
}
