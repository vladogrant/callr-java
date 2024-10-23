package org.corefx.callr.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.UUID;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "client")
public class ClientConfigurationProperties {

	private UUID id;

	private URI uri;

	@NestedConfigurationProperty
	ClientSslConfigurationProperties ssl;

	@NestedConfigurationProperty
	AuthenticationConfigurationProperties authentication;

}
