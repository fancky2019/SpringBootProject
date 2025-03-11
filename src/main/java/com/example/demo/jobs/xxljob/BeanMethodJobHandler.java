package com.example.demo.jobs.xxljob;

import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.service.demo.IMqMessageService;
import com.example.demo.utility.RedisKeyConfigConst;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * 将@XxlJob里的注解填入后台的JobHandler*
 *
 * http://localhost:8182/xxl-job-admin/toLogin
 * admin,123456
 * 建议采用方法注册:方法上加注解的方式 @XxlJob("beanMethodJobHandler")
 *
 *1、添加执行器
 * 2、添加任务
 *
 * //
 * postProcessBeforeInitialization：在 Bean 的初始化方法（如 @PostConstruct、InitializingBean 的 afterPropertiesSet 方法）之前调用。
 *
 * postProcessAfterInitialization：在 Bean 的初始化方法之后调用。
 *
 * BeanPostProcessor (Bean的后置处理器)的应用场景
 *4.1 修改 Bean 的属性
 * 在 Bean 初始化之前或之后，动态修改 Bean 的属性值。
 * 4.2 代理 Bean 的实例
 * 在 Bean 初始化之后，为其创建代理对象（如 AOP 代理）。
 * 4.3 扫描注解
 * 在 Bean 初始化之后，扫描 Bean 的方法或字段上的注解，并执行相应的逻辑（如 XXL-JOB 的 @XxlJob 注解扫描）。
 * 4.4 注册自定义组件
 * 在 Bean 初始化之后，将 Bean 注册到自定义的容器或管理器中。
 *
 *
 * //region 在 Spring 容器启动时，XXL-JOB 通过自定义机制扫描所有带有 @XxlJob 注解的方法，并将这些方法注册为任务 Handler
 * XXL-JOB 通过 Spring 的扩展机制（如 BeanPostProcessor 或 ApplicationListener）在 Spring 容器启动时扫描所有 Bean，
 * 找到带有 @XxlJob 注解的方法，并将其注册到调度中心。
 *
 *
 *
 * import org.springframework.beans.BeansException;
 * import org.springframework.beans.factory.config.BeanPostProcessor;
 * import org.springframework.stereotype.Component;
 * import java.lang.reflect.Method;
 *
 * @Component
 * public class XxlJobAnnotationProcessor implements BeanPostProcessor {
 *
 *  // 在 Bean 初始化之前调用
 *     @Override
 *     public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
 *         return bean;
 *     }
 *
 *  // 在 Bean 初始化之后调用
 *     @Override
 *     public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
 *         // 遍历 Bean 的所有方法
 *         for (Method method : bean.getClass().getDeclaredMethods()) {
 *             // 检查方法是否带有 @XxlJob 注解
 *             if (method.isAnnotationPresent(XxlJob.class)) {
 *                 XxlJob xxlJob = method.getAnnotation(XxlJob.class);
 *                 String jobName = xxlJob.value(); // 获取任务名称
 *                 // 将任务方法注册到 XXL-JOB 调度中心
 *                 registerJobHandler(jobName, bean, method);
 *             }
 *         }
 *         return bean;
 *     }
 *
 *     private void registerJobHandler(String jobName, Object bean, Method method) {
 *         // 将任务方法注册到 XXL-JOB 调度中心的逻辑
 *         System.out.println("注册任务: " + jobName);
 *         // 这里可以调用 XXL-JOB 的 API 将任务注册到调度中心
 *     }
 * }
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 * import org.springframework.context.ApplicationContext;
 * import org.springframework.context.ApplicationListener;
 * import org.springframework.context.event.ContextRefreshedEvent;
 * import java.lang.reflect.Method;
 * import java.util.Map;
 *
 * public class XxlJobSpringExecutor implements ApplicationListener<ContextRefreshedEvent> {
 *
 *     @Override
 *     public void onApplicationEvent(ContextRefreshedEvent event) {
 *         // 获取 Spring 容器
 *         ApplicationContext applicationContext = event.getApplicationContext();
 *         // 获取所有 Bean
 *         Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(Component.class);
 *         for (Object bean : beanMap.values()) {
 *             // 遍历 Bean 的所有方法
 *             for (Method method : bean.getClass().getDeclaredMethods()) {
 *                 // 检查方法是否带有 @XxlJob 注解
 *                 if (method.isAnnotationPresent(XxlJob.class)) {
 *                     XxlJob xxlJob = method.getAnnotation(XxlJob.class);
 *                     String jobName = xxlJob.value(); // 获取任务名称
 *                     // 将任务方法注册到 XXL-JOB 调度中心
 *                     registerJobHandler(jobName, bean, method);
 *                 }
 *             }
 *         }
 *     }
 *
 *     private void registerJobHandler(String jobName, Object bean, Method method) {
 *         // 将任务方法注册到 XXL-JOB 调度中心的逻辑
 *         System.out.println("注册任务: " + jobName);
 *         // 这里可以调用 XXL-JOB 的 API 将任务注册到调度中心
 *     }
 * }
 * //endregion
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
@Slf4j
@Component
public class BeanMethodJobHandler {

    // private static final Logger LOGGER = LogManager.getLogger(BeanMethodJobHandler.class);

    @Autowired
    private IMqMessageService mqMessageService;

    //region 模板
    @XxlJob("beanMethodJobHandler")
    public void beanMethodJobHandler() throws Exception {
        //param xxl admin 填写的任务参数
        String param = XxlJobHelper.getJobParam();
        // XxlJobLogger.log("bean method jobhandler running...");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timeStr = formatter.format(LocalDateTime.now());
        XxlJobHelper.log("BeanMethodJobHandler");
        log.info("xxljob - BeanMethodJobHandler  {} ", timeStr);

    }

    @XxlJob("dynamicJob")
    public void dynamicJob() throws Exception {
        // XxlJobLogger.log("bean method jobhandler running...");

        //param xxl admin 填写的任务参数
        String param = XxlJobHelper.getJobParam();
        System.out.println("dynamicJob - " + param);
        XxlJobHelper.log("dynamicJob");
        log.info("dynamicJob");

    }


    //endregion


    @XxlJob("mqFailHandler")
    public  void mqFailHandler() throws Exception {
        log.info("start executing xxljob - mqFailHandler ");
        //param xxl admin 填写的任务参数
        String param = XxlJobHelper.getJobParam();

        mqMessageService.mqOperation();
        // XxlJobLogger.log("bean method jobhandler running...");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timeStr = formatter.format(LocalDateTime.now());
        XxlJobHelper.log("mqFailHandler");
        log.info("end executing  xxljob - mqFailHandler  {} ", timeStr);
    }


}
