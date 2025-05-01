package kr.mayb.util;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;
import kr.mayb.error.BadRequestException;
import kr.mayb.error.ImageConversionException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class ImageUtils {

    public static byte[] convertToWebp(byte[] imageBytes) {
        try {
            return ImmutableImage.loader()
                    .fromBytes(imageBytes)
                    .bytes(WebpWriter.DEFAULT);
        } catch (IOException e) {
            throw new ImageConversionException("Fail to convert to webp: " + e.getMessage());
        } catch (Exception e) {
            throw new BadRequestException("Fail to convert to webp");
        }
    }

    public static byte[] convertToWebpLossless(byte[] imageBytes) {
        try {
            return ImmutableImage.loader()
                    .fromBytes(imageBytes)
                    .bytes(WebpWriter.DEFAULT.withLossless());
        } catch (IOException e) {
            throw new ImageConversionException("Fail to convert to webp: " + e.getMessage());
        } catch (Exception e) {
            throw new BadRequestException("Fail to convert to webp");
        }
    }

    public static void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        String contentType = Optional.ofNullable(file.getContentType())
                .orElseThrow(() -> new BadRequestException("Content type is null"));

        ImageType.validateContentType(contentType);
    }

    @Getter
    @RequiredArgsConstructor
    private enum ImageType {
        JPEG("image/jpeg"),
        PNG("image/png"),
        WEBP("image/webp");

        private final String contentType;

        public static void validateContentType(String contentType) {
            boolean isValid = Arrays.stream(values())
                    .anyMatch(type -> type.getContentType().equalsIgnoreCase(contentType));

            if (!isValid) {
                throw new BadRequestException("Not supported imageType(only jpeg, png, webp can be uploaded): " + contentType);
            }
        }
    }
}
