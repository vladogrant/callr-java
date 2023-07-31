package org.corefx.callr.example.calculator.service.host;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.corefx.callr.client.CallRServiceBase;
import org.corefx.callr.example.calculator.Calculator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"org.corefx.callr.example.calculator.service",
})
@AllArgsConstructor
public class CalculatorServiceHost implements CommandLineRunner {

	Calculator service;


	public static void main(String[] args) {
		SpringApplication.run(CalculatorServiceHost.class, args);
	}


	@SneakyThrows
	@Override
	public void run(String... args) {
		((CallRServiceBase)service).start();
		System.in.read();
	}

}
