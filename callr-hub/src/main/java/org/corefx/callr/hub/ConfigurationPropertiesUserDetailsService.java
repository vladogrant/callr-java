package org.corefx.callr.hub;

import org.corefx.callr.configuration.AuthorizationConfigurationProperties;
import org.corefx.callr.configuration.BasicAuthenticationConfigurationProperties;
import org.corefx.callr.configuration.GlobalConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConfigurationPropertiesUserDetailsService implements UserDetailsService {

	@Autowired
	private GlobalConfigurationProperties config;

	@Autowired
	private PasswordEncoder passwordEncoder;


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AuthorizationConfigurationProperties authz = config.getAuthorization();
		if(authz == null || authz.getUserRoles() == null || authz.getUserRoles().isEmpty()) {
			throw new UsernameNotFoundException("Username not found!");
		}
		List<String> roles = authz.getUserRoles().get(username);
		if(roles == null) {
			throw new UsernameNotFoundException("Username not found!");
		}
		List<GrantedAuthority> authorities =
				AuthorityUtils.createAuthorityList(roles.stream().map(s -> "ROLE_" + s.toUpperCase()).toList());
		BasicAuthenticationConfigurationProperties auth = config.getAuthentication().getBasic();
		String password = passwordEncoder.encode(auth.getSecret());
		return new User(username, password, authorities);
	}
}
