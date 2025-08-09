package com.yogesh.kafka.util;

import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerConfig;

import com.yogesh.kafka.annotations.KafkaProducerConfig;

public final class KafkaProps {
	private KafkaProps() {
	}

	public static Properties producerFromAnnotation(KafkaProducerConfig cfg) {
		Properties p = new Properties();
		p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, cfg.bootstrapServers());
		p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, cfg.keySerializer().getName());
		p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, cfg.valueSerializer().getName());

		// Optional tunables
		p.put(ProducerConfig.ACKS_CONFIG, cfg.acks());
		p.put(ProducerConfig.RETRIES_CONFIG, cfg.retries());
		p.put(ProducerConfig.LINGER_MS_CONFIG, cfg.lingerMs());
		p.put(ProducerConfig.BATCH_SIZE_CONFIG, cfg.batchSize());
		if (!cfg.compressionType().isEmpty()) {
			p.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, cfg.compressionType());
		}
		p.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, cfg.enableIdempotence());
		return p;
	}
}
