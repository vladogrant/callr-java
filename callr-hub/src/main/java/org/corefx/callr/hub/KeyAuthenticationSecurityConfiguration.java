package org.corefx.callr.hub;

import org.corefx.callr.configuration.AuthorizationConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name="callr.authentication.type", havingValue = "key")
public class KeyAuthenticationSecurityConfiguration {

	private final RequestHeaderAuthenticationProvider requestHeaderAuthenticationProvider;

	@Value("${callr.authentication.key.header}")
	private String headerName;


	@Autowired
	public KeyAuthenticationSecurityConfiguration(RequestHeaderAuthenticationProvider requestHeaderAuthenticationProvider) {
		this.requestHeaderAuthenticationProvider = requestHeaderAuthenticationProvider;
	}


	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				.addFilterAfter(requestHeaderAuthenticationFilter(), HeaderWriterFilter.class)
				.authorizeHttpRequests(customizer -> customizer.requestMatchers("/**").authenticated());
		return httpSecurity.build();
	}


	@Bean
	public RequestHeaderAuthenticationFilter requestHeaderAuthenticationFilter() {
		RequestHeaderAuthenticationFilter filter = new RequestHeaderAuthenticationFilter();
		filter.setPrincipalRequestHeader(headerName);
		filter.setExceptionIfHeaderMissing(false);
		filter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/**"));
		filter.setAuthenticationManager(authenticationManager());

		return filter;
	}


	@Bean
	protected AuthenticationManager authenticationManager() {
		return new ProviderManager(Collections.singletonList(requestHeaderAuthenticationProvider));
	}

}
