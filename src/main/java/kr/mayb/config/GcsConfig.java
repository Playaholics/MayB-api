package kr.mayb.config;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class GcsConfig {
    @Bean
    public Storage storage() {
        StorageOptions defaultInstance = StorageOptions.getDefaultInstance();
        return defaultInstance.getService();
    }
}
