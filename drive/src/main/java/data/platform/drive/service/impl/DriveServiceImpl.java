package data.platform.drive.service.impl;

import data.platform.drive.dto.FileEntryDto;
import data.platform.drive.exception.DriveException;
import data.platform.drive.service.DriveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import static data.platform.drive.util.FileValidationUtil.*;

@Slf4j
@Service
public class DriveServiceImpl implements DriveService {

    private static final ConcurrentHashMap<String, Object> FILE_LOCKS = new ConcurrentHashMap<>();

    @Value("${data.platform.drive.path}")
    private String drivePath;

    @Value("${data.platform.file.max.size}")
    private long maxFileSize;

    @Override
    public List<FileEntryDto> getFilesAndDirectories() {
        var path = Paths.get(drivePath);

        if (Files.exists(path) && Files.isDirectory(path)) {
            try (var stream = Files.list(path)) {
                return stream
                        .map(p -> new FileEntryDto(
                                p.getFileName()
                                 .toString(),
                                Files.isDirectory(p) ? "dir" : "file"
                        ))
                        .toList();
            } catch (IOException e) {
                log.error("Error reading files from drive path: {}", drivePath, e);
                throw new DriveException("Error reading files from drive path: " + drivePath);
            }
        }

        return List.of();
    }

    @Override
    public void uploadFile(String srcPath, String destPath) {
        var source = Paths.get(srcPath)
                          .toAbsolutePath()
                          .normalize();
        var lockKey = source.toString();
        var lock = (ReentrantLock) FILE_LOCKS.computeIfAbsent(lockKey, k -> new ReentrantLock());

        lock.lock();
        try {
            validateSourceFile(source, srcPath);
            validateFileSize(source, maxFileSize);

            var destinationDir = Paths.get(drivePath)
                                      .resolve(destPath);
            validateDestinationDir(destinationDir);

            var destinationFile = destinationDir.resolve(source.getFileName())
                                                .toAbsolutePath()
                                                .normalize();
            checkDestinationFile(source, destinationFile);

            Files.move(source, destinationFile);
            log.info("Successfully moved file from {} to {}", source, destinationFile);
        } catch (IOException e) {
            log.error("I/O error while moving file from {} to {}", srcPath, destPath, e);
            throw new DriveException("Error uploading file: " + srcPath + " to " + destPath);
        } finally {
            lock.unlock();
            FILE_LOCKS.remove(lockKey);
        }
    }
}
