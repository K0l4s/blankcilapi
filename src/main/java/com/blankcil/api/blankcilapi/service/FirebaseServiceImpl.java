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
    private static final String MAIN_FOLDER = "main/";
    private static final String PODCAST_FOLDER = "podcasts/";
    private static final String VIDEOS_FOLDER = "videos/";
    private static final String THUMBNAIL_FOLDER = "thumbnail/";
    private static final String INFO_FOLDER = "info/";
    private static final String AVATAR_FOLDER = "avatar/";
    private static final String COVER_FOLDER = "cover/";

    @Value("${firebase.storage.bucket}")
    private String bucketName;

    @Autowired
    private Storage firebaseStorage;

    @Autowired
    private UserRepository userRepository;

    @Override
    public String uploadVideoToFirebase(byte[] videoBytes) throws IOException {
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
        String folderName = MAIN_FOLDER + userId + "-" + userEmail + "/" + PODCAST_FOLDER + VIDEOS_FOLDER;
        BlobId blobId = BlobId.of(bucketName, folderName + fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("video/mp4").build();
        Blob blob = storage.create(blobInfo, videoBytes);

        FFmpegUtil.deleteTempFile(FFmpegUtil.VIDEO_FILE);
        return  blob.getMediaLink();
    }

    @Override
    public String uploadImageToFirebase(MultipartFile imageFile, String type) throws IOException {
        System.out.println(imageFile);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        String userId = userEntity.getId().toString();

        Storage storage = firebaseStorage;

        // Get file extension
        String originalFilename = imageFile.getOriginalFilename();
        assert originalFilename != null;
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);

        // Generate unique image name with original file extension
        String imageName = UUID.randomUUID() + "." + fileExtension;

        String folderName = switch (type) {
            case "thumbnail" -> MAIN_FOLDER + userId + "-" + userEmail + "/" + PODCAST_FOLDER + THUMBNAIL_FOLDER;
            case "avatar" -> MAIN_FOLDER + userId + "-" + userEmail + "/" + INFO_FOLDER + AVATAR_FOLDER;
            case "cover" -> MAIN_FOLDER + userId + "-" + userEmail + "/" + INFO_FOLDER + COVER_FOLDER;
            default -> null;
        };

        // Upload image to bucket
        BlobId blobId = BlobId.of(bucketName, folderName + imageName);

        try (InputStream inputStream = imageFile.getInputStream()) {
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(imageFile.getContentType()).build();
            Blob blob = storage.create(blobInfo, inputStream.readAllBytes());
            return blob.getMediaLink();
        }
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
        String folderName = MAIN_FOLDER + userId + "-" + userName + "/";
//        BlobId folderBlobId = BlobId.of(bucketName, folderName);
        Storage storage = firebaseStorage;

        // Kiểm tra xem thư mục đã tồn tại chưa
        if (!isFolderExists(storage, folderName)) {
            createFolder(storage, null, folderName);

            // Tạo thư mục Podcast
            createFolder(storage, folderName, PODCAST_FOLDER);

            // Tạo thư mục Podcast/videos
            createFolder(storage, folderName + PODCAST_FOLDER, VIDEOS_FOLDER);

            // Tạo thư mục Podcast/thumbnail
            createFolder(storage, folderName + PODCAST_FOLDER, THUMBNAIL_FOLDER);

            // Tạo thư mục Info
            createFolder(storage, folderName, INFO_FOLDER);

            // Tạo thư mục Info/avatar
            createFolder(storage, folderName + INFO_FOLDER, AVATAR_FOLDER);

            // Tạo thư mục Info/cover
            createFolder(storage, folderName + INFO_FOLDER, COVER_FOLDER);
        }

    }


    // Hàm kiểm tra xem thư mục đã tồn tại hay chưa
    private boolean isFolderExists(Storage storage, String folderName) {
        BlobId blobId = BlobId.of(bucketName, folderName);
        Blob blob = storage.get(blobId);
        return blob != null;
    }

    // Hàm tạo thư mục mới
    private void createFolder(Storage storage, String parentFolder, String folderName) {
        String fullPath = parentFolder + folderName + "/";
        BlobId blobId = BlobId.of(bucketName, fullPath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("application/x-directory").build();
        storage.create(blobInfo);
    }

    @Override
    public void deleteFileFromFirebase(String filePath) {
        if (filePath == null) {
            return;
        }
        Storage storage = firebaseStorage;
        BlobId blobId = BlobId.of(bucketName, filePath);
        storage.delete(blobId);
    }

}
