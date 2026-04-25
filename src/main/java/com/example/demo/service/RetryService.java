package com.example.demo.service;

import com.example.demo.model.viewModel.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.retry.RetryContext;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class RetryService {

    /**
     * 重试几次不成功之后就抛出失败的异常
     * <p>
     * Backoff重试等待策略，默认使用@Backoff，@Backoff的value默认为1000L ms；multiplier（前一次 delay 的倍数） 2 ;2s 4s 8s
     *
     * @Retryable = 同步阻塞的重试机制，不是异步并发。
     *
     *
     * @Retryable(
     *     value = Exception.class,      // 触发重试的异常类型
     *     recover = "recoveryFun",      // 降级/恢复方法名
     *     maxAttempts = 3,              // 最大尝试次数（包括第一次）
     *     backoff = @Backoff(
     *         delay = 2000L,            // 初始重试间隔 2秒
     *         multiplier = 2            // 间隔倍增系数
     *     )
     * )
     *
     *执行顺序：@Async → @Retryable
     * async 一次 + 同线程 retry 多次
     *
     * @Async 导致@Retryable 重试失效
     *
     *
     *
     * Resilience4j（推荐替代 Spring Retry）
     *
     */
    //指定重试失败的异常处理方法，不指定recover 会随表找一个@Recover方法
//    @Async("threadPoolExecutor") //@Async 导致@Retryable 重试失效
    @Retryable(value = Exception.class, recover = "recoveryFun", maxAttempts = 3, backoff = @Backoff(delay = 2000L, multiplier = 2))
    public MessageResult<Void> test(Object obj) {
//        @Retryable = 同步阻塞的重试机制，不是异步并发。

        // 获取当前重试上下文
        RetryContext context = RetrySynchronizationManager.getContext();

        // 获取重试次数（从0开始，0表示第一次执行）
        int retryCount = context.getRetryCount();

        log.info("currentThread: {} ,RetryTime - {}", Thread.currentThread().getName(), retryCount);

        int n = Integer.parseInt("m");
        return MessageResult.success();
    }


    /**
     *  @Recover 执行了此方法
     *
     *
     *      重试一直失败最终会进入@Recover 方法，如果不提高此注解会抛出异常
     *
     *      可以自定义异常，在重试业务中抛出对应异常，在Recover中做处理
     *
     *       recover方法必须和Retryable方法有相同类型的返回值
     * 、 @Recover 方法参数和 @Retryable 参数匹配；会自动将@Retryable方法的参数 传给@Recover方法的参数
     *
     * recoveryFun(Exception e,Object obj)
     * <p>
     * 1 @Retryable 是MessageResult<Void> 可以返回 MessageResult<String>
     * java 泛型 类型擦除，伪泛型
     *
     * @param e
     * @param obj
     */
    @Recover
    public MessageResult<Void> recoveryFun(Exception e, Object obj) throws Exception {
        String errMsg= e.getMessage();

        log.info("recoveryFun param obj 重试最终 RetryTime-" + e.getMessage());

//        将消息写入本地消息表异步重试。无法保证写入消息表成功需要人工介入：查看日志。两阶段日志
        return MessageResult.success();
    }
}
