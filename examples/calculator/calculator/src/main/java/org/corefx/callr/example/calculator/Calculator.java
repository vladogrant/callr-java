package org.corefx.callr.example.calculator;

public interface Calculator extends AutoCloseable {
	void ping();

	int add(Integer a, Integer b);

	int div(Integer a, Integer b);

	double div(Double a, Double b);
}