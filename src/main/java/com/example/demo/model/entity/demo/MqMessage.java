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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Map;
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
     *json  串
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
     *top 用，业务类型
     */
    private String queue;

    /**
     * 0:未生产 1：已生产 2：已消费 3:消费失败
     */
    private Integer status;

    private Boolean sendMq;
    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 最大重试次数.不走rabbitmq ，消息表直接作为队列。rabbitmq 不需要使用这两个字段，mq 默认重试4次
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
//        this.status = 0;
        this.version = 0;
        this.remark = "";
        this.createTime = LocalDateTime.now();
        this.modifyTime = LocalDateTime.now();
    }

    public MqMessage toMessage(Map<String, String> map) {
        if (map == null) {
            return null;
        }

        MqMessage message = this;

        // 基本字段
        message.setBusinessId(getLong(map, "businessId"));
        message.setBusinessKey(map.get("businessKey"));
        message.setMsgId(map.get("msgId"));
        message.setMsgContent(map.get("msgContent"));
        message.setExchange(map.get("exchange"));
        message.setRouteKey(map.get("routeKey"));
        message.setQueue(map.get("queue"));
        message.setStatus(getInteger(map, "status", 0));
        message.setSendMq(getBoolean(map, "sendMq", false));
        message.setRetryCount(getInteger(map, "retryCount", 0));
        message.setMaxRetryCount(getInteger(map, "maxRetryCount", 3));
        message.setRetry(getInteger(map, "retry", 0));
        message.setVersion(getInteger(map, "version", 0));
        message.setDeleted(getBoolean(map, "deleted", false));
        message.setFailureReason(map.get("failureReason"));
        message.setErrorStack(map.get("errorStack"));
        message.setRemark(map.get("remark"));

        // 日期字段解析
        String createTimeStr = map.get("createTime");
        if (createTimeStr != null) {
            message.setCreateTime(parseDateTime(createTimeStr));
        }

        String modifyTimeStr = map.get("modifyTime");
        if (modifyTimeStr != null) {
            message.setModifyTime(parseDateTime(modifyTimeStr));
        }

        String nextRetryTimeStr = map.get("nextRetryTime");
        if (nextRetryTimeStr != null) {
            try {
                message.setNextRetryTime(new Date(Long.parseLong(nextRetryTimeStr)));
            } catch (NumberFormatException e) {
//                log.warn("解析 nextRetryTime 失败: {}", nextRetryTimeStr);
            }
        }

        return message;
    }

    // ========== 辅助方法 ==========
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private void putIfNotNull(Map<String, String> map, String key, Object value) {
        if (value != null) {
            map.put(key, value.toString());
        }
    }

    private Long getLong(Map<String, String> map, String key) {
        String value = map.get(key);
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer getInteger(Map<String, String> map, String key, Integer defaultValue) {
        String value = map.get(key);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private Boolean getBoolean(Map<String, String> map, String key, Boolean defaultValue) {
        String value = map.get(key);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(value) || "1".equals(value);
    }

    private String getString(Map<String, String> map, String key, String defaultValue) {
        String value = map.get(key);
        return value == null || value.isEmpty() ? defaultValue : value;
    }

    private LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null) return null;
        try {
            return LocalDateTime.parse(dateStr, ISO_FORMATTER);
        } catch (DateTimeParseException e) {
            try {
                return LocalDateTime.parse(dateStr, DATE_FORMATTER);
            } catch (DateTimeParseException e2) {
//                log.warn("日期解析失败: {}", dateStr);
                return null;
            }
        }
    }

}
