package org.corefx.callr.example.calculator.client;

import org.corefx.callr.Parameter;
import org.corefx.callr.client.CallRClient;
import org.corefx.callr.client.CallRServiceProxy;
import org.corefx.callr.example.calculator.Calculator;

import java.util.UUID;

public class CalculatorServiceProxy extends CallRServiceProxy implements Calculator {


	public CalculatorServiceProxy(CallRClient client, UUID serviceId) {
		super(serviceId, client);
	}


	public void ping() {
		invoke();
	}


	@Override
	public int add(Integer a, Integer b) {
		return (int)invoke(
				new Parameter("a", Integer.class.getName(), a),
				new Parameter("b", Integer.class.getName(), b)
		);
	}

}
