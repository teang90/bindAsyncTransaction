package com.jty.pf.bindasynctransaction.restAPI.service;

import com.jty.pf.bindasynctransaction.common.data.ResponseDTO;
import com.jty.pf.bindasynctransaction.jms.JmsSender;
import com.jty.pf.bindasynctransaction.jms.ReceiveCallback;
import com.jty.pf.bindasynctransaction.restAPI.data.IpRequestDTO;
import com.jty.pf.bindasynctransaction.restAPI.data.IpResponseDTO;
import com.jty.pf.bindasynctransaction.transaction.TransactionBindingManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class ClientRegistService implements EventService {

    private final JmsSender jmsSender;
    private final TransactionBindingManager transactionBindingManager;

    @Override
    public ResponseDTO registIp(String sessionId, String clientIp, LocalDateTime accessTime) throws Exception {
        // 1) 비동기 응답을 수신 받을 callback과 타임아웃 처리를 위한 CompletableFuture 세팅
        CompletableFuture<IpResponseDTO> outDtoCF = new CompletableFuture<>();
        ReceiveCallback receiveCallback = (txId, responseDTO) -> {
            if(!sessionId.equals(txId))
                throw new IllegalStateException("not match transactionId : "+
                        "request.trId=%s, response.trid=%s".formatted(sessionId, txId)
                );

            outDtoCF.complete( (IpResponseDTO) responseDTO);
        };

        // 2) [응답 데이터 수신 준비] 측위 응답에 callback 등록
        transactionBindingManager.addResponseCallback(sessionId, receiveCallback);
        
        // 3) core 모듈로 biz 요청
        jmsSender.send( IpRequestDTO.of(sessionId, clientIp, accessTime) );

        return outDtoCF.get(3L, TimeUnit.SECONDS);
    }

}
