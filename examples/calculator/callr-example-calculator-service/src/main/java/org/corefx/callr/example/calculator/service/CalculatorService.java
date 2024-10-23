package org.corefx.callr.example.calculator.service;

import org.corefx.callr.client.AuthorizedRoles;
import org.corefx.callr.client.CallRClient;
import org.corefx.callr.client.CallRServiceBase;
import org.corefx.callr.example.calculator.Calculator;

public class CalculatorService extends CallRServiceBase implements Calculator {

	public CalculatorService(CallRClient client) {
		super(client);
	}


	@AuthorizedRoles( {"adder", "divider"})
	public void ping() {

	}


	@Override
	@AuthorizedRoles( {"adder"})
	public int add(Integer a, Integer b) {
		return a + b;
	}


	@Override
	@AuthorizedRoles( {"divider"})
	public int div(Integer a, Integer b) {
		return a / b;
	}


	@Override
	@AuthorizedRoles( {"divider"})
	public double div(Double a, Double b) {
		return a / b;
	}

}
