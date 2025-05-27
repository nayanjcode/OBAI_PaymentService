//package com.nayan.obai.payment.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity(prePostEnabled = true)
//public class SecurityConfig
//{
//	@Bean
//	public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception
//	{
//		http
//				.authorizeHttpRequests(authorize -> authorize
//						.requestMatchers("/login**", "/oauth2/**").permitAll()
//						.requestMatchers("/payment/**").authenticated()
//						.anyRequest().permitAll()
//				)
////				.oauth2Login(Customizer.withDefaults()) // Auth0 login flow
//				.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())); // JWT token validation
//
//		return http.build();
//	}
//}
