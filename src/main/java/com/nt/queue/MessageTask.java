package com.nt.queue;

import org.springframework.kafka.support.Acknowledgment;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageTask {

    private final String payload;
    private final Acknowledgment acknowledgment;
}