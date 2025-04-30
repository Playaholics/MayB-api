package kr.mayb.service;

import kr.mayb.enums.GcsPath;
import kr.mayb.error.BadRequestException;
import kr.mayb.util.ImgCompressUtils;
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

    public String upload(MultipartFile file, GcsPath type) {
        String uuidName = generateUniqueFileName();
        String fullBlobName = GcsPath.getValue(type) + uuidName;

        try {
            // Convert to .webp for compression
            byte[] converted = ImgCompressUtils.convertToWebp(file.getBytes());
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
}
