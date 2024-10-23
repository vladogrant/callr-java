package org.corefx.callr.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "ssl")
public class ClientSslConfigurationProperties {

	@NestedConfigurationProperty
	TrustStoreConfigurationProperties trustStore;
}