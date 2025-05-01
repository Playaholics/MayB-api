package kr.mayb.service;

import kr.mayb.enums.GcsBucketPath;
import kr.mayb.error.BadRequestException;
import kr.mayb.util.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private static final String WEBP_EXTENSION = ".webp";

    private final GcsService gcsService;

    public String upload(MultipartFile file, GcsBucketPath pathType) {
        ImageUtils.validateImageFile(file);

        String uuidName = generateUniqueFileName();
        String fullBlobName = GcsBucketPath.getPath(pathType) + uuidName;

        try {
            // Convert to .webp for compression
            byte[] converted = ImageUtils.convertToWebp(file.getBytes());

            return gcsService.upload(converted, fullBlobName);
        } catch (Exception e) {
            throw new BadRequestException("Failed to upload image: " + file.getOriginalFilename());
        }
    }

    private String generateUniqueFileName() {
        return new StringBuilder()
                .append(UUID.randomUUID())
                .append("-")
                .append(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                .append(WEBP_EXTENSION)
                .toString();
    }

    public void delete(String profileUrl, GcsBucketPath pathType) {
        String uuidName = profileUrl.substring(profileUrl.lastIndexOf("/") + 1);
        String fullBlobName = GcsBucketPath.getPath(pathType) + uuidName;

        gcsService.delete(fullBlobName);
    }
}
