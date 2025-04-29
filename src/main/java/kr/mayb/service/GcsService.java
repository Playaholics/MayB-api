package kr.mayb.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import kr.mayb.error.ExternalApiException;
import kr.mayb.util.ImgCompressUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class GcsService {

    private static final String WEBP_EXTENSION = ".webp";

    private final Storage storage;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    @Async("mayb-taskExecutor")
    public void upload(MultipartFile file, String fullBlobName) {
        BlobId blobId = BlobId.of(bucketName, fullBlobName);
        BlobInfo info = BlobInfo.newBuilder(blobId)
                .setContentType(WEBP_EXTENSION)
                .build();

        try {
            // Convert to .webp for compression
            byte[] converted = ImgCompressUtils.convertToWebp(file.getBytes());
            storage.create(info, converted);
        } catch (Exception e) {
            throw new ExternalApiException("Failed to upload file to GCS: " + e.getMessage());
        }
    }
}
