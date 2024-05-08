package com.blankcil.api.blankcilapi.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class FFmpegUtil {
    public static File VIDEO_FILE = null;
    public static byte[] combineMultipartFiles(MultipartFile imageFile, MultipartFile audioFile) throws IOException, InterruptedException {
        // Lấy đường dẫn tương đối đến thư mục temp mặc định
        String tempDirPath = System.getProperty("java.io.tmpdir");
        File tempDirectory = new File(tempDirPath);

        // Lưu các file tạm thời
        File tempImageFile = File.createTempFile("temp-image", ".jpg", tempDirectory);
        File tempAudioFile = File.createTempFile("temp-audio", ".mp3", tempDirectory);

        // Ghi dữ liệu từ MultipartFile vào các file tạm thời
        Files.write(tempImageFile.toPath(), imageFile.getBytes());
        Files.write(tempAudioFile.toPath(), audioFile.getBytes());

        // Đường dẫn đến file output video
        File outputVideoFile = File.createTempFile("output-video", ".mp4", tempDirectory);

        // Gọi ffmpeg để gộp ảnh và audio thành video
        ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg", "-y", "-loop", "1",
                "-i", tempImageFile.getAbsolutePath(),
                "-i", tempAudioFile.getAbsolutePath(),
                "-c:v", "libx264", "-c:a", "aac", "-strict", "experimental",
                "-b:a", "192k", "-shortest", outputVideoFile.getAbsolutePath());

        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.to(new File("FFmpeg-log.txt")));

        Process process = processBuilder.start();

        // Chờ quá trình hoàn thành
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Xóa các file tạm thời
        deleteTempFile(tempImageFile);
        deleteTempFile(tempAudioFile);

        VIDEO_FILE = outputVideoFile;
        return Files.readAllBytes(outputVideoFile.toPath());
    }

    public static void deleteTempFile(File file) {
        if (file.exists()) {
            file.delete();
        }
    }
}
