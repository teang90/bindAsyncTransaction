package com.jty.pf.bindasynctransaction.jms;

import com.jty.pf.bindasynctransaction.restAPI.data.IpRequestDTO;
import com.jty.pf.bindasynctransaction.utils.TypeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

@RequiredArgsConstructor
public class JmsSender {

    private final JmsTemplate jmsTemplate;

    @Value("${jms.queue.send.event}")
    private String targetQueueName;

    public void send(IpRequestDTO ipReqDTO) throws Exception {
        MessagePostProcessor  messagePostProcessor = message -> {
            message.setObjectProperty(TxCommonValue.JMS_HEADER_TX_KEY.getValue(), ipReqDTO.getTransactionId()) ;
            return message;
        };

        jmsTemplate.convertAndSend(targetQueueName, TypeConverter.toJson(ipReqDTO), messagePostProcessor);
    }

}
