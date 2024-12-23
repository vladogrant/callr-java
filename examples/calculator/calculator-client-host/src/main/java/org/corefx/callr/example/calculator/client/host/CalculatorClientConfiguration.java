package org.corefx.callr.example.calculator.client.host;

import org.corefx.callr.client.CallRClient;
import org.corefx.callr.example.calculator.Calculator;
import org.corefx.callr.example.calculator.client.CalculatorServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CalculatorClientConfiguration {


	@Autowired
	CalculatorClientConfigurationProperties config;


	public CalculatorClientConfiguration() {

	}


	@Bean
	Calculator calculator() {
		return new CalculatorServiceProxy(client(), config.getServiceId());
	}


	@Bean
	CallRClient client() {
		return new CallRClient(config);
	}
}
