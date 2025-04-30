package kr.mayb.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class GcsService {

    private static final String WEBP_EXTENSION = ".webp";
    private static final String GCS_HOST_URL = "https://storage.googleapis.com";

    private final Storage storage;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    public String upload(byte[] file, String fullBlobName) {
        BlobId blobId = BlobId.of(bucketName, fullBlobName);
        BlobInfo info = BlobInfo.newBuilder(blobId)
                .setContentType(WEBP_EXTENSION)
                .build();

        Blob uploaded = storage.create(info, file);

        return UriComponentsBuilder
                .fromUriString(GCS_HOST_URL)
                .pathSegment(uploaded.getBucket(), uploaded.getName())
                .toUriString();
    }
}
