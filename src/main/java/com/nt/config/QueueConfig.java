package com.nt.config;

import com.nt.queue.MessageTask;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class QueueConfig {

    @Bean
    public BlockingQueue<MessageTask> messageQueue(
            @Value("${app.processing.queue-capacity}") int capacity) {
        return new LinkedBlockingQueue<>(capacity);
    }
}