package data.platform.drive.controller;

import data.platform.drive.dto.FileEntryDto;
import data.platform.drive.service.DriveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Drive Management",
        description = "APIs for managing files in the Drive Service"
)
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class DriveController {

    private final DriveService driveService;

    @Operation(
            summary = "Get files and directories",
            description = "This endpoint retrieves a list of files and directories from the given path."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Files and directories retrieved successfully"
    )
    @GetMapping()
    public ResponseEntity<List<FileEntryDto>> getFilesAndDirectories() {
        var filesAndDirectories = driveService.getFilesAndDirectories();

        return ResponseEntity.ok(filesAndDirectories);
    }

    @Operation(
            summary = "Upload a file",
            description = "This endpoint allows you to upload a file from the source path to the destination path."
    )
    @ApiResponse(
            responseCode = "200",
            description = "File uploaded successfully"
    )
    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam String srcPath, @RequestParam String destPath) {

        driveService.uploadFile(srcPath, destPath);

        return ResponseEntity.ok("File uploaded successfully");
    }
}
