package org.corefx.callr.hub;

import org.corefx.callr.configuration.AuthorizationConfigurationProperties;
import org.corefx.callr.configuration.GlobalConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.util.List;

@Configuration
@ConditionalOnProperty(name = "callr.authentication.type", havingValue = "basic")
@EnableWebSecurity
@EnableConfigurationProperties(GlobalConfigurationProperties.class)
public class BasicAuthenticationSecurityConfiguration {

	@Autowired
	private RestAuthenticationEntryPoint authenticationEntryPoint;

	@Autowired
	private GlobalConfigurationProperties config;


	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				.authorizeHttpRequests(customizer -> customizer.anyRequest().authenticated())
				.httpBasic(config -> config.authenticationEntryPoint(authenticationEntryPoint))
				.addFilterAfter(new CustomFilter(), BasicAuthenticationFilter.class);
		;
		return httpSecurity.build();
	}


	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder builder) throws Exception {

		AuthorizationConfigurationProperties authz = config.getAuthorization();
		if(authz == null || authz.getUserRoles() == null || authz.getUserRoles().isEmpty()) {
			return;
		}

		String secret = config.getAuthentication().getBasic().getSecret();
		if(secret == null) return;

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> userDetails = builder.inMemoryAuthentication();
		userDetails = userDetails.passwordEncoder(passwordEncoder);
		for(String user : authz.getUserRoles().keySet()) {
			List<String> roles = authz.getUserRoles().get(user);
			userDetails = userDetails
					.withUser(user)
					.password(passwordEncoder.encode(secret))
					.authorities(roles.toArray(new String[0])).and();

		}
	}

}
