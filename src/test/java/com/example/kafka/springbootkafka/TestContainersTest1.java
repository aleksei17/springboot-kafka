package com.example.kafka.springbootkafka;

import org.awaitility.Duration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@DirtiesContext
@ContextConfiguration(initializers = TestContainersTest1.Initializer.class)
public class TestContainersTest1 {

    public static KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka").withTag("5.4.3"));

    @Autowired
    private Producer producer;

    @Autowired
    private Consumer consumer;

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

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext ctx) {
            TestPropertyValues.of(
                    "spring.kafka.consumer.bootstrap-servers=" + kafka.getBootstrapServers(),
                    "spring.kafka.producer.bootstrap-servers=" + kafka.getBootstrapServers(),
                    "spring.kafka.bootstrap-servers=" + kafka.getBootstrapServers()

            ).applyTo(ctx.getEnvironment());
        }
    }
}
