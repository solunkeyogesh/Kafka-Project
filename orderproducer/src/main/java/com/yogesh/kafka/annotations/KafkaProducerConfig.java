// src/main/java/com/yogesh/kafka/annotations/KafkaProducerConfig.java
package com.yogesh.kafka.annotations;

import org.apache.kafka.common.serialization.Serializer;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface KafkaProducerConfig {
    String bootstrapServers();

    Class<? extends Serializer<?>> keySerializer() default
            org.apache.kafka.common.serialization.StringSerializer.class;

    Class<? extends Serializer<?>> valueSerializer();

    // Nice-to-have tuning (with sensible defaults)
    String acks() default "all";               // "all" | "1" | "0"
    int retries() default 3;
    int lingerMs() default 0;                  // batching delay
    int batchSize() default 16384;             // in bytes
    String compressionType() default "";       // "", "gzip", "snappy", "lz4", "zstd"
    boolean enableIdempotence() default false; // set true for exactly-once in producer
}
