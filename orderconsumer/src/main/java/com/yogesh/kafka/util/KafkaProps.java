package com.yogesh.kafka.util;

import com.yogesh.kafka.annotations.KafkaConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.Properties;

public final class KafkaProps {
	private KafkaProps() {
	}

	public static Properties consumerFromAnnotation(KafkaConsumerConfig cfg) {
		Properties p = new Properties();
		p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, cfg.bootstrapServers());
		p.put(ConsumerConfig.GROUP_ID_CONFIG, cfg.groupId());
		p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, cfg.keyDeserializer().getName());
		p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, cfg.valueDeserializer().getName());
		p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, cfg.autoOffsetReset());
		p.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.toString(cfg.enableAutoCommit()));
		return p;
	}
}
