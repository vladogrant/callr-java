package org.corefx.callr.hub;

import lombok.AllArgsConstructor;
import org.corefx.callr.configuration.AuthenticationConfigurationProperties;
import org.corefx.callr.configuration.AuthorizationConfigurationProperties;
import org.corefx.callr.configuration.GlobalConfigurationProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class ConfigurationPropertiesUserDetailsService implements UserDetailsService {

	private final GlobalConfigurationProperties config;

	private final PasswordEncoder passwordEncoder;


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AuthorizationConfigurationProperties authz = config.getAuthorization();
		List<GrantedAuthority> authorities = new ArrayList<>();
		if(authz != null && authz.getUserRoles() != null) {
			List<String> roles = authz.getUserRoles().get(username);
			if(roles != null) {
				authorities = AuthorityUtils.createAuthorityList(roles.stream().map(s -> "ROLE_" + s.toUpperCase()).toList());
			}
		}
		String password = "";
		AuthenticationConfigurationProperties auth = config.getAuthentication();
		if(auth.getType().equals("basic"))
			password = passwordEncoder.encode(auth.getBasic().getSecret());
		return new User(username, password, authorities);
	}
}
