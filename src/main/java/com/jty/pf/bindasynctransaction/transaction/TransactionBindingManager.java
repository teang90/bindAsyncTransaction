package com.jty.pf.bindasynctransaction.transaction;

import com.jty.pf.bindasynctransaction.common.data.ResponseDTO;
import com.jty.pf.bindasynctransaction.jms.ReceiveCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;

@Slf4j
public class TransactionBindingManager {

    private final CacheManager cacheManager;

    public TransactionBindingManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void receiveMsg(String txId, ResponseDTO responseDTO) {
        ReceiveCallback receiveCallback = cacheManager.getCache("txManagerCache").get(txId, ReceiveCallback.class);

        if(receiveCallback==null){
            log.error("timeout!! {}", txId);
        }

        receiveCallback.recvResponse(txId, responseDTO);
    }

    public void addResponseCallback(String sessionId, ReceiveCallback receiveCallback) {
        cacheManager.getCache("txManagerCache").put(sessionId, receiveCallback);
    }
}
