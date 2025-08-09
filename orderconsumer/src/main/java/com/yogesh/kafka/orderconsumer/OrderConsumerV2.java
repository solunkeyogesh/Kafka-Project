package com.yogesh.kafka.orderconsumer;

import java.time.Duration;
import java.util.Collections;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.yogesh.kafka.annotations.KafkaConsumerConfig;
import com.yogesh.kafka.model.Order;
import com.yogesh.kafka.serdes.OrderDeserializer;
import com.yogesh.kafka.util.KafkaProps;

@KafkaConsumerConfig(bootstrapServers = "localhost:9092", groupId = "order-consumer-group-v2", topic = "orderTopic", keyDeserializer = org.apache.kafka.common.serialization.StringDeserializer.class, valueDeserializer = OrderDeserializer.class, autoOffsetReset = "earliest", enableAutoCommit = true, pollMillis = 5000)
public class OrderConsumerV2 {

	public static void main(String[] args) {
		KafkaConsumerConfig cfg = OrderConsumerV2.class.getAnnotation(KafkaConsumerConfig.class);
		java.util.Properties props = KafkaProps.consumerFromAnnotation(cfg);

		int messageCount = 0;
		int maxMessages = 3;

		try (KafkaConsumer<String, Order> consumer = new KafkaConsumer<>(props)) {
			consumer.subscribe(Collections.singletonList(cfg.topic()));

			while (true) {
				ConsumerRecords<String, Order> records = consumer.poll(Duration.ofMillis(cfg.pollMillis()));

				for (ConsumerRecord<String, Order> r : records) {
					System.out.printf("Consumed key=%s value=%s from %s-%d @ offset %d%n", r.key(), r.value(),r.topic(), r.partition(), r.offset());

					messageCount++;
					if (messageCount >= maxMessages) {
						System.out.println("✅ Reached " + maxMessages + " messages. Stopping consumer.");
						return; // try-with-resources closes the consumer
					}
				}
			}
		}
	}
}
