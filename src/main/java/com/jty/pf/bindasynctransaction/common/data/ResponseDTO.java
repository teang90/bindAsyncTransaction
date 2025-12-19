package com.jty.pf.bindasynctransaction.common.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ResponseDTO {
    private String transactionId;

    @JsonCreator
    protected ResponseDTO(
            @JsonProperty("transactionId") String transactionId
    ) {
        this.transactionId = transactionId;
    }
}
