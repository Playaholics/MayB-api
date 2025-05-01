package kr.mayb.util;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImgCompressUtilsTest {

    @Test
    void convertToWebpTest() throws IOException, URISyntaxException {
        // given
        Path path = Path.of(getClass().getClassLoader().getResource("test-image.jpg").toURI());
        File testFile = path.toFile();

        assertTrue(testFile.exists(), "파일이 존재해야함.");

        byte[] bytes = Files.readAllBytes(path);

        // when
        byte[] converted = ImageUtils.convertToWebp(bytes);

        // then
        double originalFileSizeKB = testFile.length() / 1024.0;
        double convertedFileSizeKB = converted.length / 1024.0;
        double compressionRatio = (convertedFileSizeKB / originalFileSizeKB) * 100;
        double compressionRate = 100 - compressionRatio;

        System.out.printf("Original: %.2f KB, Converted: %.2f KB, Compression: %.2f%%%n",
                originalFileSizeKB, convertedFileSizeKB, compressionRate);
    }

    @Test
    void convertToWebpWithLosslessTest() throws IOException, URISyntaxException {
        // given
        Path path = Path.of(getClass().getClassLoader().getResource("test-image.jpg").toURI());
        File testFile = path.toFile();

        assertTrue(testFile.exists(), "파일이 존재해야함.");

        byte[] bytes = Files.readAllBytes(path);

        // when
        byte[] converted = ImageUtils.convertToWebpLossless(bytes);

        // then
        double originalFileSizeKB = testFile.length() / 1024.0;
        double convertedFileSizeKB = converted.length / 1024.0;
        double compressionRatio = (convertedFileSizeKB / originalFileSizeKB) * 100;
        double compressionRate = 100 - compressionRatio;

        System.out.printf("Original: %.2f KB, Converted: %.2f KB, Compression: %.2f%%%n",
                originalFileSizeKB, convertedFileSizeKB, compressionRate);
    }
}
