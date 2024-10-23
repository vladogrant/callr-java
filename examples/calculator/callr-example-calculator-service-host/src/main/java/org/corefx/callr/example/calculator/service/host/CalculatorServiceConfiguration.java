package org.corefx.callr.example.calculator.service.host;

import lombok.AllArgsConstructor;
import org.corefx.callr.client.CallRClient;
import org.corefx.callr.example.calculator.Calculator;
import org.corefx.callr.example.calculator.service.CalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class CalculatorServiceConfiguration {


	@Autowired
	CalculatorServiceConfigurationProperties config;


	public CalculatorServiceConfiguration() {

	}


	@Bean
	Calculator calculator(CallRClient client) {
		return new CalculatorService(client);
	}


	@Bean
	CallRClient client() {
		return new CallRClient(config);
	}
}