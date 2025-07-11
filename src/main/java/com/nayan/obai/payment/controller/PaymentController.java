package com.nayan.obai.payment.controller;

import com.nayan.obai.payment.entity.Payment;
import com.nayan.obai.payment.entity.PaymentStatus;
import com.nayan.obai.payment.service.PaymentService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payment")
public class PaymentController
{
	final Logger logger = LogManager.getLogger("PaymentController");

	@Autowired
	private PaymentService paymentService;

	@PostMapping("/")
	@CircuitBreaker(name = "orderInventoryBreaker", fallbackMethod = "orderInventoryFallback")
	@Retry(name = "orderInventoryRetry", fallbackMethod = "orderInventoryFallback")
	@RateLimiter(name = "paymentLimiter", fallbackMethod = "orderInventoryFallback")
	@PreAuthorize("hasRole('REGULAR_USERS')")
	ResponseEntity<Void> makePayment(@RequestBody final Payment payment)
	{
		logger.debug("payment request: " + payment);
		boolean isPaymentSuccess = paymentService.makePayment(payment);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	// creating fallback method for ckt breaker
	public ResponseEntity<Payment> orderInventoryFallback(final Payment payment, final Exception e)
	{
		logger.debug("calling fallback for make payment: " + e.getMessage());
		final Payment p = Payment.builder()
				.paymentStatus(PaymentStatus.FAILED)
				.build();
		return new ResponseEntity(p, HttpStatus.OK);
	}

	@GetMapping("/{transactionId}")
	@PreAuthorize("hasRole('ADMIN')")
	ResponseEntity<Payment> getTransactionDetails(@PathVariable final UUID transactionId)
	{
		final Payment transactionDetails = paymentService.getTransactionDetails(transactionId);
		return ResponseEntity.ok(transactionDetails);
	}

	@GetMapping("/")
	@PreAuthorize("hasRole('ADMIN')")
	ResponseEntity<List<Payment>> getPayments()
	{
		final List<Payment> payments = paymentService.getPayments();
		return ResponseEntity.ok(payments);
	}
}
