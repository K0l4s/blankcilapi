package com.blankcil.api.blankcilapi.service;

import com.blankcil.api.blankcilapi.entity.UserEntity;
import com.blankcil.api.blankcilapi.repository.UserRepository;
import com.blankcil.api.blankcilapi.utils.FFmpegUtil;
import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;


@Service
public class FirebaseServiceImpl implements IFirebaseService {
    @Value("${firebase.storage.bucket}")
    private String bucketName;

    @Autowired
    private Storage firebaseStorage;

    @Autowired
    private UserRepository userRepository;

    @Override
    public String uploadFileToFirebase(byte[] videoBytes) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        String userId = userEntity.getId().toString();
        createUserFolder(userId, userEmail);

        Storage storage = firebaseStorage;

        // Tạo tên file video duy nhất
        String fileName = UUID.randomUUID() + ".mp4";

        // Upload video byte array lên bucket Firebase Storage
        String folderName = "main/" + userId + "-" + userEmail + "/";
        BlobId blobId = BlobId.of(bucketName, folderName + fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("video/mp4").build();
        Blob blob = storage.create(blobInfo, videoBytes);

        FFmpegUtil.deleteTempFile(FFmpegUtil.VIDEO_FILE);
        return  blob.getMediaLink();
    }

    public String storeTempFile(MultipartFile file) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();

        // Tạo thư mục tạm cho người dùng hiện tại nếu nó chưa tồn tại
        String userTempDirectory = currentUser + "/";
//        createTempFolder(userTempDirectory);

        // Generate unique file name
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        String filePath = userTempDirectory + fileName;

        // Lưu tệp tạm vào thư mục tạm của người dùng hiện tại
        BlobId blobId = BlobId.of(bucketName, filePath);

        try (InputStream inputStream = file.getInputStream()) {
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
            Storage storage = firebaseStorage;
            storage.create(blobInfo, inputStream.readAllBytes());
        }

        return filePath;
    }

    @Override
    public void createUserFolder(String userId, String userName) throws IOException {
        String folderName = "main/" + userId + "-" + userName + "/";
        BlobId folderBlobId = BlobId.of(bucketName, folderName);
        Storage storage = firebaseStorage;
        // Kiểm tra xem thư mục đã tồn tại chưa
        if (!isFolderExists(storage, folderBlobId)) {
            // Nếu chưa tồn tại, tạo mới thư mục
            BlobInfo blobInfo = BlobInfo.newBuilder(folderBlobId).build();
            storage.create(blobInfo);
        }
    }

    private boolean isFolderExists(Storage storage, BlobId blobId) {
        Blob blob = storage.get(blobId);
        return blob != null && blob.exists();
    }
}
