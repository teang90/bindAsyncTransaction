package com.jty.pf.bindasynctransaction.transaction;

import com.jty.pf.bindasynctransaction.common.data.ResponseDTO;
import com.jty.pf.bindasynctransaction.jms.ReceiveCallback;
import com.jty.pf.bindasynctransaction.restAPI.data.IpRequestDTO;
import com.jty.pf.bindasynctransaction.restAPI.data.IpResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

@Slf4j
public class TransactionBindingManager {

    private final Cache cache;

    public TransactionBindingManager(CacheManager cacheManager) {
        this.cache = cacheManager.getCache("txManagerCache");
    }

    public void receiveMsg(String txId, ResponseDTO responseDTO) {
        ReceiveCallback receiveCallback = cache.get(txId, ReceiveCallback.class);

        if(receiveCallback==null){
            log.error("timeout!! {}", txId);
        }

        receiveCallback.recvResponse(txId, responseDTO);
    }

    public void addResponseCallback(String sessionId, ReceiveCallback receiveCallback) {
        cache.put(sessionId, receiveCallback);
    }
}
