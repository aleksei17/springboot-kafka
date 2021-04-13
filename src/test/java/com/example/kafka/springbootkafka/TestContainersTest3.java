package com.example.kafka.springbootkafka;

import org.awaitility.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@DirtiesContext
public class TestContainersTest3 {

    public static KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka").withTag("5.4.3"));

    static {
        kafka.start();
        System.setProperty("spring.kafka.consumer.bootstrap-servers", kafka.getBootstrapServers());
        System.setProperty("spring.kafka.producer.bootstrap-servers", kafka.getBootstrapServers());
        System.setProperty("spring.kafka.bootstrap-servers", kafka.getBootstrapServers());
    }

    @Autowired
    private Producer producer;

    @Autowired
    private Consumer consumer;

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
