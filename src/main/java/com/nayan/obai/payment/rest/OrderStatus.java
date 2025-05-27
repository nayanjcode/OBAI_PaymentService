package com.nayan.obai.payment.rest;

public enum OrderStatus
{
	SUCCESS(0),
	FAILED(1),
	PENDING(2);

	private final int code;

	OrderStatus(int code)
	{
		this.code = code;
	}

	public static OrderStatus fromCode(int code)
	{
		for (OrderStatus s : values())
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