package com.nayan.obai.payment.service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Component
public class FeignClientInterceptor implements RequestInterceptor
{
	@Autowired
	private OAuth2AuthorizedClientManager manager;

	@Override
	public void apply(RequestTemplate template) {
		final OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
				.withClientRegistrationId("my-internal-client")
				.principal("internal")
				.build();

		final OAuth2AuthorizedClient client = manager.authorize(authorizeRequest);

		if (client == null) {
			throw new IllegalStateException("Failed to authorize client");
		}

		final String token = client.getAccessToken().getTokenValue();
		System.out.println("Token from FeignInterceptor: " + token);

		template.header("Authorization", MessageFormat.format("Bearer {0}", token));
	}
}
