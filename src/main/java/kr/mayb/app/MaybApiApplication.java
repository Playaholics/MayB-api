package kr.mayb.app;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import java.time.ZoneOffset;
import java.util.TimeZone;

@SpringBootApplication
public class MaybApiApplication {

	@Bean
	public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
		PropertySourcesPlaceholderConfigurer propsConfig = new PropertySourcesPlaceholderConfigurer();
		propsConfig.setLocation(new ClassPathResource("git.properties"));
		propsConfig.setIgnoreResourceNotFound(true);
		propsConfig.setIgnoreUnresolvablePlaceholders(true);
		return propsConfig;
	}

	@PostConstruct
	void started() {
		TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));
	}

	public static void main(String[] args) {
		SpringApplication.run(MaybApiApplication.class, args);
	}
}
