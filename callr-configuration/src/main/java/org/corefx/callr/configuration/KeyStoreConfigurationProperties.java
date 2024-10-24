package org.corefx.callr.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "key-store")
public class KeyStoreConfigurationProperties {

	private Resource file;

	private String password;



}
