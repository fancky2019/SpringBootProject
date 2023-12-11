package com.example.demo.service;

import com.example.demo.model.viewModel.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RetryService {
    private static int time = 1;

    /**
     * 重试几次不成功之后就抛出失败的异常
     * <p>
     * Backoff重试等待策略，默认使用@Backoff，@Backoff的value默认为1000L ms；multiplier（前一次 delay 的倍数） 2 ;2s 4s 8s
     */
    //指定重试失败的异常处理方法，不指定recover 会随表找一个@Recover方法
    @Retryable(value = Exception.class, recover = "recoveryFun", maxAttempts = 3, backoff = @Backoff(delay = 2000L, multiplier = 2))
    public MessageResult<Void> test(Object obj) {
        log.info("RetryTime - {}", time);
        time++;
        throw new RuntimeException(time + "");
    }

    /**
     * 重试一直失败最终会进入@Recover 方法，如果不提高此注解会抛出异常
     * <p>
     * 可以自定义异常，在重试业务中抛出对应异常，在Recover中做处理
     * <p>
     * recover方法必须和Retryable方法有相同类型的返回值
     *
     * @param e
     */
    @Recover
    public void recovery(Exception e) {
        log.info("recovery 重试最终 RetryTime-" + e.getMessage());
    }

    @Recover
    public MessageResult<String> recoveryFun(Exception e) {
        log.info("recoveryFun 重试最终 RetryTime-" + e.getMessage());
        return MessageResult.faile(e.getMessage());

    }

    /**
     * 、 @Recover 方法参数和 @Retryable 参数匹配；会自动将@Retryable方法的参数 传给@Recover方法的参数
     * recoveryFun(Exception e,Object obj)
     * <p>
     * 1 @Retryable 是MessageResult<Void> 可以返回 MessageResult<String>
     * java 泛型 类型擦除，伪泛型
     *
     * @param e
     * @param obj
     */
    @Recover
    public MessageResult<String> recoveryFun(Exception e, Object obj) throws Exception {
        log.info("recoveryFun param obj 重试最终 RetryTime-" + e.getMessage());
        return MessageResult.faile(e.getMessage());
    }
}
