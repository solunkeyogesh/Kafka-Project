package com.yogesh.kafka.orderconsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class OrderConsumer {

	public static void main(String[] args) {

		Properties props = new Properties();

		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.IntegerDeserializer");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-consumer");

		try (KafkaConsumer<String, Integer> consumer = new KafkaConsumer<>(props)) {
			consumer.subscribe(Collections.singletonList("orderTopicV1"));

			ConsumerRecords<String, Integer> orders = consumer.poll(Duration.ofSeconds(20));

			orders.forEach(x -> System.out.println(x));
			consumer.close();

		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
