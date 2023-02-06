package com.example.demo.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
@Service
public class SseEmitterServiceImpl implements ISseEmitterService {

    /**
     * 容器，保存连接，用于输出返回
     */
    private static Map<String, SseEmitter> sseCache = new ConcurrentHashMap<>();



    /*
    当后台服务重启，http会和后天重连
     */
    @Override
    public SseEmitter createSseConnect(String userId) throws Exception {
        // 设置超时时间，0表示不过期。默认30秒，超过时间未完成会抛出异常：AsyncRequestTimeoutException
        SseEmitter sseEmitter = new SseEmitter(0L);//建议和会话时长保持一致
        // 是否需要给客户端推送ID

        // 注册回调
        sseEmitter.onCompletion(completionCallBack(userId));
        sseEmitter.onError(errorCallBack(userId));
        sseEmitter.onTimeout(timeoutCallBack(userId));
        sseCache.put(userId, sseEmitter);
        log.info("创建新的sse连接，当前用户：{}", userId);

        try {
            Object obj = SseEmitter.event().id("USER_ID").data(userId);
            sseEmitter.send(SseEmitter.event().id("USER_ID").data(userId));
        } catch (IOException e) {
            log.error("SseEmitterServiceImpl[createSseConnect]: 创建长链接异常，客户端ID:{}", userId, e);
            throw new Exception("创建连接异常！", e);
        }


        CompletableFuture.runAsync(() ->
        {
            while (true) {
                String msg = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                batchSendMessage(msg);
            }
        });

        return sseEmitter;
    }

    /**
     * 群发所有人
     *
     * @param msg
     */
    @Override
    public void batchSendMessage(String msg) {
        sseCache.forEach((k, v) -> {
            SseEmitter sseEmitter = sseCache.get(k);
            if (sseEmitter != null) {
                sendMsgToClientByUserId(k, msg, sseEmitter);
            }
        });
        // return null;
    }

    /**
     * 根据客户端id关闭SseEmitter对象
     *
     * @param userId
     */
    @Override
    public void closeSseConnect(String userId) {
        SseEmitter sseEmitter = sseCache.get(userId);
        if (sseEmitter != null) {
            //出发completionCallBack
            sseEmitter.complete();
//            removeUser(userId);
        }
    }


    /**
     * 根据客户端id获取SseEmitter对象
     *
     * @param userId
     * @return
     */
    @Override
    public SseEmitter getSseEmitterByUserId(String userId) {
        return sseCache.get(userId);
    }


    /**
     * 推送消息到客户端，此处结合业务代码，业务中需要推送消息处调用即可向客户端主动推送消息
     *
     * @param
     * @param
     * @return
     */
    @Override
    public SseEmitter sendMsgToClient(String userId, String msg) {
        SseEmitter sseEmitter = sseCache.get(userId);
        if (sseEmitter != null) {
            sendMsgToClientByUserId(userId, msg, sseEmitter);
            return sseEmitter;
        }
        return null;
    }


    /**
     * 推送消息到客户端
     * 此处做了推送失败后，重试推送机制，可根据自己业务进行修改
     *
     * @param userId 客户端ID
     * @param
     * @author re
     * @date 2022/3/30
     **/
    private void sendMsgToClientByUserId(String userId, String msg, SseEmitter sseEmitter) {
        if (sseEmitter == null) {
            log.error("SseEmitterServiceImpl[sendMsgToClient]: 推送消息失败：客户端{}未创建长链接,失败消息:{}",
                    userId, msg);
            return;
        }

        SseEmitter.SseEventBuilder sendData = SseEmitter.event().id("TASK_RESULT").data(msg, MediaType.APPLICATION_JSON);
        try {
            sseEmitter.send(sendData);
        } catch (IOException e) {
            // 推送消息失败，记录错误日志，进行重推
            log.warn("SseEmitterServiceImpl[sendMsgToClient]: 推送消息失败：{},尝试进行重推", msg);
            removeUser(userId);
        }
    }

    /**
     * 长链接完成后回调接口(即关闭连接时调用)
     *
     * @param userId 客户端ID
     * @return java.lang.Runnable
     * @author re
     * @date 2021/12/14
     **/
    private Runnable completionCallBack(String userId) {
        return () -> {
            log.info("结束连接：{}", userId);
            removeUser(userId);
        };
    }

    /**
     * 连接超时时调用
     *
     * @param userId 客户端ID
     * @return java.lang.Runnable
     * @author re
     * @date 2021/12/14
     **/
    private Runnable timeoutCallBack(String userId) {
        return () -> {
            log.info("连接超时：{}", userId);
            removeUser(userId);
        };
    }

    /**
     * 推送消息异常时，回调方法
     *
     * @param userId 客户端ID
     * @return java.util.function.Consumer<java.lang.Throwable>
     * @author re
     * @date 2021/12/14
     **/
    private Consumer<Throwable> errorCallBack(String userId) {
        return throwable -> {
            log.error("SseEmitterServiceImpl[errorCallBack]：连接异常,客户端ID:{}", userId);
            removeUser(userId);
        };
    }


    /**
     * 移除用户连接
     *
     * @param userId 客户端ID
     * @author re
     * @date 2021/12/14
     **/
    private void removeUser(String userId) {
        sseCache.remove(userId);
        log.info("SseEmitterServiceImpl[removeUser]:移除用户：{}", userId);
    }
}
