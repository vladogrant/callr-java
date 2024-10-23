package org.corefx.callr.client.test;

import lombok.Getter;
import lombok.Setter;
import org.corefx.callr.configuration.ClientConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "callr.test.client")
public class CallRClientTestConfigurationProperties extends ClientConfigurationProperties {

}
