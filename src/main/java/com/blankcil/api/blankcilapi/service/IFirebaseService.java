package com.blankcil.api.blankcilapi.service;

import com.google.cloud.storage.Storage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IFirebaseService {
    String uploadVideoToFirebase(byte[] videoBytes) throws IOException;
    String uploadImageToFirebase(MultipartFile imageFile, String type) throws IOException;
    void createUserFolder(String userId, String userName) throws IOException;
}
