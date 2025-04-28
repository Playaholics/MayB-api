package kr.mayb.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import kr.mayb.enums.GcsFolderPath;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GcsService {

    private final Storage storage;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile file, GcsFolderPath type) {
        try {
            String originalFilename = file.getOriginalFilename();
            String contentType = file.getContentType();

            String blobName = generateUniqueFileName(originalFilename);
            String fullBlobName = GcsFolderPath.getValue(type) + blobName;

            BlobId blobId = BlobId.of(bucketName, fullBlobName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(contentType)
                    .build();

            storage.create(blobInfo, file.getBytes());

            return String.format("https://storage.googleapis.com/%s/%s", bucketName, fullBlobName);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to upload file to GCS: " + e.getMessage());
        }
    }

    private String generateUniqueFileName(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        return uuid + "-" + originalFilename;
    }
}
