package com.jty.pf.bindasynctransaction.restAPI.controller;

import com.jty.pf.bindasynctransaction.common.data.ResponseDTO;
import com.jty.pf.bindasynctransaction.restAPI.service.EventService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EventController {

    private final EventService eventService;

    @GetMapping("/enter.do")
    public ResponseEntity<ResponseDTO> registIp(HttpServletRequest request) throws Exception {
        ResponseDTO responseDTO = eventService.registIp(
                request.getSession().getId(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        );

        log.info("responseDTO={}", responseDTO);

        return ResponseEntity.ok().body(responseDTO);
    }

}