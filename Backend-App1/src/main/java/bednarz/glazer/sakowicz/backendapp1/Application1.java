package bednarz.glazer.sakowicz.backendapp1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(scanBasePackages = "bednarz.glazer.sakowicz")
public class Application1 {

	public static void main(String[] args) {
		SpringApplication.run(Application1.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
