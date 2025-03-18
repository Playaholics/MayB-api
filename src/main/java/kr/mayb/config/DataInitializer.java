package kr.mayb.config;

import kr.mayb.data.model.Authority;
import kr.mayb.data.repository.AuthorityRepository;
import kr.mayb.enums.AuthorityName;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DataInitializer {

    @Profile("local")
    @Bean
    ApplicationRunner init(AuthorityRepository authorityRepository) {
        // local 테스트 시 h2 db에 초기 데이터 삽입
        return args -> {
            if (authorityRepository.count() == 0) { // 중복 삽입 방지
                authorityRepository.save(new Authority(1L, AuthorityName.ROLE_USER));
                authorityRepository.save(new Authority(2L, AuthorityName.ROLE_ADMIN));
            }
        };
    }
}
