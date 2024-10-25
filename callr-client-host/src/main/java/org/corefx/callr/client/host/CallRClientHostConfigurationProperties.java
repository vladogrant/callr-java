package org.corefx.callr.client.host;

import lombok.Getter;
import lombok.Setter;
import org.corefx.callr.configuration.ClientConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "callr.client")
public class CallRClientHostConfigurationProperties extends ClientConfigurationProperties {

}
