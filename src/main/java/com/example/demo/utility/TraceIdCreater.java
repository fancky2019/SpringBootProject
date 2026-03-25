package com.example.demo.utility;

import brave.internal.Platform;
import brave.internal.RecyclableBuffers;

/**
 * 用到了io.zipkin.brave包下的两个类
 *
 * 可以引入sleuth 依赖
 *
 *
 * // 下游服务接收到请求时
 * public class TracingFilter {
 *     public void doFilter(ServletRequest request, ...) {
 *         HttpServletRequest httpRequest = (HttpServletRequest) request;
 *
 *         // 从请求头中提取上游传递的 traceId
 *         String traceId = httpRequest.getHeader("X-B3-TraceId");
 *         String parentSpanId = httpRequest.getHeader("X-B3-SpanId");
 *
 *         if (traceId != null) {
 *             // 使用传递过来的 traceId，生成新的 spanId
 *             MDC.put("traceId", traceId);
 *             MDC.put("spanId", generateSpanId());
 *             MDC.put("parentSpanId", parentSpanId);
 *         }
 *     }
 * }
 *
 * 传递的 HTTP 头（B3 协议）
 * Sleuth 使用 B3 协议（Zipkin 的标准）在服务间传递追踪信息：
 *
 * Header 名称	             说明	                 示例
 * X-B3-TraceId	            全局唯一的链路 ID	           a1b2c3d4e5f67890
 * X-B3-SpanId	            当前 Span 的 ID	           1234567890abcdef
 * X-B3-ParentSpanId	    父 Span 的 ID	            fedcba0987654321
 * X-B3-Sampled	            是否采样（1=采样，0=不采样）	1
 *
 *
 * 消息队列（MQ）传递 TraceId：消息头中自动包含 X-B3-TraceId
 *
 *
 *
 *
 */
public class TraceIdCreater {

    static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


    public static String getTraceId() {
        return toLowerHex(nextId());
    }

    public static long nextId() {
        long nextId;
        for(nextId = Platform.get().randomLong(); nextId == 0L; nextId = Platform.get().randomLong()) {
        }
        return nextId;
    }

    public static String toLowerHex(long v) {
        char[] data = RecyclableBuffers.parseBuffer();
        writeHexLong(data, 0, v);
        return new String(data, 0, 16);
    }

    private static void writeHexLong(char[] data, int pos, long v) {
        writeHexByte(data, pos, (byte) ((v >>> 56L) & 0xff));
        writeHexByte(data, pos + 2, (byte) ((v >>> 48L) & 0xff));
        writeHexByte(data, pos + 4, (byte) ((v >>> 40L) & 0xff));
        writeHexByte(data, pos + 6, (byte) ((v >>> 32L) & 0xff));
        writeHexByte(data, pos + 8, (byte) ((v >>> 24L) & 0xff));
        writeHexByte(data, pos + 10, (byte) ((v >>> 16L) & 0xff));
        writeHexByte(data, pos + 12, (byte) ((v >>> 8L) & 0xff));
        writeHexByte(data, pos + 14, (byte) (v & 0xff));
    }

    private static void writeHexByte(char[] data, int pos, byte b) {
        data[pos] = HEX_DIGITS[(b >> 4) & 0xf];
        data[pos + 1] = HEX_DIGITS[b & 0xf];
    }
}
