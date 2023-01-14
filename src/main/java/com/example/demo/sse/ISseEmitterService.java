package com.example.demo.sse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ISseEmitterService {

    SseEmitter createSseConnect(String userId) throws Exception;
    void closeSseConnect(String userId);
    SseEmitter batchSendMessage(String msg);
    SseEmitter getSseEmitterByUserId(String userId);
    SseEmitter sendMsgToClient(String userId, String msg);
}
