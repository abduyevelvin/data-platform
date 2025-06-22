package data.platform.drive.service;

import data.platform.drive.dto.FileEntryDto;

import java.util.List;

public interface DriveService {

    List<FileEntryDto> getFilesAndDirectories();

    void uploadFile(String srcPath, String destPath);
}
