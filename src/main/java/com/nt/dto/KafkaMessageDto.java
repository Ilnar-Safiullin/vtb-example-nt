package com.nt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KafkaMessageDto {

    @JsonProperty("msg_uuid")
    private String msgUuid;

    @JsonProperty("head")
    private Boolean head;

    @JsonProperty("method")
    private String method;

    @JsonProperty("uri")
    private String uri;
}