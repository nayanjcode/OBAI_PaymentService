package com.nayan.obai.payment.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResultEvent implements Serializable
{
	private static final long serialVersionUID = 1L;

	private UUID orderId;
	private UUID customerId;
	private Long timestamp;
	private boolean isSuccessful;
}
