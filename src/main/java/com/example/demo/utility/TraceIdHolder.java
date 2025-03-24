package com.example.demo.utility;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;

public final class TraceIdHolder {

    /**
     * 每个请求线程一个traceId,在demo.filters.TraceIdFilter 中设置值和销毁（destroy）
     */
    private static final ThreadLocal<String> traceIdHolder = new NamedThreadLocal("TraceId");

    private TraceIdHolder() {
    }

    public static void removeTraceId() {
        traceIdHolder.remove();
    }

    public static void setTraceId( String traceId) {
        traceIdHolder.set(traceId);
    }


    public static String getTraceId() {
        return traceIdHolder.get();
    }
}
