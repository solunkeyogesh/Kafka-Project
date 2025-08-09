# Kafka + Zookeeper + Schema Registry (Confluent 7.2.1) — Docker Setup

A minimal, single-node setup using Docker containers on a user-defined network. Works well on Docker Desktop (Windows/macOS) and Linux.

## Prerequisites
- Docker installed and running
- Ports available: `2181`, `9092`, `8081`

## 1) Create a Docker network
```bash
docker network create kafka-net
```

## 2) Pull images
```bash
docker pull confluentinc/cp-zookeeper:7.2.1
docker pull confluentinc/cp-kafka:7.2.1
docker pull confluentinc/cp-schema-registry:7.2.1
```

## 3) Start containers

### Zookeeper
```bash
docker run -d --name zookeeper --network kafka-net   -p 2181:2181   -e ZOOKEEPER_CLIENT_PORT=2181   confluentinc/cp-zookeeper:7.2.1
```

### Kafka (dual listeners: host + internal)
```bash
docker run -d --name kafka --network kafka-net   -p 9092:9092   -e KAFKA_BROKER_ID=1   -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181   -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=PLAINTEXT:PLAINTEXT,PLAINTEXT_INTERNAL:PLAINTEXT   -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092,PLAINTEXT_INTERNAL://0.0.0.0:9093   -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092,PLAINTEXT_INTERNAL://kafka:9093   -e KAFKA_INTER_BROKER_LISTENER_NAME=PLAINTEXT_INTERNAL   -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1   -e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1   -e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1   -e KAFKA_MIN_INSYNC_REPLICAS=1   -e KAFKA_DEFAULT_REPLICATION_FACTOR=1   confluentinc/cp-kafka:7.2.1
```

### Schema Registry
```bash
docker run -d --name schema-registry --network kafka-net   -p 8081:8081   -e SCHEMA_REGISTRY_HOST_NAME=schema-registry   -e SCHEMA_REGISTRY_LISTENERS=http://0.0.0.0:8081   -e SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS=PLAINTEXT://kafka:9093   -e SCHEMA_REGISTRY_KAFKASTORE_TOPIC_REPLICATION_FACTOR=1   -e SCHEMA_REGISTRY_KAFKASTORE_TOPIC_MIN_ISR=1   confluentinc/cp-schema-registry:7.2.1
```

## 4) Quick verification

### Check containers
```bash
docker ps
```

### Schema Registry health
```bash
curl http://localhost:8081/subjects
# Expect: [] (empty array) if no schemas yet
```

## 5) Create a topic (optional sanity check)
```bash
docker exec -it kafka bash -lc "kafka-topics --bootstrap-server kafka:9093 --create --topic test-topic --replication-factor 1 --partitions 1"
docker exec -it kafka bash -lc "kafka-topics --bootstrap-server kafka:9093 --list"
```

## 6) Produce & consume (optional)
```bash
# Producer
docker exec -it kafka bash -lc "kafka-console-producer --bootstrap-server kafka:9093 --topic test-topic"

# In another terminal, consumer
docker exec -it kafka bash -lc "kafka-console-consumer --bootstrap-server kafka:9093 --topic test-topic --from-beginning"
```

## 7) Common troubleshooting

- **Schema Registry timeout / join group errors**  
  Ensure `_schemas` has RF=1 and Kafka is single-node friendly (env vars above already set). If `_schemas` was created with RF=3, recreate it:
  ```bash
  docker exec -it kafka bash -lc "kafka-topics --bootstrap-server kafka:9093 --delete --topic _schemas || true"
  docker exec -it kafka bash -lc "kafka-topics --bootstrap-server kafka:9093 --create --topic _schemas --partitions 1 --replication-factor 1"
  ```

- **“Broker may not be available” / `localhost` inside containers**  
  Use the **internal listener** `kafka:9093` between containers (already configured). Host apps use `localhost:9092`.

- **Ports in use**  
  Stop or change any service already bound to `2181`, `9092`, `8081`.

## 8) Stop & cleanup
```bash
# Stop
docker stop schema-registry kafka zookeeper

# Remove
docker rm schema-registry kafka zookeeper

# Optional: remove network
docker network rm kafka-net
```

---

**Notes (Windows/Docker Desktop):**
- Use `localhost:9092` from your host apps/clients.
- Containers should reference Kafka as `kafka:9093` (thanks to the shared `kafka-net` network).
