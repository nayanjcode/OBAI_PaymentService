//package com.nayan.obai.payment.service.config;
//
//import feign.RequestInterceptor;
//import feign.RequestTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
//
//import java.text.MessageFormat;
//
//public class FeignClientInterceptor implements RequestInterceptor
//{
//	@Autowired
//	private OAuth2AuthorizedClientManager manager;
//
//	@Override
//	public void apply(RequestTemplate template)
//	{
//		final String token = manager.authorize(OAuth2AuthorizeRequest.withClientRegistrationId("my-internal-client").principal("internal").build()).getAccessToken().getTokenValue();
//		template.header("Authorization", MessageFormat.format("Bearer {0}", token));
//	}
//}
