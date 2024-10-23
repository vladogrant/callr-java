package org.corefx.callr.hub;

import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class ContainerConfiguration implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {


	@Override
	public void customize(ConfigurableServletWebServerFactory factory) {

	}
}
