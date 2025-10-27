# Kafka experiment

## Starting a Kafka broker with Docker

To start a Kafka broker using Docker, you can use the following command:

```bash
docker compose -f kafka-experiment/compose.yaml up -d
```

## Test publish and consume messages

Via api /api/messages

```bash
curl -X POST http://localhost:8080/api/messages \
  -H "Content-Type: application/json" \
  -d '"Hello, Kafka!"'
```
```bash
curl -X POST http://localhost:8080/api/messages \
  -H "Content-Type: application/json" \
  -d '{"content":"Hello from curl!","sender":"curl-user"}'
```