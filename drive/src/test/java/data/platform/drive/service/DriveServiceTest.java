package data.platform.drive.service;

import data.platform.drive.exception.*;
import data.platform.drive.service.impl.DriveServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.*;

class DriveServiceTest {

    private DriveService service;
    private Path tempDrive;

    @BeforeEach
    void setUp() throws IOException {
        service = new DriveServiceImpl();
        tempDrive = Files.createTempDirectory("drive-test");
        ReflectionTestUtils.setField(service, "drivePath", tempDrive.toString());
        ReflectionTestUtils.setField(service, "maxFileSize", 10 * 1024 * 1024L);
    }

    @AfterEach
    void tearDown() throws IOException {
        try (var walk = Files.walk(tempDrive)) {
            walk.sorted((a, b) -> b.compareTo(a))
                .forEach(p -> {
                    try {
                        Files.deleteIfExists(p);
                    } catch (IOException ignored) {
                    }
                });
        }
    }

    @Test
    void uploadFile_success() throws IOException {
        // given
        var src = Files.createTempFile("src", ".txt");
        Files.writeString(src, "test");
        var destDir = tempDrive.resolve("dest");
        Files.createDirectory(destDir);

        // when
        service.uploadFile(src.toString(), "dest");

        // then
        assertTrue(Files.exists(destDir.resolve(src.getFileName())));
        assertFalse(Files.exists(src));
    }

    @Test
    void uploadFile_sourceNotFound() {
        // given
        var destDir = tempDrive.resolve("dest");
        assertDoesNotThrow(() -> Files.createDirectory(destDir));

        // when - then
        assertThrows(
                ResourceNotFoundException.class, () ->
                        service.uploadFile("nonexistent.txt", "dest")
        );
    }

    @Test
    void uploadFile_destinationNotDirectory() throws IOException {
        // given
        var src = Files.createTempFile("src", ".txt");
        Files.writeString(src, "test");
        var notDir = tempDrive.resolve("notdir.txt");
        Files.createFile(notDir);

        // when - then
        assertThrows(
                InvalidDestinationException.class, () ->
                        service.uploadFile(src.toString(), "notdir.txt")
        );
    }

    @Test
    void uploadFile_fileExistsInDestination() throws IOException {
        // given
        var src = Files.createTempFile("src", ".txt");
        Files.writeString(src, "test");
        var destDir = tempDrive.resolve("dest");
        Files.createDirectory(destDir);
        Files.createFile(destDir.resolve(src.getFileName()));

        // when - then
        assertThrows(
                ResourceAlreadyExistsException.class, () ->
                        service.uploadFile(src.toString(), "dest")
        );
    }

    @Test
    void uploadFile_sourceAndDestinationAreSame_throwsDriverException() throws IOException {
        // given
        var src = Files.createTempFile("src", ".txt");
        Files.writeString(src, "test");
        var destDir = tempDrive.resolve("dest");
        Files.createDirectory(destDir);

        ReflectionTestUtils.setField(
                service,
                "drivePath",
                src.getParent()
                   .toString()
        );

        // when - then
        assertThrows(
                SourceAndDestinationSameException.class,
                () -> service.uploadFile(src.toString(), "")
        );
    }

    @Test
    void uploadFile_fileTooLarge() throws IOException {
        // given
        var src = Files.createTempFile("large", ".bin");
        var data = new byte[10 * 1024 * 1024 + 1];
        Files.write(src, data);
        var destDir = tempDrive.resolve("dest");
        Files.createDirectory(destDir);

        // Set maxFileSize to 10 MB
        ReflectionTestUtils.setField(service, "maxFileSize", 10 * 1024 * 1024L);

        // when - then
        var ex = assertThrows(
                FileSizeExceededException.class,
                () -> service.uploadFile(src.toString(), "dest")
        );
        assertTrue(ex.getMessage()
                     .contains("File size exceeds maximum limit"));
    }

    @Test
    void getFilesAndDirectories_returnsCorrectTypes() throws IOException {
        // given
        var dir = tempDrive.resolve("folder");
        var file = tempDrive.resolve("file.txt");
        Files.createDirectory(dir);
        Files.createFile(file);

        // when
        var items = service.getFilesAndDirectories();

        // then
        assertSoftly(as -> {
            as.assertThat(items)
              .hasSize(2);
            as.assertThat(items.stream()
                               .anyMatch(i -> i.name()
                                               .equals("folder") && i.type()
                                                                     .equals("dir")))
              .isTrue();
            as.assertThat(items.stream()
                               .anyMatch(i -> i.name()
                                               .equals("file.txt") && i.type()
                                                                       .equals("file")))
              .isTrue();
        });
    }
}