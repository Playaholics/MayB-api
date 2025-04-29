package kr.mayb.service;

import kr.mayb.enums.GcsPath;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private static final String WEBP_EXTENSION = ".webp";
    public static final String GCS_IMAGE_URL_PREFIX = "https://storage.googleapis.com/%s/%s";

    private final GcsService gcsService;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    public String upload(MultipartFile file, GcsPath type) {
        String uuidName = generateUniqueFileName();
        String fullBlobName = GcsPath.getValue(type) + uuidName;

        // Save in GCS bucket async
        gcsService.upload(file, fullBlobName);

        return String.format(GCS_IMAGE_URL_PREFIX, bucketName, fullBlobName);
    }

    private String generateUniqueFileName() {
        return new StringBuilder()
                .append(UUID.randomUUID())
                .append("-")
                .append(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                .append(WEBP_EXTENSION)
                .toString();
    }
}
