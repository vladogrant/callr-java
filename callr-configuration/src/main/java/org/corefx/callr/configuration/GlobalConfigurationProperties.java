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
@ConfigurationProperties(prefix = "callr")
public class GlobalConfigurationProperties {

	private URI uri;

	@NestedConfigurationProperty
	AuthenticationConfigurationProperties authentication;

	@NestedConfigurationProperty
	AuthorizationConfigurationProperties authorization;

}
