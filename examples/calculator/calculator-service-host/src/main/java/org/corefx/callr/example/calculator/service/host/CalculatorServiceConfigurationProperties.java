package org.corefx.callr.example.calculator.service.host;

import lombok.Getter;
import lombok.Setter;
import org.corefx.callr.configuration.ClientConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "callr.example.calculator.service")
public class CalculatorServiceConfigurationProperties extends ClientConfigurationProperties {

}
