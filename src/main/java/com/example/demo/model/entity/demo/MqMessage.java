package com.example.demo.model.entity.demo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * 本地消息表可以和业务数据保证原子性
 * 使用业务数据提交成功的观察者模式或者cdc 还是要落本地消息表防止发送失败，但是不能保证消息落数据库成功，
 * 和业务数据丢失原子性
 *
 *
 *
 * @author author
 * @since 2023-11-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("mq_message")
@ApiModel(value = "MqMessage对象", description = "")
public class MqMessage extends EntityBase implements Serializable {

//    private static final long serialVersionUID = 1L;
//
////    @TableId(value = "id", type = IdType.AUTO)
////    private Integer id;
//
//    private String msgId;
//
//    private String businessKey;
//    private String businessId;
//    private String msgContent;
//
//    private String exchange;
//
//    private String routeKey;
//
//    private String queue;
//    private Boolean retry = true;
//    private Integer retryCount;
//    private DateTime nextRetryTime;
//    /**
//     * 0:未生成 1：已生产 2：已消费 3:消费失败
//     */
//    private Integer status;
//    private Integer maxRetryCount;
//    private String failureReason;
//    private String errorStack;
//    private String remark;
////    private Boolean publishAck;
////
////    private Boolean consumeAck;
////
////    private Boolean consumeFail;
////@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
////    private LocalDateTime createTime;
////    private LocalDateTime updateTime;
////    private Integer version;
//    /**
//     * 取代 publishAck consumeAck
//     *    0:未生成 1：已生产 2：已消费
//     */
//    public MqMessage() {
//
//    }
//
//    public MqMessage(String exchange, String routeKey, String queue, String msgContent) {
//        this.msgId = UUID.randomUUID().toString();
//        this.msgContent = msgContent;
//        this.exchange = exchange;
//        this.routeKey = routeKey;
//        this.queue = queue;
//        this.status=0;
//        this.version=0;
//        this.remark="";
//        this.createTime = LocalDateTime.now();
//        this.modifyTime = LocalDateTime.now();
//    }




    //WMS-ES

//    /**
//     *
//     */
//    @TableId(type = IdType.AUTO)
//    private Integer id;

    /**
     *
     */
    private Long businessId;

    /**
     * 业务唯一标识
     */
    private String businessKey;

    /**
     *
     */
    private String msgId;

    /**
     *
     */
    private String msgContent;

    /**
     *
     */
    private String exchange;

    /**
     *
     */
    private String routeKey;

    /**
     *
     */
    private String queue;

    /**
     * 0:未生成 1：已生产 2：已消费 3:消费失败
     */
    private Integer status;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 最大重试次数
     */
    private Integer maxRetryCount;

    /**
     *
     */
    private Date nextRetryTime;

    /**
     * 异常信息
     */
    private String failureReason;

    /**
     * 异常堆栈
     */
    private String errorStack;

    /**
     *
     */
    private Integer retry;

    /**
     *
     */
    private String remark;

//    /**
//     *
//     */
//    private Integer version;
//
//    /**
//     *
//     */
//    private Integer deleted;
//
//    /**
//     *
//     */
//    private String traceId;

//    /**
//     *
//     */
//    private Date createTime;
//
//    /**
//     *
//     */
//    private Date modifyTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;



    public MqMessage() {

    }

    public MqMessage(String exchange, String routeKey, String queue, String msgContent) {
        this.msgId = UUID.randomUUID().toString();
        this.msgContent = msgContent;
        this.exchange = exchange;
        this.routeKey = routeKey;
        this.queue = queue;
        this.status = 0;
        this.version = 0;
        this.remark = "";
        this.createTime = LocalDateTime.now();
        this.modifyTime = LocalDateTime.now();
    }

}
