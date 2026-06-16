package com.example.demo.config;

import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
在 IDE 中点击停止按钮
在终端按 Ctrl + C
使用 kill pid 命令（默认发送 SIGTERM 信号，等同于 kill -15 pid）
注意：kill -9 pid 会立即杀死进程，JVM 没有机会执行任何关闭逻辑，因此不会有任何退出日志，也不会有任何清理操作，应该避免在生产环境使用。
 */
@Component
public class AppShutdownHook {

    private static final Logger logger = LoggerFactory.getLogger(AppShutdownHook.class);

    @PreDestroy
    public void onExit() {
        // 在这里打印你想要的任何退出日志
        logger.info("收到优雅关闭信号，Spring 容器正在销毁，执行清理任务...");
        // 你可以在这里关闭连接池、保存未处理的数据等
        logger.info("自定义清理任务执行完毕，应用正在退出。");
    }
}