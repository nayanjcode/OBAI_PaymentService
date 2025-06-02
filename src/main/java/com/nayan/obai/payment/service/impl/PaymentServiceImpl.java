package com.nayan.obai.payment.service.impl;

import com.nayan.obai.payment.config.RabbitConfig;
import com.nayan.obai.payment.rest.Order;
import com.nayan.obai.payment.entity.Payment;
import com.nayan.obai.payment.rest.Product;
import com.nayan.obai.payment.event.PaymentResultEvent;
import com.nayan.obai.payment.exception.PaymentServiceException;
import com.nayan.obai.payment.repository.PaymentRepository;
import com.nayan.obai.payment.service.PaymentService;
import com.nayan.obai.payment.service.external.InventoryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
	final Logger logger = LogManager.getLogger("PaymentService");

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private InventoryService inventoryService;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public PaymentServiceImpl(final PaymentRepository paymentRepository, final RestTemplate restTemplate, final InventoryService inventoryService, final RabbitTemplate rabbitTemplate)
	{
		this.paymentRepository = paymentRepository;
		this.restTemplate = restTemplate;
		this.inventoryService = inventoryService;
		this.rabbitTemplate = rabbitTemplate;
	}

	@Override
	public boolean makePayment(final Payment payment)
	{
		boolean isPaymentSuccessful = false;
		// url to call order service: http://localhost:8084/order/{0}
		// check stock by calling inventory service before payment
		// for calling inventoryService, you will need product id for which you need to call orderService
//		final Order order = restTemplate.getForObject(MessageFormat.format("http://ORDERSERVICE/order/{0}", payment.getOrderId()), Order.class);

		logger.debug("Calling order service for order info for the payment");
		final ResponseEntity<Order> orderRes =  restTemplate.getForEntity(MessageFormat.format("http://ORDERSERVICE/order/{0}", payment.getOrderId()), Order.class);
		final Order order = orderRes.getBody();
		logger.info("Order: " + order);
		final List<Product> products = order.getProducts();
		logger.debug("Calling product service for product info of the order");
		for (int i = 0; i < products.size(); i++)
		{
			logger.info("Calling product service for getting product info for product: " + products.get(i).getProductId());
			final ResponseEntity<Product> productRes = inventoryService.getProduct(products.get(i).getProductId());
			logger.info("Products: " + productRes);
		}
		logger.debug("calling product service for validating, locking and reserving product");
		final ResponseEntity<Boolean> isProductValidated = inventoryService.validateAndReserveProductStock(order);
		if(isProductValidated.getBody().booleanValue()){
			try{
				logger.debug("initiating payment");
				paymentRepository.save(payment);
				logger.info("Payment success");
				final PaymentResultEvent paymentSuccessEvent = PaymentResultEvent.builder()
						.isSuccessful(true)
						.customerId(order.getCustomerId())
						.orderId(order.getOrderId())
						.timestamp(Instant.now().toEpochMilli())
						.build();
				publishPaymentEvent(paymentSuccessEvent);
				isPaymentSuccessful = true;
			}
			catch (Exception e) {
				logger.error("Payment failed: " + e.getMessage());
				final PaymentResultEvent paymentFailedEvent = PaymentResultEvent.builder()
						.isSuccessful(false)
						.customerId(order.getCustomerId())
						.orderId(order.getOrderId())
						.timestamp(Instant.now().toEpochMilli())
						.build();
				publishPaymentEvent(paymentFailedEvent);
			}
		} else {
			logger.error("Product validation failed");
			final PaymentResultEvent paymentFailedEvent = PaymentResultEvent.builder()
					.isSuccessful(false)
					.customerId(order.getCustomerId())
					.orderId(order.getOrderId())
					.timestamp(Instant.now().toEpochMilli())
					.build();
			publishPaymentEvent(paymentFailedEvent);
		}
		return isPaymentSuccessful;
	}

	@Override
	public Payment getTransactionDetails(final UUID transactionId)
	{
		logger.debug("getting payment details for transaction id = " + transactionId);
		return paymentRepository.findById(transactionId).orElseThrow(() -> new PaymentServiceException("this transaction does not exist"));
	}

	// ideally this is not a good method. This should be get payments of particular users.
	@Override
	public List<Payment> getPayments()
	{
		logger.debug("getting all payments info");
		return paymentRepository.findAll();
	}

	@Override
	public void publishPaymentEvent(PaymentResultEvent event) {
		if(event.isSuccessful())
		{
			logger.debug("publishing payment success event");
		} else {
			logger.debug("publishing payment fail event");
		}
		rabbitTemplate.convertAndSend(RabbitConfig.PAYMENT_EXCHANGE, "payment.result", event);
	}
}
