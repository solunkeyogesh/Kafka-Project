package com.yogesh.kafka.orderconsumer;

import com.yogesh.kafka.annotations.KafkaConsumerConfig;
import com.yogesh.kafka.util.KafkaProps;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import com.yogesh.kafka.avro.Order;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@KafkaConsumerConfig(
        bootstrapServers = "localhost:9092",
        groupId = "order-consumer-group-v3",
        topic = "orderTopicV3", // use a fresh topic if you previously sent non-Avro data
        keyDeserializer = org.apache.kafka.common.serialization.StringDeserializer.class,
        valueDeserializer = io.confluent.kafka.serializers.KafkaAvroDeserializer.class,
        autoOffsetReset = "earliest",
        enableAutoCommit = true,
        pollMillis = 1000
)
public class OrderConsumerV3 {

    public static void main(String[] args) {
        // Read annotation + base props
        com.yogesh.kafka.annotations.KafkaConsumerConfig cfg =
                OrderConsumerV3.class.getAnnotation(com.yogesh.kafka.annotations.KafkaConsumerConfig.class);
        Properties props = KafkaProps.consumerFromAnnotation(cfg);

        // --- IMPORTANT: Schema Registry URL ---
        props.put("schema.registry.url", "http://localhost:8081");
        // If you generated SpecificRecord classes, set this to true and change the value type:
         props.put("specific.avro.reader", "true");

        try (KafkaConsumer<String, GenericRecord> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(cfg.topic()));

            while (true) {
                ConsumerRecords<String, GenericRecord> records =
                        consumer.poll(Duration.ofMillis(cfg.pollMillis()));

                for (ConsumerRecord<String, GenericRecord> r : records) {
                    GenericRecord value = r.value();

                    // Safely extract known fields (if present in schema)
                    Object product = value != null ? value.get("product") : null;
                    Object price = value != null ? value.get("price") : null;
                    Object quantity = value != null ? value.get("quantity") : null;
                    Object customerName = value != null ? value.get("customerName") : null;

                    System.out.printf(
                            "Consumed key=%s product=%s price=%s qty=%s customer=%s from %s-%d @ offset %d%n",
                            r.key(), product, price, quantity, customerName,
                            r.topic(), r.partition(), r.offset()
                    );
                }
            }
        }
    }
}
