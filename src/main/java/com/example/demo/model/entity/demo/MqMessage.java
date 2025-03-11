package com.example.demo.model.entity.demo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.util.UUID;

import com.example.demo.rabbitMQ.RabbitMQConfig;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import okhttp3.internal.ws.RealWebSocket;

/**
 * <p>
 *
 * </p>
 *
 * @author author
 * @since 2023-11-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("mq_message")
@ApiModel(value = "MqMessage对象", description = "")
public class MqMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String msgId;

    private String msgContent;

    private String exchange;

    private String routeKey;

    private String queue;

//    private Boolean publishAck;
//
//    private Boolean consumeAck;
//
//    private Boolean consumeFail;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String remark;

    private Integer version;
    private Integer status;
    /**
     * 取代 publishAck consumeAck
     *    0:未生成 1：已生产 2：已消费
     */
    public MqMessage() {

    }

    public MqMessage(String exchange, String routeKey, String queue, String msgContent) {
        this.msgId = UUID.randomUUID().toString();
        this.msgContent = msgContent;
        this.exchange = exchange;
        this.routeKey = routeKey;
        this.queue = queue;
        this.status=0;
        this.version=0;
        this.remark="";
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
}
