package org.corefx.callr.example.calculator.hub.host;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"org.corefx.callr.hub"
})
public class CalculatorHubHostApplication {

	public static void main(String[] args) {
		SpringApplication.run(CalculatorHubHostApplication.class, args);
	}

}
