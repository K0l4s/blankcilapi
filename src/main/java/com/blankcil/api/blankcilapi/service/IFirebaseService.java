package com.blankcil.api.blankcilapi.service;

import com.google.cloud.storage.Storage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IFirebaseService {
    String uploadFileToFirebase(byte[] videoBytes) throws IOException;
    void createUserFolder(String userId, String userName) throws IOException;
}
