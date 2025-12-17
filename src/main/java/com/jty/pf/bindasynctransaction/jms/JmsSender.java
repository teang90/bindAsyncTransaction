package com.jty.pf.bindasynctransaction.jms;

import com.jty.pf.bindasynctransaction.restAPI.data.IpRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

@RequiredArgsConstructor
public class JmsSender {

    private final JmsTemplate jmsTemplate;



    public void send(IpRequestDTO ipReqDTO){
        MessagePostProcessor  messagePostProcessor = message -> {
            message.setObjectProperty(TxCommonValue.JMS_HEADER_TX_KEY.getValue(), ipReqDTO.getTransactionId()) ;
            return message;
        };

        jmsTemplate.convertAndSend(ipReqDTO, messagePostProcessor);
    }

}
