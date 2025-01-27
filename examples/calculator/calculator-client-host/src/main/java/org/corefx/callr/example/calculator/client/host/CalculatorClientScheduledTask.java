package org.corefx.callr.example.calculator.client.host;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.corefx.callr.example.calculator.Calculator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Slf4j
@AllArgsConstructor
@EnableConfigurationProperties
@EnableScheduling
@Component
public class CalculatorClientScheduledTask {

	Calculator calculatorBean;


	@Scheduled(fixedRate = Long.MAX_VALUE)
	public void run() throws Exception {
		try(Calculator calculator = calculatorBean) {
			calculator.ping();
			int a = 1;
			int b = 2;
			int c = calculator.add(a, b);
			System.out.println(MessageFormat.format("{0} + {1} = {2}{3}", a, b, c, System.lineSeparator()));

			a = 10;
			b = 5;
			c = calculator.div(a, b);
			System.out.println(MessageFormat.format("{0} / {1} = {2}{3}", a, b, c, System.lineSeparator()));

			double da = 6.66;
			double db = 3.33;
			double dc = calculator.div(da, db);
			System.out.println(MessageFormat.format("{0} / {1} = {2}{3}", da, db, dc, System.lineSeparator()));

			db = 0.00;
			dc = calculator.div(da, db);
			System.out.println(MessageFormat.format("{0} / {1} = {2}{3}", da, db, dc, System.lineSeparator()));

			b = 0;
			try {
				c = calculator.div(a, b);
				System.out.println(c);
			}
			catch(Exception ex) {
				log.error(ex.getMessage(), ex);
			}
			System.out.println(MessageFormat.format("{0} / {1} => java.lang.ArithmeticException: / by zero", a, b));
		}

	}

}
