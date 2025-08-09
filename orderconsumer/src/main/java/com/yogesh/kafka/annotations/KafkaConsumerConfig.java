package com.yogesh.kafka.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import org.apache.kafka.common.serialization.Deserializer;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface KafkaConsumerConfig {
	
	String bootstrapServers();

	String groupId();

	String topic();

	Class<? extends Deserializer<?>> keyDeserializer() default org.apache.kafka.common.serialization.StringDeserializer.class;

	Class<? extends Deserializer<?>> valueDeserializer(); // required

	String autoOffsetReset() default "earliest"; // earliest | latest | none

	boolean enableAutoCommit() default true;

	int pollMillis() default 1000;
}
