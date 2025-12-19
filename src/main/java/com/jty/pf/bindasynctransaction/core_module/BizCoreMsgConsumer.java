package com.jty.pf.bindasynctransaction.core_module;

import com.jty.pf.bindasynctransaction.jms.TxCommonValue;
import com.jty.pf.bindasynctransaction.restAPI.data.IpRequestDTO;
import com.jty.pf.bindasynctransaction.restAPI.data.IpResponseDTO;
import com.jty.pf.bindasynctransaction.restAPI.data.ResultCode;
import com.jty.pf.bindasynctransaction.utils.TypeConverter;
import jakarta.jms.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.random.RandomGenerator;

@Component
@RequiredArgsConstructor
@Slf4j
public class BizCoreMsgConsumer {

    private final JmsTemplate jmsTemplate;
    private final RandomGenerator rg = RandomGenerator.getDefault();

    @JmsListener(destination = "ip-request")
    public void ipResponse(Message message) {

        try{
            String txId = message.getStringProperty(TxCommonValue.JMS_HEADER_TX_KEY.getValue());
            IpRequestDTO requestDTO = TypeConverter.convert(message.getBody(String.class), IpRequestDTO.class);
            // 대기열 관리:
            // requestDTO 에서 IP와 accessTime을 추출해서 redis Z-SET에 저장해서 접속 대기열을 관리한다.
            IpResponseDTO from = IpResponseDTO.from(txId, ResultCode.SUCCESS, rg.nextLong(0, Short.MAX_VALUE));
            jmsTemplate.convertAndSend("ip-response",  TypeConverter.toJson(from));
        } catch (Exception e) {
            log.error("error!! {}", e.getMessage(), e);
        }

    }


}
