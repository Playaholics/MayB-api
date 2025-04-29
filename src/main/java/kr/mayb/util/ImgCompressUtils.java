package kr.mayb.util;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;
import kr.mayb.error.BadRequestException;
import kr.mayb.error.ImageConversionException;

import java.io.IOException;

public class ImgCompressUtils {

    public static byte[] convertToWebp(byte[] imageBytes) {
        try {
            return ImmutableImage.loader()
                    .fromBytes(imageBytes)
                    .bytes(WebpWriter.DEFAULT);
        } catch (IOException e) {
            throw new ImageConversionException("Fail to compress img: " + e.getMessage());
        } catch (Exception e) {
            throw new BadRequestException("Fail to compress img");
        }
    }

    public static byte[] convertToWebpLossless(byte[] imageBytes) {
        try {
            return ImmutableImage.loader()
                    .fromBytes(imageBytes)
                    .bytes(WebpWriter.DEFAULT.withLossless());
        } catch (IOException e) {
            throw new ImageConversionException("Fail to compress img: " + e.getMessage());
        } catch (Exception e) {
            throw new BadRequestException("Fail to compress img");
        }
    }
}
