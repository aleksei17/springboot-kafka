package com.example.kafka.springbootkafka;

import org.awaitility.Duration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@DirtiesContext
public class TestContainersTest4 {

    public static KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka").withTag("5.4.3"));

    @Autowired
    private Producer producer;

    @Autowired
    private Consumer consumer;

    @DynamicPropertySource
    static void setKafkaBrokers(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.consumer.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.kafka.producer.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @BeforeAll
    static void beforeAll() {
        kafka.start();
    }

    @Test
    void test() {
        String message = "test message";
        producer.sendMessage(message);

        await()
                .atLeast(Duration.ZERO)
                .atMost(Duration.ONE_SECOND)
                .with()
                .pollInterval(Duration.ONE_HUNDRED_MILLISECONDS)
                .until(consumer::getLastMessageConsumed, equalTo(message));
    }
}
