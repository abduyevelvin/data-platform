package data.platform.drive.controller;

import data.platform.drive.dto.FileEntryDto;
import data.platform.drive.exception.DriveException;
import data.platform.drive.service.DriveService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DriveController.class)
class DriveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DriveService driveService;

    @Test
    void getFilesAndDirectories_returnsList() throws Exception {
        // given
        var fileItem = createFileTypeDto("test.txt", "file");
        var directoryItem = createFileTypeDto("testDir", "dir");

        // when
        when(driveService.getFilesAndDirectories())
                .thenReturn(List.of(fileItem, directoryItem));

        var response = mockMvc.perform(get("/files"));

        // then
        response.andExpectAll(
                status().isOk(),
                jsonPath("$[0].name").value("test.txt"),
                jsonPath("$[0].type").value("file"),
                jsonPath("$[1].name").value("testDir"),
                jsonPath("$[1].type").value("dir")
        );
    }

    @Test
    void uploadFile_success() throws Exception {
        // when
        var response = mockMvc.perform(post("/files")
                .param("srcPath", "src.txt")
                .param("destPath", "dest/"));

        // then
        response.andExpectAll(
                status().isOk(),
                content().string("File uploaded successfully")
        );
    }

    @Test
    void uploadFile_failure() throws Exception {
        // given
        doThrow(new DriveException("Failed to upload file"))
                .when(driveService)
                .uploadFile(anyString(), anyString());

        // when - then
        mockMvc.perform(post("/files")
                       .param("srcPath", "src.txt")
                       .param("destPath", "dest/"))
               .andExpect(status().isInternalServerError());
    }

    private FileEntryDto createFileTypeDto(String name, String type) {
        return new FileEntryDto(
                name, type
        );
    }
}