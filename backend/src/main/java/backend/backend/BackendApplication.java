package backend.backend;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
@EnableScheduling
@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		
		
		SpringApplication.run(BackendApplication.class, args);
		
	}
	
}
