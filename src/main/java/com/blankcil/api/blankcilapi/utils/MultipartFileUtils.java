package com.blankcil.api.blankcilapi.utils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MultipartFileUtils {
    public static MultipartFile createDefaultImage() {
        try {
            byte[] defaultImageData = Files.readAllBytes(Paths.get("src/main/resources/images/NoImgDefault.png"));
            return new MockMultipartFile("NoImgDefault.png", "NoImgDefault.png", "image/png", defaultImageData);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
