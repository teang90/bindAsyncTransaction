package com.jty.pf.bindasynctransaction.jms;

import com.fasterxml.jackson.databind.util.ExceptionUtil;
import com.jty.pf.bindasynctransaction.common.data.ResponseDTO;
import com.jty.pf.bindasynctransaction.restAPI.data.IpResponseDTO;
import com.jty.pf.bindasynctransaction.transaction.TransactionBindingManager;
import jakarta.jms.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.ExceptionUtils;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JmsMsgListener {

    private final TransactionBindingManager transactionBindingManager;

    @JmsListener(destination = "${jms.queue.recv.evnet}", containerFactory = "selectorListenerContainerFactory")
    public void consumeQueueMsg(Message message){
        try{
            String txId = message.getStringProperty(TxCommonValue.JMS_HEADER_TX_KEY.getValue());
            ResponseDTO responseDTO = message.getBody(ResponseDTO.class);
            transactionBindingManager.receiveMsg(txId, responseDTO);
        }catch (Exception e){
            log.error("error!! {}", e.getMessage());
        }
    }


}


