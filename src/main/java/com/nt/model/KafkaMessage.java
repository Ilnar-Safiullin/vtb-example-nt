package com.nt.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "message_log")
public class KafkaMessage {
    @Id
    @Column(name = "msg_uuid", nullable = false)
    private UUID msguuid;

    @Column(name = "head", nullable = false)
    private Boolean head;

    @Column(name = "time_rq", nullable = false)
    private Long timeRq;
}