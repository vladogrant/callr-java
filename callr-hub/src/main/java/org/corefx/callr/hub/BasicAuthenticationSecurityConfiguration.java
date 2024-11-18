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
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@ConditionalOnProperty(name = "callr.authentication.type", havingValue = "basic")
@EnableWebSecurity
@EnableConfigurationProperties(GlobalConfigurationProperties.class)
@AllArgsConstructor
public class BasicAuthenticationSecurityConfiguration {

	private final RestAuthenticationEntryPoint authenticationEntryPoint;

	private final UserDetailsService userDetailsService;


	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				.authorizeHttpRequests(customizer -> customizer.anyRequest().authenticated())
				.httpBasic(config -> config.authenticationEntryPoint(authenticationEntryPoint))
				.addFilterAfter((request, response, chain) -> chain.doFilter(request, response), BasicAuthenticationFilter.class)
				.userDetailsService(userDetailsService);
		;
		return httpSecurity.build();
	}


}
