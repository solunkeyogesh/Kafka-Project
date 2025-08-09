package com.yogesh.kafka.serdes;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yogesh.kafka.model.Order;

public class OrderDeserializer implements Deserializer<Order> {
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public Order deserialize(String topic, byte[] data) {
		try {
			return data == null ? null : objectMapper.readValue(data, Order.class);
		} catch (Exception e) {
			throw new RuntimeException("Error deserializing Order", e);
		}
	}
}
