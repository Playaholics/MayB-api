package kr.mayb.service;

import kr.mayb.enums.GcsFolderPath;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private static final String WEBP_EXTENSION = ".webp";

    private final GcsService gcsService;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    public String upload(MultipartFile file, GcsFolderPath type) {
        String fileName = file.getName();
        String uuidName = generateUniqueFileName(fileName + WEBP_EXTENSION);
        String fullBlobName = GcsFolderPath.getValue(type) + uuidName;

        // Save in GCS bucket async
        gcsService.upload(file, fullBlobName);

        return String.format("https://storage.googleapis.com/%s/%s", bucketName, fullBlobName);
    }

    private String generateUniqueFileName(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        return uuid + "-" + originalFilename;
    }
}
