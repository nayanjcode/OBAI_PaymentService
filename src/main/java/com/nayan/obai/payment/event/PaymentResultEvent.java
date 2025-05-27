package com.nayan.obai.payment.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResultEvent
{
	private UUID orderId;
	private UUID customerId;
	private Long timestamp;
	private boolean isSuccessful;
}
