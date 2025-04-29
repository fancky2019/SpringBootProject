package com.example.demo.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Map;

/**
 * 使用Filter方案即可满足基本需求，性能开销最小且实现简单。如需更精细的监控，再考虑结合AOP :aspect 或 HandlerInterceptor
 *
 *
 *
 *
 */
@Component
@Slf4j
public class RequestLogFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 包装请求以支持多次读取
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);

        // 记录请求开始时间
        long startTime = System.currentTimeMillis();

        try {
            // 继续过滤器链
            filterChain.doFilter(wrappedRequest, response);
        } finally {
            // 记录请求信息
            logRequestInfo(wrappedRequest, startTime);
        }
    }

    private void logRequestInfo(ContentCachingRequestWrapper request, long startTime) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== 请求详情 ===\n");
        sb.append("Method: ").append(request.getMethod()).append("\n");
        sb.append("URI: ").append(request.getRequestURI()).append("\n");
        sb.append("耗时: ").append(System.currentTimeMillis() - startTime).append("ms\n");

        // 打印参数
        Map<String, String[]> paramMap = request.getParameterMap();
        if (!paramMap.isEmpty()) {
            sb.append("Parameters:\n");
            paramMap.forEach((key, values) ->
                    sb.append("  ").append(key).append(": ").append(Arrays.toString(values)).append("\n"));
        }

        // 打印请求体
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            String body = new String(content, request.getCharacterEncoding());
            sb.append("Body: ").append(body).append("\n");
        }

        log.info(sb.toString());
    }
}
