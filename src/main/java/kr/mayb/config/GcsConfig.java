package kr.mayb.config;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GcsConfig {

    @Bean
    public Storage storage() {

        StorageOptions defaultInstance = StorageOptions.getDefaultInstance();
        System.out.printf("gcp_key: " + defaultInstance.getCredentials());
        return defaultInstance.getService();
    }
}
