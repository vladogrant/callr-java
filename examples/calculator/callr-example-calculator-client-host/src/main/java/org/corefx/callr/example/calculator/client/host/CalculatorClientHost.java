package org.corefx.callr.example.calculator.client.host;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.corefx.callr.example.calculator.Calculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.text.MessageFormat;

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

		calculator.ping();

		int a = 1;
		int b = 2;
		int c = calculator.add(a, b);
		System.out.println(MessageFormat.format("{0} + {1} = {2}", a, b, c));

		a = 10;
		b = 5;
		c = calculator.div(a, b);
		System.out.println(MessageFormat.format("{0} / {1} = {2}", a, b, c));

		double da = 6.66;
		double db = 3.33;
		double dc = calculator.div(da, db);
		System.out.println(MessageFormat.format("{0} / {1} = {2}", da, db, dc));

		db = 0.00;
		dc = calculator.div(da, db);
		System.out.println(MessageFormat.format("{0} / {1} = {2}", da, db, dc));

		b = 0;
		try {
			c = calculator.div(a, b);
			System.out.println(c);
		}
		catch(Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		System.out.println(MessageFormat.format("{0} / {1} => java.lang.ArithmeticException: / by zero", a, b));
	}

}
