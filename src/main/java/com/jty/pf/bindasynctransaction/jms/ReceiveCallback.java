package com.jty.pf.bindasynctransaction.jms;

import com.jty.pf.bindasynctransaction.common.data.ResponseDTO;
import com.jty.pf.bindasynctransaction.restAPI.data.IpResponseDTO;

public interface ReceiveCallback {
    void recvResponse(String transactionId, ResponseDTO responseDTO);
}
