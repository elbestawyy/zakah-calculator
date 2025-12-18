package ntg.project.ZakahCalculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ZakahCalculatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZakahCalculatorApplication.class, args);
	}

}
