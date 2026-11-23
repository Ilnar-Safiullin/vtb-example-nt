package com.nt.kafka;

import com.nt.queue.MessageTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;

@Slf4j
@Component
@RequiredArgsConstructor
public class Listener {

    private final BlockingQueue<MessageTask> messageQueue;

    @KafkaListener(
            topics = "${app.kafka.topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            concurrency = "${spring.kafka.listener.concurrency}"
    )
    public void listen(String message, Acknowledgment ack) throws InterruptedException {
        log.info("[Read from Kafka] {}", message);
        messageQueue.put(new MessageTask(message, ack));
    }
}