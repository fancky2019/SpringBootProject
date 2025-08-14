package com.example.demo.init;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

/**
 * 关闭事件
 *使用快捷键：
 * Windows/Linux: Ctrl+F2
 * macOS: ⌘F2
 *
 *会触发应用的正常关闭流程
 * 执行 @PreDestroy、DisposableBean 等生命周期回调
 * 比强制停止更安全
 *
 */
@Component
public class ContextClosedListener implements ApplicationListener<ContextClosedEvent> {

//    @PreDestroy
//    public void onExit() {
//        System.out.println("应用程序即将关闭，执行清理工作...");
//        // 执行资源释放、连接关闭等操作
//    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        System.out.println("Spring上下文已关闭，应用程序即将退出ContextClosedListener");
        // 执行退出前的操作
    }
}
