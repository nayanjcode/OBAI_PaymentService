package com.nayan.obai.payment.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentStatusConverter implements AttributeConverter<PaymentStatus, Integer>
{

	@Override
	public Integer convertToDatabaseColumn(PaymentStatus status)
	{
		return status != null ? status.getCode() : null;
	}

	@Override
	public PaymentStatus convertToEntityAttribute(Integer dbCode)
	{
		return dbCode != null ? PaymentStatus.fromCode(dbCode) : null;
	}
}