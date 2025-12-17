package com.jty.pf.bindasynctransaction.restAPI.data;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class IpRequestDTO {
    private String transactionId;
    private String clientIp;
    private LocalDateTime accessTime;

    private IpRequestDTO(String transactionId, String clientIp, LocalDateTime accessTime) {
        this.transactionId = transactionId;
        this.clientIp = clientIp;
        this.accessTime = accessTime;
    }

    public static IpRequestDTO of(String transactionId, String clientIp, LocalDateTime accessTime){
        return new IpRequestDTO(transactionId, clientIp, accessTime);
    }

}
