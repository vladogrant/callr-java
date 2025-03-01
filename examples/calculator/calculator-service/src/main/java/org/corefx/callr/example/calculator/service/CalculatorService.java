package org.corefx.callr.example.calculator.service;

import org.corefx.callr.Authorized;
import org.corefx.callr.client.CallRClient;
import org.corefx.callr.client.CallRServiceBase;
import org.corefx.callr.example.calculator.Calculator;

import java.util.concurrent.CompletableFuture;

public class CalculatorService extends CallRServiceBase implements Calculator {

	public CalculatorService(CallRClient client) {
		super(client);
	}


	@Authorized(roles = {"ADDER", "DIVIDER"})
	public void ping() {
	}


	@Authorized(roles = {"ADDER", "DIVIDER"})
	public CompletableFuture<Void> pingAsync() {
		return CompletableFuture.completedFuture(null);
	}


	@Override
	@Authorized(roles = {"ADDER"})
	public int add(Integer a, Integer b) {
		return a + b;
	}


	@Override
	@Authorized(roles = {"ADDER"})
	public CompletableFuture<Integer> addAsync(Integer a, Integer b) {
		return CompletableFuture.supplyAsync(() -> add(a, b));
		// return CompletableFuture.completedFuture(add(a, b));
	}


	@Override
	@Authorized(roles = {"DIVIDER"})
	public int div(Integer a, Integer b) {
		return a / b;
	}


	@Override
	@Authorized(roles = {"DIVIDER"})
	public CompletableFuture<Integer> divAsync(Integer a, Integer b) {
		return CompletableFuture.supplyAsync(() -> div(a, b));
		// return CompletableFuture.completedFuture(div(a, b));
	}


	@Override
	@Authorized(roles = {"DIVIDER"})
	public double div(Double a, Double b) {
		return a / b;
	}


	@Override
	@Authorized(roles = {"DIVIDER"})
	public CompletableFuture<Double> divAsync(Double a, Double b) {
		return CompletableFuture.supplyAsync(() -> div(a, b));
		// return CompletableFuture.completedFuture(div(a, b));
	}

}
