package com.nayan.obai.payment.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Order
{
	private UUID orderId;
	private UUID customerId;
	private BigDecimal totalAmount;
	private OrderStatus orderStatus;
	private List<Product> products;
}
