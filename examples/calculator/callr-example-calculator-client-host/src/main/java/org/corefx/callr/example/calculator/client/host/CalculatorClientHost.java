package org.corefx.callr.example.calculator.client.host;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.corefx.callr.example.calculator.Calculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@AllArgsConstructor
@Slf4j
@SpringBootApplication
public class CalculatorClientHost implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(CalculatorClientHost.class, args);
	}

	@Autowired
	Calculator calculator;


	@SneakyThrows
	@Override
	public void run(String... args) {
		// calculator.ping();
		int a = 1;
		int b = 0;
		try {
			double c = calculator.div(a, b);
			System.out.println(c);
		}
		catch(Exception ex) {
			log.error(ex.getMessage(), ex);
		}
	}

}
