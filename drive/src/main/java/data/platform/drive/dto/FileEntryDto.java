package data.platform.drive.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Data Transfer Object representing a file or directory.")
public record FileEntryDto(
        @Schema(description = "Name of the file or directory")
        String name,
        @Schema(description = "Type: DIR for directory, FILE for file")
        String type
) {
}
