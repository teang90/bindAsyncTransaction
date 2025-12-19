package com.jty.pf.bindasynctransaction.restAPI.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class IpRequestDTO {
    private String transactionId;
    private String clientIp;
    private LocalDateTime accessTime;

    @JsonCreator
    private IpRequestDTO(
            @JsonProperty("transactionId") String transactionId,
            @JsonProperty("clientIp") String clientIp,
            @JsonProperty("accessTime")LocalDateTime accessTime
    ) {
        this.transactionId = transactionId;
        this.clientIp = clientIp;
        this.accessTime = accessTime;
    }

    public static IpRequestDTO of(String transactionId, String clientIp, LocalDateTime accessTime){
        return new IpRequestDTO(transactionId, clientIp, accessTime);
    }

}
