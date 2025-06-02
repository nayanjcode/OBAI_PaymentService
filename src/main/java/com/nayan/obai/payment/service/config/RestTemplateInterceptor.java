package com.nayan.obai.payment.service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

import java.io.IOException;
import java.text.MessageFormat;

public class RestTemplateInterceptor implements ClientHttpRequestInterceptor
{
	@Autowired
	private OAuth2AuthorizedClientManager manager;

	public RestTemplateInterceptor(final OAuth2AuthorizedClientManager manager)
	{
		this.manager = manager;
	}

	@Override
	public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, final ClientHttpRequestExecution execution) throws IOException
	{
		final String token = manager.authorize(OAuth2AuthorizeRequest.withClientRegistrationId("my-internal-client").principal("internal").build()).getAccessToken().getTokenValue();
		request.getHeaders().add("Authorization", MessageFormat.format("Bearer {0}", token));
		System.out.println("Interceptor token" + token);
		return execution.execute(request, body);
	}
}
