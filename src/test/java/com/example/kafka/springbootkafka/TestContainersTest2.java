package com.example.kafka.springbootkafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(TestContainersTest2.KafkaTestConfiguration.class)
@SpringBootTest
@DirtiesContext
public class TestContainersTest2 {

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

    @TestConfiguration
    static class KafkaTestConfiguration {

        /**
         * Not used
         */
        @Bean
        public AdminClientConfig adminClientConfig(KafkaContainer kafkaContainer) {
            Map<String, Object> props = new HashMap<>();
            props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
            return new AdminClientConfig(props);
        }

        @Bean
        public KafkaContainer kafkaContainer() {
            KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka").withTag("5.4.3"));
            kafkaContainer.start();
            return kafkaContainer;
        }

        @Bean
        ConcurrentKafkaListenerContainerFactory<Integer, String> kafkaListenerContainerFactory(ConsumerFactory<Integer, String> consumerFactory) {
            ConcurrentKafkaListenerContainerFactory<Integer, String> factory =
                    new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(consumerFactory);
            return factory;
        }

        @Bean
        public ConsumerFactory<Integer, String> consumerFactory(KafkaContainer kafkaContainer) {
            Map<String, Object> props = new HashMap<>();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "baeldung");
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            // more standard configuration

            return new DefaultKafkaConsumerFactory<>(props);
        }

        @Bean
        public ProducerFactory<String, String> producerFactory(KafkaContainer kafkaContainer) {
            Map<String, Object> configProps = new HashMap<>();
            configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
            configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            // more standard configuration
            return new DefaultKafkaProducerFactory<>(configProps);
        }

        @Bean
        public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
            return new KafkaTemplate<>(producerFactory);
        }
    }
}
