package com.example.kafka.springbootkafka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
@DirtiesContext
@EmbeddedKafka
// you van also define topics in EmbeddedKafka
//@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9091", "port=9091" })
public class EmbeddedKafkaIntegrationTest {

    @Autowired
    private Producer producer;

    @Autowired
    private Consumer consumer;

    @Test
    void name() throws InterruptedException {
        String message = "test message";
        producer.sendMessage(message);
        Thread.sleep(1_000);
        String actual = consumer.getLastMessageConsumed();
        assertEquals(message, actual);
    }
}
