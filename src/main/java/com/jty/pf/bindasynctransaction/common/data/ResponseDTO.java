package com.jty.pf.bindasynctransaction.common.data;

import lombok.Getter;

@Getter
public class ResponseDTO {
    private String transactionId;

    public ResponseDTO(String transactionId) {
        this.transactionId = transactionId;
    }
}
