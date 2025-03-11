package com.example.demo.service.demo;

import com.example.demo.model.entity.demo.MqMessage;
import com.baomidou.mybatisplus.extension.service.IService;

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
    void update (MqMessage mqMessage);
    /**
     * 失败处理
     */
    void mqOperation();

    void rePublish(List<MqMessage> mqMessageList);

    void reConsume(List<MqMessage> mqMessageList) throws Exception;
}
