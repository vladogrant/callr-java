package org.corefx.callr.example.calculator.client.host;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.corefx.callr.client.CallRClient;
import org.corefx.callr.example.calculator.Calculator;
import org.corefx.callr.example.calculator.client.CalculatorServiceProxy;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URI;
import java.util.UUID;

@AllArgsConstructor
@SpringBootApplication
public class CalculatorClientHost implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(CalculatorClientHost.class, args);
	}


	@SneakyThrows
	@Override
	public void run(String... args) {
		CallRClient client = new CallRClient(UUID.randomUUID(), new URI("ws://localhost:8080"));
		Calculator calculator = new CalculatorServiceProxy(client, UUID.fromString("00000000-0000-0000-0000-A736F2F2FAD2"));
		calculator.ping();
		int a = 2;
		int b = 3;
		int c = calculator.add(a, b);
		System.out.println(c);
		System.in.read();
	}

}
