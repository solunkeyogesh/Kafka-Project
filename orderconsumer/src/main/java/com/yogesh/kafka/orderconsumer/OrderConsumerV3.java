package com.yogesh.kafka.orderconsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.yogesh.kafka.avro.Order; // <-- Generated from your .avsc
import com.yogesh.kafka.annotations.KafkaConsumerConfig;
import com.yogesh.kafka.util.KafkaProps;

@KafkaConsumerConfig(bootstrapServers = "localhost:9092", groupId = "order-consumer-group-v3", topic = "orderTopicV3", keyDeserializer = org.apache.kafka.common.serialization.StringDeserializer.class, valueDeserializer = io.confluent.kafka.serializers.KafkaAvroDeserializer.class, autoOffsetReset = "earliest", enableAutoCommit = true, pollMillis = 1000)
public class OrderConsumerV3 {

	public static void main(String[] args) {
		KafkaConsumerConfig cfg = OrderConsumerV3.class.getAnnotation(KafkaConsumerConfig.class);
		Properties props = KafkaProps.consumerFromAnnotation(cfg);

		// Schema Registry config
		props.put("schema.registry.url", "http://localhost:8081");
		props.put("specific.avro.reader", "true"); // Important for SpecificRecord

		try (KafkaConsumer<String, Order> consumer = new KafkaConsumer<>(props)) {
			consumer.subscribe(Collections.singletonList(cfg.topic()));

			while (true) {
				ConsumerRecords<String, Order> records = consumer.poll(Duration.ofMillis(cfg.pollMillis()));

				for (ConsumerRecord<String, Order> r : records) {
					Order order = r.value(); // Already a typed Avro object
					if (order != null) {
						System.out.printf("Consumed key=%s product=%s price=%d qty=%d customer=%s from %s-%d @ offset %d%n",r.key(), order.getProduct(), order.getPrice(), order.getQuantity(),order.getCustomerName(), r.topic(), r.partition(), r.offset());
					}
				}
			}
		}
	}
}
