package org.corefx.callr.example.calculator.client.host;

import lombok.Getter;
import lombok.Setter;
import org.corefx.callr.configuration.ClientConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "callr.example.calculator.client")
public class CalculatorClientConfigurationProperties extends ClientConfigurationProperties {

	UUID serviceId;
}
