package com.nayan.obai.payment.service;

import com.nayan.obai.payment.entity.Payment;
import com.nayan.obai.payment.event.PaymentResultEvent;

import java.util.List;
import java.util.UUID;

public interface PaymentService
{
	boolean makePayment(Payment payment);
	Payment getTransactionDetails(UUID transactionId);
	List<Payment> getPayments();
	void publishPaymentEvent(PaymentResultEvent event);

	}
