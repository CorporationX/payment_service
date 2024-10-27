Если вы хотите запустить через кафку, то надо ее локально поднять, должен быть включен докер десктоп,  у меня виндовс, поэтому гайд будет про виндовс:
1. Локально создаем папку, любую, создаем там файл docker-compose.yml и вставляем туда следующий код:
```yaml
version: '3'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:latest
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: "zookeeper:2181"
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,PLAINTEXT_HOST://host.docker.internal:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: 'true'
    ports:
      - "9092:9092"

  kafdrop:
    image: obsidiandynamics/kafdrop:latest
    depends_on:
      - kafka
    environment:
      KAFKA_BROKERCONNECT: host.docker.internal:9092
      JVM_OPTS: "-Xms32M -Xmx64M"
    ports:
      - "9101:9000"
```

Далее в терминале переходим в папку с файлом docker-compose.yml и выполняем команду:
```shell
docker-compose up -d
```
Всё, контейнеры запущены, можно пулить ветку и тестить. 
Тут есть кафдроп - юайка для визуального простомтра топика с сообщениями.