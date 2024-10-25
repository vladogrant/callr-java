package org.corefx.callr.hub;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
public class DefaultPasswordEncoderConfiguration {
	@Bean
	public PasswordEncoder passwordEncoder() {
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		return encoder;
	}

}
