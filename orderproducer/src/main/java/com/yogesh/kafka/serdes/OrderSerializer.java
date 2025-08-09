package com.yogesh.kafka.serdes;

import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yogesh.kafka.model.Order;

public class OrderSerializer implements Serializer<Order> {
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public byte[] serialize(String topic, Order data) {
		try {
			return data == null ? null : objectMapper.writeValueAsBytes(data);
		} catch (Exception e) {
			throw new RuntimeException("Error serializing Order", e);
		}
	}
}
