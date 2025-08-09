package com.yogesh.kafka.orderproducer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.yogesh.kafka.annotations.KafkaProducerConfig;
import com.yogesh.kafka.model.Order;
import com.yogesh.kafka.serdes.OrderSerializer;
import com.yogesh.kafka.util.KafkaProps;

@KafkaProducerConfig(bootstrapServers = "localhost:9092", keySerializer = org.apache.kafka.common.serialization.StringSerializer.class, valueSerializer = OrderSerializer.class, acks = "all", retries = 3, lingerMs = 5, batchSize = 32768, compressionType = "gzip", enableIdempotence = true)
public class OrderProducerV2 {
	public static void main(String[] args) throws Exception {
		var cfg = OrderProducerV2.class.getAnnotation(KafkaProducerConfig.class);
		var props = KafkaProps.producerFromAnnotation(cfg);

		try (Producer<String, Order> producer = new KafkaProducer<>(props)) {
			Order order = new Order("Hp-Laptop", 340000, 2, "John Doe");
			ProducerRecord<String, Order> record = new ProducerRecord<>("orderTopic", order.product(), order);
			var meta = producer.send(record).get();
			System.out.printf("Sent %s to %s-%d @ offset %d%n", order, meta.topic(), meta.partition(), meta.offset());
		}
	}
}
