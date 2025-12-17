package com.jty.pf.bindasynctransaction.restAPI.service;

import com.jty.pf.bindasynctransaction.common.data.ResponseDTO;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface EventService {
    ResponseDTO registIp(String transactionId, String clientIp, LocalDateTime accessTime) throws ExecutionException, InterruptedException, TimeoutException;
}
