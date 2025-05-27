package com.nayan.obai.payment.config;

import com.nayan.obai.payment.service.config.RestTemplateInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class PaymentConfig
{
//	@Autowired
//	private ClientRegistrationRepository clientRegistrationRepository;
//
//	@Autowired
//	private OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate()
	{
		final RestTemplate restTemplate = new RestTemplate();
//		final List<ClientHttpRequestInterceptor> interceptorList = new ArrayList<>();
//		interceptorList.add(new RestTemplateInterceptor(oAuth2AuthorizedClientManager(clientRegistrationRepository, oAuth2AuthorizedClientRepository)));
//		restTemplate.setInterceptors(interceptorList);
		return restTemplate;
	}

//	@Bean
//	public OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager(
//			final ClientRegistrationRepository clientRegistrationRepository,
//			final OAuth2AuthorizedClientRepository auth2AuthorizedClientRepository
//	) {
//		final OAuth2AuthorizedClientProvider provider = OAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().build();
//		final DefaultOAuth2AuthorizedClientManager defaultOAuth2AuthorizedClientManager = new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, auth2AuthorizedClientRepository);
//		defaultOAuth2AuthorizedClientManager.setAuthorizedClientProvider(provider);
//		return defaultOAuth2AuthorizedClientManager;
//	}
}
