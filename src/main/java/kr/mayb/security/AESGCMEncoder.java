package kr.mayb.security;

import jakarta.annotation.PostConstruct;
import kr.mayb.error.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class AESGCMEncoder {

    private static final String AES_ALGORITHM = "AES/GCM/NoPadding";
    private static final int IV_SIZE = 12; // GCM 권장 IV 크기
    private static final int TAG_LENGTH = 128; // GCM 인증 태그 길이 (128비트)

    @Value("${encryption.secret-key}")
    private String secretKeyHex;

    private static SecretKey secretKey;

    @PostConstruct
    private void init() {
        byte[] keyBytes = hexStringToByteArray(secretKeyHex);
        secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    private byte[] generateIV() {
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    public String encrypt(String plainText) {
        try {
            byte[] iv = generateIV();
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // [ IV(12바이트) | 암호문 ] 저장
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new BadRequestException("암호화에 실패했습니다.");
        }
    }

    public String decrypt(String encryptedText) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);

            // IV & 암호문 분리
            byte[] iv = new byte[IV_SIZE];
            byte[] encryptedData = new byte[decodedBytes.length - IV_SIZE];

            System.arraycopy(decodedBytes, 0, iv, 0, IV_SIZE);
            System.arraycopy(decodedBytes, IV_SIZE, encryptedData, 0, encryptedData.length);

            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);
            byte[] decrypted = cipher.doFinal(encryptedData);

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new BadRequestException("복호화에 실패했습니다.");
        }
    }

    private byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}

