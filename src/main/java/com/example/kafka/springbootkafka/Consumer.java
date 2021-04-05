package com.example.kafka.springbootkafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class Consumer {

    private String lastMessageConsumed;

    @KafkaListener(topics = "test_topic",groupId = "group_id")
    public void consumeMessage(String message){
        System.out.println(message);
        this.lastMessageConsumed = message;
    }

    public String getLastMessageConsumed() {
        return lastMessageConsumed;
    }
}
