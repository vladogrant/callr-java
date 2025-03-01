package org.corefx.callr.example.calculator;

import java.util.concurrent.CompletableFuture;

public interface Calculator extends AutoCloseable {
	void ping();
	CompletableFuture<Void> pingAsync();

	int add(Integer a, Integer b);
	CompletableFuture<Integer> addAsync(Integer a, Integer b);

	int div(Integer a, Integer b);
	CompletableFuture<Integer> divAsync(Integer a, Integer b);

	double div(Double a, Double b);
	CompletableFuture<Double> divAsync(Double a, Double b);
}
