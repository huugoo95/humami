package com.hugo.humami.service.impl;

import com.hugo.humami.service.S3Service;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Iterator;
import java.util.UUID;

@Service
public class S3ServiceImpl implements S3Service {

    private static final int TARGET_WIDTH = 1600;
    private static final int TARGET_HEIGHT = 900;
    private static final float JPEG_QUALITY = 0.80f;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucketName = "humami";
    private final int expirationImageUrlInHours = 1;

    public S3ServiceImpl(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    @Override
    public String getTempUrl(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(expirationImageUrlInHours))
                .getObjectRequest(getObjectRequest)
                .build();

        URL presignedUrl = s3Presigner.presignGetObject(presignRequest).url();
        return presignedUrl.toString();
    }

    @Override
    public String uploadImage(MultipartFile image, String name) throws IOException {
        String key = generateImageKey(name);
        byte[] normalizedImage = normalizeTo16x9Jpeg(image);

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType(MediaType.IMAGE_JPEG_VALUE)
                        .build(),
                RequestBody.fromInputStream(new ByteArrayInputStream(normalizedImage), normalizedImage.length)
        );
        return key;
    }

    @Override
    public void deleteImage(String key) {
        if (key == null || key.isBlank()) {
            return;
        }
        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build()
        );
    }

    private byte[] normalizeTo16x9Jpeg(MultipartFile image) throws IOException {
        BufferedImage source = ImageIO.read(image.getInputStream());
        if (source == null) {
            throw new IOException("Invalid image format");
        }

        BufferedImage cropped = cropCenterToAspect(source, 16.0 / 9.0);
        BufferedImage resized = resize(cropped, TARGET_WIDTH, TARGET_HEIGHT);
        return toJpegBytes(resized, JPEG_QUALITY);
    }

    private BufferedImage cropCenterToAspect(BufferedImage source, double targetAspect) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();
        double sourceAspect = (double) sourceWidth / sourceHeight;

        int cropWidth = sourceWidth;
        int cropHeight = sourceHeight;

        if (sourceAspect > targetAspect) {
            cropWidth = (int) Math.round(sourceHeight * targetAspect);
        } else if (sourceAspect < targetAspect) {
            cropHeight = (int) Math.round(sourceWidth / targetAspect);
        }

        int x = Math.max((sourceWidth - cropWidth) / 2, 0);
        int y = Math.max((sourceHeight - cropHeight) / 2, 0);

        return source.getSubimage(x, y, cropWidth, cropHeight);
    }

    private BufferedImage resize(BufferedImage source, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(source, 0, 0, width, height, null);
        g2d.dispose();
        return resized;
    }

    private byte[] toJpegBytes(BufferedImage image, float quality) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");

        if (!writers.hasNext()) {
            throw new IOException("No JPEG writer available");
        }

        ImageWriter writer = writers.next();
        ImageWriteParam writeParam = writer.getDefaultWriteParam();
        writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        writeParam.setCompressionQuality(quality);

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(image, null, null), writeParam);
        } finally {
            writer.dispose();
        }

        return baos.toByteArray();
    }

    private String generateImageKey(String name) {
        return name.replace(" ", "_") + "_" + UUID.randomUUID() + ".jpg";
    }
}
