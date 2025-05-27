package com.nayan.obai.payment.service.impl;

import com.nayan.obai.payment.config.RabbitConfig;
import com.nayan.obai.payment.rest.Order;
import com.nayan.obai.payment.entity.Payment;
import com.nayan.obai.payment.rest.Product;
import com.nayan.obai.payment.event.PaymentResultEvent;
import com.nayan.obai.payment.exception.ResourceNotFoundException;
import com.nayan.obai.payment.repository.PaymentRepository;
import com.nayan.obai.payment.service.PaymentService;
import com.nayan.obai.payment.service.external.InventoryService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService
{
	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private InventoryService inventoryService;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Override
	public void makePayment(final Payment payment)
	{
		// url to call order service: http://localhost:8084/order/{0}
		// check stock by calling inventory service before payment
		// for calling inventoryService, you will need product id for which you need to call orderService
//		final Order order = restTemplate.getForObject(MessageFormat.format("http://ORDERSERVICE/order/{0}", payment.getOrderId()), Order.class);
		final ResponseEntity<Order> orderRes =  restTemplate.getForEntity(MessageFormat.format("http://ORDERSERVICE/order/{0}", payment.getOrderId()), Order.class);
		final Order order = orderRes.getBody();
		System.out.println(order);
		final List<Product> products = order.getProducts();
		for (int i = 0; i < products.size(); i++)
		{
			final ResponseEntity<Product> productRes = inventoryService.getProduct(products.get(i).getProductId());
			System.out.println("Products: " + productRes);
		}
		final ResponseEntity<Boolean> isProductValidated = inventoryService.validateAndReserveProductStock(order);
		if(isProductValidated.getBody().booleanValue()){
			try{
				paymentRepository.save(payment);
				final PaymentResultEvent paymentSuccessEvent = PaymentResultEvent.builder()
						.isSuccessful(true)
						.customerId(order.getCustomerId())
						.orderId(order.getOrderId())
						.timestamp(Instant.now().toEpochMilli())
						.build();
				publishPaymentEvent(paymentSuccessEvent);
			}
			catch (Exception e) {
				final PaymentResultEvent paymentFailedEvent = PaymentResultEvent.builder()
						.isSuccessful(false)
						.customerId(order.getCustomerId())
						.orderId(order.getOrderId())
						.timestamp(Instant.now().toEpochMilli())
						.build();
				publishPaymentEvent(paymentFailedEvent);
			}
		} else {
			final PaymentResultEvent paymentFailedEvent = PaymentResultEvent.builder()
					.isSuccessful(false)
					.customerId(order.getCustomerId())
					.orderId(order.getOrderId())
					.timestamp(Instant.now().toEpochMilli())
					.build();
			publishPaymentEvent(paymentFailedEvent);
		}
	}

	@Override
	public Payment getTransactionDetails(final UUID transactionId)
	{
		return paymentRepository.findById(transactionId).orElseThrow(() -> new ResourceNotFoundException("this transaction does not exist"));
	}

	@Override
	public List<Payment> getPayments()
	{
		return paymentRepository.findAll();
	}


	public void publishPaymentEvent(PaymentResultEvent event) {
		rabbitTemplate.convertAndSend(RabbitConfig.PAYMENT_EXCHANGE, "payment.result", event);
	}
}
