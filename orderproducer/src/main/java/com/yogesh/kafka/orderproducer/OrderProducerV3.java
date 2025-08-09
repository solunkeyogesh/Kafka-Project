package com.yogesh.kafka.orderproducer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.yogesh.kafka.annotations.KafkaProducerConfig;
import com.yogesh.kafka.avro.Order;
import com.yogesh.kafka.util.KafkaProps;

import io.confluent.kafka.serializers.KafkaAvroSerializer;

@KafkaProducerConfig(bootstrapServers = "localhost:9092", keySerializer = KafkaAvroSerializer.class, valueSerializer = KafkaAvroSerializer.class, acks = "all", retries = 3, lingerMs = 5, batchSize = 32768, compressionType = "gzip", enableIdempotence = true)
public class OrderProducerV3 {
	public static void main(String[] args) throws Exception {
		var cfg = OrderProducerV3.class.getAnnotation(KafkaProducerConfig.class);
		var props = KafkaProps.producerFromAnnotation(cfg);
		props.put("schema.registry.url", "http://localhost:8081");
//		props.put("specific.avro.reader", "true");

		try (Producer<String, Order> producer = new KafkaProducer<>(props)) {
			Order order = new Order("Hp-Laptop", 340000, 2, "John Doe");
			ProducerRecord<String, Order> record = new ProducerRecord<>("orderTopicV3", order.getProduct(), order);
			var meta = producer.send(record).get();
			System.out.printf("✅ Sent %s to %s-%d @ offset %d%n", order, meta.topic(), meta.partition(), meta.offset());
		}
	}
}
