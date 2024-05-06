package com.blankcil.api.blankcilapi.utils;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ImageProcessing {
    public static MultipartFile resizeImageTo9by16(MultipartFile imageFile) throws Exception {
        BufferedImage originalImage = ImageIO.read(imageFile.getInputStream());
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // Tính toán chiều cao mới dựa trên tỷ lệ của ảnh gốc và chiều rộng mới là 720
        int newWidth = 720;
        int newHeight = (int) Math.ceil((double) height / width * newWidth);

        if (newHeight % 2 != 0) {
            newHeight++;
        }
        System.out.println(newHeight);

        // Tạo ảnh mới với kích thước mới
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();

        // Vẽ ảnh gốc lên ảnh mới với kích thước mới
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();

        // Chuyển ảnh mới sang định dạng byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", outputStream);
        byte[] resizedImageBytes = outputStream.toByteArray();

        // Tạo ByteArrayInputStream từ byte array
        ByteArrayInputStream inputStream = new ByteArrayInputStream(resizedImageBytes);

        // Tạo và trả về MultipartFile mới
        return new MockMultipartFile("resizedImage.jpg", inputStream);
    }
}
