package com.nayan.obai.payment.exception;

public class PaymentServiceException extends RuntimeException
{
	public PaymentServiceException() {
		super("Resource not found");
	}

	public PaymentServiceException(String message) {
		super(message);
	}
}
