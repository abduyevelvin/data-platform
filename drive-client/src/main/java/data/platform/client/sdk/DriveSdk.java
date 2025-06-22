package data.platform.client.sdk;

import data.platform.client.dto.FileEntryDto;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DriveSdk {

    private final String baseUrl;
    private final RestTemplate restTemplate;

    public DriveSdk(String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }

    public List<FileEntryDto> listFiles() {
        var url = baseUrl + "/files";
        var response = restTemplate.getForEntity(url, FileEntryDto[].class);
        var body = response.getBody();

        return body != null ? Arrays.asList(body) : Collections.emptyList();
    }

    public String uploadFile(String srcPath, String dstPath) {
        var url = String.format("%s/%s?srcPath=%s&destPath=%s", baseUrl, "files", srcPath, dstPath);

        return restTemplate.postForObject(url, null, String.class);
    }
}