package com.jty.pf.bindasynctransaction.restAPI.controller;

import com.jty.pf.bindasynctransaction.common.data.ResponseDTO;
import com.jty.pf.bindasynctransaction.restAPI.service.EventService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/enter.do")
    public ResponseEntity<ResponseDTO> registIp(HttpServletRequest request) throws ExecutionException, InterruptedException, TimeoutException {
        ResponseDTO responseDTO = eventService.registIp(request.getRequestedSessionId(), request.getRemoteAddr(), LocalDateTime.now());
        return ResponseEntity.ok().body(responseDTO);
    }

}
