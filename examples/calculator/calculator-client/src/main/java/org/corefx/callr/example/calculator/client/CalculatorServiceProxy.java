package org.corefx.callr.example.calculator.client;

import org.corefx.callr.Parameter;
import org.corefx.callr.client.CallRClient;
import org.corefx.callr.client.CallRServiceProxy;
import org.corefx.callr.example.calculator.Calculator;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CalculatorServiceProxy extends CallRServiceProxy implements Calculator {


	public CalculatorServiceProxy(CallRClient client, UUID serviceId) {
		super(serviceId, client);
	}


	public void ping() {
		invoke();
	}


	public CompletableFuture<Void> pingAsync() {
		return invokeAsync().thenAcceptAsync(o -> {
		});
	}


	@Override
	public int add(Integer a, Integer b) {
		return (int) invoke(
				new Parameter("a", Integer.class.getName(), a),
				new Parameter("b", Integer.class.getName(), b)
		);
	}


	@Override
	public CompletableFuture<Integer> addAsync(Integer a, Integer b) {
		return invokeAsync(
				new Parameter("a", Integer.class.getName(), a),
				new Parameter("b", Integer.class.getName(), b)
		).thenApply(o -> (Integer) o);
	}


	@Override
	public int div(Integer a, Integer b) {
		return (int) invoke(
				new Parameter("a", Integer.class.getName(), a),
				new Parameter("b", Integer.class.getName(), b)
		);
	}


	@Override
	public CompletableFuture<Integer> divAsync(Integer a, Integer b) {
		return invokeAsync(
				new Parameter("a", Integer.class.getName(), a),
				new Parameter("b", Integer.class.getName(), b)
		).thenApply(o -> (Integer) o);
	}


	@Override
	public double div(Double a, Double b) {
		return (double) invoke(
				new Parameter("a", Double.class.getName(), a),
				new Parameter("b", Double.class.getName(), b)
		);
	}


	@Override
	public CompletableFuture<Double> divAsync(Double a, Double b) {
		return invokeAsync(
				new Parameter("a", Double.class.getName(), a),
				new Parameter("b", Double.class.getName(), b)
		).thenApply(o -> (Double) o);
	}


}
