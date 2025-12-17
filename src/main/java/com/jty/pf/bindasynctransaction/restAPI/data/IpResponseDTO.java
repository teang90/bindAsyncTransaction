package com.jty.pf.bindasynctransaction.restAPI.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jty.pf.bindasynctransaction.common.data.ResponseDTO;
import com.jty.pf.bindasynctransaction.utils.TypeConverter;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class IpResponseDTO extends ResponseDTO {
    private ResultCode resultCode = ResultCode.SERVER_ERROR;
    private Long waitingNumber;

//    private IpResponseDTO(String transactionId, ResultCode resultCode, Long waitingNumber) {
//        this.transactionId = transactionId;
//        this.resultCode = resultCode;
//    }

    @JsonCreator
    private IpResponseDTO(
            @JsonProperty("transactionId") String transactionId,
            @JsonProperty("resultCode") ResultCode resultCode,
            @JsonProperty("waitingNumber") Long waitingNumber
    ) {
        super(transactionId);
        this.resultCode = resultCode;
        this.waitingNumber = waitingNumber;
    }


    public static IpResponseDTO from(String transactionId, ResultCode resultCode,  Long waitingNumber) {
        return new IpResponseDTO(transactionId, resultCode, waitingNumber);
    }

    public static IpResponseDTO from(String msg) throws Exception{
        return TypeConverter.convert(msg, IpResponseDTO.class);
    }

}
