package org.corefx.callr.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "authentication")
public class AuthenticationConfigurationProperties {

	String type;

	@NestedConfigurationProperty
	BasicAuthenticationConfigurationProperties basic;

	@NestedConfigurationProperty
	KeyAuthenticationConfigurationProperties key;

	@NestedConfigurationProperty
	SslAuthenticationConfigurationProperties ssl;
}
