package com.nayan.obai.payment.entity;

public enum PaymentStatus
{
	SUCCESS(0),
	FAILED(1),
	PENDING(2);

	private final int code;

	PaymentStatus(int code)
	{
		this.code = code;
	}

	public static PaymentStatus fromCode(int code)
	{
		for (PaymentStatus s : values())
		{
			if (s.code == code) return s;
		}
		throw new IllegalArgumentException("Invalid code: " + code);
	}

	public int getCode()
	{
		return code;
	}
}