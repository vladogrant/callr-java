package org.corefx.callr.example.calculator.client.host;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.corefx.callr.example.calculator.Calculator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@AllArgsConstructor
@EnableConfigurationProperties
@EnableScheduling
@Component
public class CalculatorClientScheduledTask {

	Calculator calculator;


	@Scheduled(fixedRate = Long.MAX_VALUE)
	public void run() {

/*
		calculator.ping();
		add(1, 2);
		div(10, 5);
		div(6.66, 3.33);
		div(6.66, 0.00d);
		div(10, 0);
*/

		CompletableFuture<Void> pingFuture = calculator.pingAsync();
		pingFuture.thenAcceptAsync(_unused -> {
			log.info("Ping completed.");
		});
		asyncAdd(1, 2);
		asyncDiv(10, 5);
		asyncDiv(6.66, 3.33);
		asyncDiv(6.66, 0.00);
		asyncDiv(10, 0);
	}


	private void add(int a, int b) {
		int c = calculator.add(a, b);
		log.info("{} + {} = {}", a, b, c);
	}


	private void asyncAdd(int a, int b) {
		CompletableFuture<Integer> future = calculator.addAsync(a, b);
		future.thenAcceptAsync(c -> {
			log.info("{} + {} = {} (async)", a, b, c);
		});
	}


	private void div(int a, int b) {
		int c = calculator.div(a, b);
		log.info("{} / {} = {}", a, b, c);
	}


	private void asyncDiv(int a, int b) {
		CompletableFuture<Integer> future = calculator.divAsync(a, b);
/*
		future.thenAcceptAsync(c -> {
			log.info("{} / {} = {} (async)", a, b, c);
		});
*/
		future.handleAsync((c, ex) -> {
			if(c != null) {
				log.info("{} / {} = {} (async)", a, b, c);
			}
			if(ex != null) {
/*
				if(ex instanceof CompletionException) {
					ex = ex.getCause();
				}
*/
				log.error(ex.getMessage(), ex);
				log.info("{} / {} => {} (async)", a, b, ex.getMessage());
			}
			return null;
		});
	}


	private void div(double a, double b) {
		try {
			double c = calculator.div(a, b);
			log.info("{} / {} = {}", a, b, c);
		}
		catch(Exception ex) {
			log.error(ex.getMessage(), ex);
			log.info("{} / {} => java.lang.ArithmeticException: / by zero", a, b);
		}
	}


	private void asyncDiv(double a, double b) {
		CompletableFuture<Double> future = calculator.divAsync(a, b);
		future.thenAcceptAsync(c -> {
			log.info("{} / {} = {} (async)", a, b, c);
		});
	}
}
