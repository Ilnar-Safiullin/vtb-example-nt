package com.nt.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.dto.KafkaMessageDto;
import com.nt.model.KafkaMessage;
import com.nt.queue.MessageTask;
import com.nt.repository.MessageRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final BlockingQueue<MessageTask> messageQueue;
    private final MessageRepository repository;
    private final ObjectMapper objectMapper;

    @Value("${app.processing.worker-threads}")
    private int workerThreads;

    @Value("${app.processing.delay}")
    private volatile long delay;

    private ExecutorService executor;
    private volatile boolean running = true;

    @PostConstruct
    public void start() {
        executor = Executors.newFixedThreadPool(workerThreads);
        for (int i = 0; i < workerThreads; i++) {
            executor.submit(this::processLoop);
        }
    }

    private void processLoop() {
        while (running) {
            try {
                MessageTask task = messageQueue.take();
                process(task);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (Exception e) {
                log.error("Ошибка обработки сообщения", e);
            }
        }
    }

    private void process(MessageTask task) throws Exception {
        KafkaMessageDto kafkaMessageDto = objectMapper.readValue(task.getPayload(), KafkaMessageDto.class);

        UUID msgUuid = UUID.fromString(kafkaMessageDto.getMsgUuid());

        Thread.sleep(delay);

        KafkaMessage entity = new KafkaMessage();
        entity.setMsguuid(msgUuid);
        entity.setHead(kafkaMessageDto.getHead());
        entity.setTimeRq(Instant.now().getEpochSecond());

        try {
            KafkaMessage saved = repository.save(entity);
            log.info("[Write to DB] { msgUuid: " + saved.getMsguuid() + ", head: " + saved.getHead() + ", timeRq: "
                    + saved.getTimeRq() + " }");
        } catch (DataIntegrityViolationException e) {
            log.warn("[Двойное чтение, один и тот же] msgUuid={}", msgUuid);
        }
        task.getAcknowledgment().acknowledge();
    }

    public void setDelayMs(long delay) {
        this.delay = delay;
        log.debug("Задержка обработки изменена на {} мс", delay);
    }

    @PreDestroy
    public void stop() {
        running = false;
        executor.shutdownNow();
    }
}