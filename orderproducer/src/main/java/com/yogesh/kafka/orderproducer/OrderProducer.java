package com.yogesh.kafka.orderproducer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.*;

public class OrderProducer {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.IntegerSerializer");

        try (Producer<String, Integer> producer = new KafkaProducer<>(props)) {
            String key = "Hp-Laptop";
            Integer value = 340000;

            ProducerRecord<String, Integer> record =
                    new ProducerRecord<>("orderTopicV1", key, value);

            RecordMetadata meta = producer.send(record).get();
            System.out.printf(
                    "Sent order with key=%s, price=%d to %s-%d @ offset %d%n",
                    key, value, meta.topic(), meta.partition(), meta.offset()
            );

            producer.flush();
            producer.close();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
