package org.corefx.callr.hub;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@ConditionalOnProperty(name="callr.authentication.type", havingValue = "key")
public class RequestHeaderAuthenticationProvider implements AuthenticationProvider {

	@Value("${callr.authentication.key.secret}")
	private String secret;


	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String secret = String.valueOf(authentication.getPrincipal());

		if(secret == null || secret.isEmpty() || !secret.equals(this.secret)) {
			throw new BadCredentialsException("Bad Request Header Credentials");
		}

		return new PreAuthenticatedAuthenticationToken(authentication.getPrincipal(), null, new ArrayList<>());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(PreAuthenticatedAuthenticationToken.class);
	}
}
