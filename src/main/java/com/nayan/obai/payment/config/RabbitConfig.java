package com.nayan.obai.payment.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig
{
	public static final String PAYMENT_EXCHANGE = "payment.exchange";

	@Bean
	public TopicExchange paymentExchange()
	{
		return new TopicExchange(PAYMENT_EXCHANGE);
	}
}
