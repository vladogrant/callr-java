package org.corefx.callr.example.calculator.service;

import org.corefx.callr.client.CallRClient;
import org.corefx.callr.client.CallRServiceBase;
import org.corefx.callr.example.calculator.Calculator;

public class CalculatorService extends CallRServiceBase implements Calculator {

	public CalculatorService(CallRClient client) {
		super(client);
	}


	public void ping() {

	}


	@Override
	public int add(Integer a, Integer b) {
		return a + b;
	}


	@Override
	public int div(Integer a, Integer b) {
		return a / b;
	}

}
