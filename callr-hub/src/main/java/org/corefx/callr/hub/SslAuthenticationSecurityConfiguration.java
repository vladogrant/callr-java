package org.corefx.callr.hub;

import lombok.AllArgsConstructor;
import org.corefx.callr.configuration.GlobalConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@ConditionalOnProperty(name = "callr.authentication.type", havingValue = "ssl")
@EnableWebSecurity
@EnableConfigurationProperties(GlobalConfigurationProperties.class)
@AllArgsConstructor
public class SslAuthenticationSecurityConfiguration {

	private final UserDetailsService userDetailsService;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				.authorizeHttpRequests(customizer -> customizer.anyRequest().authenticated())
				.x509(config -> config.subjectPrincipalRegex("CN=(.*?)(?:,|$)"))
				.userDetailsService(userDetailsService);
		;
		return httpSecurity.build();
	}


}
