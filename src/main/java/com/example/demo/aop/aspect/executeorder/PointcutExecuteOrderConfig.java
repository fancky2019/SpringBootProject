package com.example.demo.aop.aspect.executeorder;

import com.example.demo.model.pojo.Student;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * 避免在同一个方法上使用多个AOP注解,
 * 当@Async代理在外层而@Transactional代理在内层时，事务确实会失效
 * @Transactional
 * @Async
 * 如果 @Async 先执行，@Transactional 后执行，
 * // 伪代码：实际调用流程
 * AsyncProxy.invoke() {
 *    // 提交到线程池执行，直接调用目标对象（未经过事务代理）。
 *    threadPool.execute(() -> targetObject.transactionalMethod());
 * }
 *
 *
 * 事务失效的本质是 代理链断裂 + 线程上下文丢失
 * 事务上下文存储机制
 * Spring 事务依赖 TransactionSynchronizationManager，基于 ThreadLocal 存储
 * 异步线程切换：新线程无法获取原线程的 ThreadLocal 事务上下文
 *
 * 修改建议：在事务方法内用线程池执行异步方法
 *
 *
 * 如果不在配置类中指定bean注册顺序，就在Aspect加 @Order(202) 注解指定顺序
 */
//@Configuration
public class PointcutExecuteOrderConfig {

    //@Bean 方法上使用 @Order 注解来控制 bean 的初始化顺序。
    @Order(202)
    @Bean(name = "pointcutExecuteOrderOneAdvisor")
    public PointcutExecuteOrderOneAdvisor pointcutExecuteOrderOneAdvisor() {
        return  new PointcutExecuteOrderOneAdvisor();
    }

    @Order(201)
    @Bean(name = "pointcutExecuteOrderTwoAdvisor")
    public PointcutExecuteOrderTwoAdvisor pointcutExecuteOrderTwoAdvisor() {
        return  new PointcutExecuteOrderTwoAdvisor();
    }
}
