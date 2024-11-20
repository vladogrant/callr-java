package org.corefx.callr.example.calculator.service;

import org.corefx.callr.Authorized;
import org.corefx.callr.client.CallRClient;
import org.corefx.callr.client.CallRServiceBase;
import org.corefx.callr.example.calculator.Calculator;

public class CalculatorService extends CallRServiceBase implements Calculator {

	public CalculatorService(CallRClient client) {
		super(client);
	}


	@Authorized(roles = {"ADDER", "DIVIDER"})
	public void ping() {
	}


	@Override
	@Authorized(roles =  {"ADDER"})
	public int add(Integer a, Integer b) {
		return a + b;
	}


	@Override
	@Authorized(roles = {"DIVIDER"})
	public int div(Integer a, Integer b) {
		return a / b;
	}


	@Override
	@Authorized(roles = {"DIVIDER"})
	public double div(Double a, Double b) {
		return a / b;
	}

}
