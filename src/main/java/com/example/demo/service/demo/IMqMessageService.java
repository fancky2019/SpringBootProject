package com.example.demo.service.demo;

import com.example.demo.model.entity.demo.MqMessage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.model.pojo.PageData;
import com.example.demo.model.request.MqMessageRequest;
import com.example.demo.model.response.MqMessageResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author author
 * @since 2023-11-15
 */
public interface IMqMessageService extends IService<MqMessage> {
    void add(MqMessage mqMessage);

    void delete(MqMessage mqMessage);

    void update(MqMessage mqMessage) throws Exception;

    void updateByMsgId(String msgId, int status) throws Exception;

    void updateByMsgIdAsync(String msgId, int status) throws Exception;


    PageData<MqMessageResponse> list(MqMessageRequest mqMessage) throws JsonProcessingException;

    void page(MqMessageRequest mqMessage);

    void count(MqMessageRequest mqMessage);

    /**
     * 失败处理
     */
    void mqOperation();

    void rePublish(List<MqMessage> mqMessageList);

    void reConsume(List<MqMessage> mqMessageList) throws Exception;

    void redissonLockReentrantLock() throws Exception;

    void selfInvocationTransactionalBusinessLogic(int i);

    void redissonLockReleaseTransactionalUnCommit(int i) throws InterruptedException;

}
