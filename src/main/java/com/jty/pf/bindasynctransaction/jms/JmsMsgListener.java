package com.jty.pf.bindasynctransaction.jms;

import com.fasterxml.jackson.databind.util.ExceptionUtil;
import com.jty.pf.bindasynctransaction.common.data.ResponseDTO;
import com.jty.pf.bindasynctransaction.restAPI.data.IpResponseDTO;
import com.jty.pf.bindasynctransaction.transaction.TransactionBindingManager;
import com.jty.pf.bindasynctransaction.utils.TypeConverter;
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
            ResponseDTO responseDTO = TypeConverter.convert(message.getBody(String.class), IpResponseDTO.class);
            transactionBindingManager.receiveMsg(responseDTO.getTransactionId(), responseDTO);
        }catch (Exception e){
            log.error("error!! {}", e.getMessage(), e);
        }

    }


}


