package com.nayan.obai.payment.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product
{
//	private UUID id;
	private UUID productId;
	private Integer quantity;
	private LocalDateTime lastUpdated;
}
