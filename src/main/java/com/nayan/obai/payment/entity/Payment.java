package com.nayan.obai.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Payment")
public class Payment
{
	@Id
	@GeneratedValue
	@Column(name = "transactionId")
	private UUID transactionId;

	@Column(name = "orderId")
	private UUID orderId;

	@Column(name = "customerId")
	private UUID customerId;

	@Column(name = "totalAmount")
	private BigDecimal totalAmount;

	@Column(name = "paymentStatus")
	@Convert(converter = PaymentStatusConverter.class)
	private PaymentStatus paymentStatus;

	@Column(name = "timestamp")
	@UpdateTimestamp
	private LocalDateTime timestamp;


}
