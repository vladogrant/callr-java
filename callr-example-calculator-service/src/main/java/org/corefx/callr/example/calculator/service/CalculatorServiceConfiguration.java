package org.corefx.callr.example.calculator.service;

import org.corefx.callr.client.CallRClient;
import org.corefx.callr.example.calculator.Calculator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.UUID;

@Configuration
public class CalculatorServiceConfiguration {

	@Value("${callr.uri}")
	URI uri;
	@Value("${callr.example.calculator.service.id}")
	UUID id;


	public CalculatorServiceConfiguration() {

	}


	@Bean
	Calculator calculator() {
		return new CalculatorService(new CallRClient(id, uri));
	}
}
