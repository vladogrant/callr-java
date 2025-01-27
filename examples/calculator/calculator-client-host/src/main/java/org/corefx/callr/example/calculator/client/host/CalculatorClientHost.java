package org.corefx.callr.example.calculator.client.host;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@AllArgsConstructor
@Slf4j
@SpringBootApplication
public class CalculatorClientHost {

	public static void main(String[] args) {
		SpringApplication.run(CalculatorClientHost.class, args);
	}


}
