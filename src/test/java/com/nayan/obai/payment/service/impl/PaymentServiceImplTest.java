package com.nayan.obai.payment.service.impl;

import com.nayan.obai.payment.config.RabbitConfig;
import com.nayan.obai.payment.entity.Payment;
import com.nayan.obai.payment.event.PaymentResultEvent;
import com.nayan.obai.payment.repository.PaymentRepository;
import com.nayan.obai.payment.rest.Order;
import com.nayan.obai.payment.rest.Product;
import com.nayan.obai.payment.service.external.InventoryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest
{
	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private InventoryService inventoryService;

	@Mock
	private RabbitTemplate rabbitTemplate;

	@InjectMocks
	private PaymentServiceImpl paymentService;

	@Test
	void shouldPublishSuccessEventWhenStockIsValid() {
		final UUID orderId = UUID.randomUUID();
		final UUID paymentId = UUID.randomUUID();
		final Payment payment = Payment.builder().transactionId(paymentId).orderId(orderId).build();

		final Order order = Order.builder().orderId(orderId).customerId(UUID.randomUUID()).build();

		final Product product = Product.builder().productId(UUID.randomUUID()).build();

		order.setProducts(List.of(product));

		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(Order.class)))
				.thenReturn(new ResponseEntity<>(order, HttpStatus.OK));
		Mockito.when(inventoryService.getProduct(Mockito.any())).thenReturn(new ResponseEntity<>(product, HttpStatus.OK));
		Mockito.when(inventoryService.validateAndReserveProductStock(order))
				.thenReturn(new ResponseEntity<>(true, HttpStatus.OK));
		Mockito.when(paymentRepository.save(payment)).thenReturn(payment);

		paymentService.makePayment(payment);

		Mockito.verify(paymentRepository, Mockito.times(1)).save(payment);
		Mockito.verify(rabbitTemplate, Mockito.times(1))
				.convertAndSend(Mockito.eq(RabbitConfig.PAYMENT_EXCHANGE), Mockito.eq("payment.result"), Mockito.any(PaymentResultEvent.class));
	}

	@Test
	void shouldPublishFailureEventWhenStockValidationFails() {
		final UUID orderId = UUID.randomUUID();
		final Payment payment = Payment.builder().orderId(orderId).build();

		final Order order = Order.builder().orderId(orderId).customerId(UUID.randomUUID()).build();

		final Product product = Product.builder().productId(UUID.randomUUID()).build();

		order.setProducts(List.of(product));

		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(Order.class)))
				.thenReturn(new ResponseEntity<>(order, HttpStatus.OK));
		Mockito.when(inventoryService.getProduct(Mockito.any())).thenReturn(new ResponseEntity<Product>(product, HttpStatus.OK));
		Mockito.when(inventoryService.validateAndReserveProductStock(order))
				.thenReturn(new ResponseEntity<>(false, HttpStatus.OK));

		paymentService.makePayment(payment);

		Mockito.verify(paymentRepository, Mockito.never()).save(Mockito.any());
		Mockito.verify(rabbitTemplate, Mockito.times(1))
				.convertAndSend(Mockito.eq(RabbitConfig.PAYMENT_EXCHANGE), Mockito.eq("payment.result"), Mockito.any(PaymentResultEvent.class));
	}

	@Test
	void shouldPublishFailureEventWhenSavingPaymentFails() {
		final UUID orderId = UUID.randomUUID();
		final Payment payment = Payment.builder().orderId(orderId).build();

		final Order order = Order.builder().orderId(orderId).customerId(UUID.randomUUID()).build();

		final Product product = Product.builder().productId(UUID.randomUUID()).build();

		order.setProducts(List.of(product));

		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(Order.class)))
				.thenReturn(new ResponseEntity<>(order, HttpStatus.OK));
		Mockito.when(inventoryService.getProduct(Mockito.any())).thenReturn(new ResponseEntity<>(product, HttpStatus.OK));
		Mockito.when(inventoryService.validateAndReserveProductStock(order))
				.thenReturn(new ResponseEntity<>(true, HttpStatus.OK));
		Mockito.when(paymentRepository.save(payment)).thenThrow(new RuntimeException("DB error"));

		paymentService.makePayment(payment);

		Mockito.verify(rabbitTemplate, Mockito.times(1))
				.convertAndSend(Mockito.eq(RabbitConfig.PAYMENT_EXCHANGE), Mockito.eq("payment.result"), Mockito.any(PaymentResultEvent.class));
	}

	@Test
	void shouldReturnPaymentWhenTransactionExists() {
		final UUID txId = UUID.randomUUID();
		final Payment payment = Payment.builder().transactionId(txId).build();
		Mockito.when(paymentRepository.findById(txId)).thenReturn(Optional.of(payment));

		final Payment result = paymentService.getTransactionDetails(txId);

		Assertions.assertEquals(txId, result.getTransactionId());
	}

	@Test
	void shouldThrowExceptionWhenTransactionNotFound() {
		final UUID txId = UUID.randomUUID();
		Mockito.when(paymentRepository.findById(txId)).thenReturn(Optional.empty());

		Assertions.assertThrows(RuntimeException.class, () -> paymentService.getTransactionDetails(txId));
	}

	@Test
	void shouldReturnAllPayments() {
		final List<Payment> payments = List.of(new Payment(), new Payment());
		Mockito.when(paymentRepository.findAll()).thenReturn(payments);

		final List<Payment> result = paymentService.getPayments();

		Assertions.assertEquals(2, result.size());
	}

}