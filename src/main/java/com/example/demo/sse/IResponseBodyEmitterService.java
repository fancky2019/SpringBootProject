package com.example.demo.sse;

import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface IResponseBodyEmitterService {
    ResponseBodyEmitter createResponseBodyEmitterConnect(String userId) throws Exception;
    void closeResponseBodyEmitterConnect(String userId);
    void batchSendMessage(String msg);
    ResponseBodyEmitter getResponseBodyEmitterByUserId(String userId);
    ResponseBodyEmitter sendMsgToClient(String userId, String msg);
}
