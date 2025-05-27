package com.nayan.obai.payment.service.external;

import com.nayan.obai.payment.rest.Order;
import com.nayan.obai.payment.rest.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "INVENTORYSERVICE")
public interface InventoryService
{
	@GetMapping("/inventory/{productId}")
	ResponseEntity<Product> getProduct(@PathVariable UUID productId);

	@PostMapping("/inventory/validate")
	public ResponseEntity<Boolean> validateAndReserveProductStock(@RequestBody Order orderProduct);
}
