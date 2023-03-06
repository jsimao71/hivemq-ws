/**
 * 
 */
package com.hivemq.ws.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
//@EnableWebSecurity
public class WebSecurityConfig  {

	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.authorizeHttpRequests((requests) -> requests
				.requestMatchers(new RequestMatcher() {

					@Override
					public boolean matches(HttpServletRequest request) {
						System.out.println(request.getMethod() + " " + request.getRequestURI());
						return true;
					}
				}).permitAll()
					
				.requestMatchers(new String[]{"/mqtt/**"}).permitAll()
				.requestMatchers(HttpMethod.POST, new String[]{"/mqtt/**"}).permitAll()
				.requestMatchers(HttpMethod.PUT, new String[]{"/mqtt/**"}).permitAll()
				.requestMatchers(HttpMethod.DELETE, new String[]{"/mqtt/**"}).permitAll()
				.requestMatchers(new String[]{"/", "/login", "/signup"}).permitAll()
				.anyRequest().permitAll()
				//.anyRequest().authenticated()
			)
			.httpBasic()
			//.and()
			//.formLogin((form) -> form
			//	.loginPage("/login")
			//	.permitAll()
			//)
			//.logout((logout) -> logout.permitAll())
			;

		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		@SuppressWarnings("deprecation")
		UserDetails user =
			 User.withDefaultPasswordEncoder()
				.username("user")
				.password("password")
				.roles("USER")
				.build();

		return new InMemoryUserDetailsManager(user);
	}
}