package com.example.demo.dynamicDataSource;

import com.example.demo.dynamicDataSource.DataSourceAnnotation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/*
Filter:servlet采用回调的方式实现，可以获取request信息，获取不到方法的参数信息。
Interceptor:采用反射动态代理实现，可以获取request信息，获取不到方法的参数信息。
Aspect:springboot 默认采用动态代理实现，获取不到request请求的信息，可以获取方法的参数
 */
/*
@Component 的目录必须在主目录下，不然扫描不到，必须加下面注解
@ComponentScan(basePackages = {"com.example.demo.dynamicDataSource.DynamicDataSourceAspect})
 */
@Aspect
@Component
public class DynamicDataSourceAspect {
//    HttpServlet
   // HttpServletRequest
//    HttpServletResponse
//    DispatcherServlet
    @Around("@annotation(DataSourceAnnotation)")
    public Object switchDB(ProceedingJoinPoint point ) throws Throwable {
        // ...(方法执行前的逻辑)
        setDB( point);
        Object result = point.proceed();
        // ...(方法执行后的逻辑)
        return result;
    }
        public void setDB(JoinPoint point){
        //获得当前访问的class
        Class<?> className = point.getTarget().getClass();
        //获得访问的方法名
        String methodName = point.getSignature().getName();
        //得到方法的参数的类型
        Class[] argClass = ((MethodSignature)point.getSignature()).getParameterTypes();
        String dataSource = "";
        try {
            // 得到访问的方法对象
            Method method = className.getMethod(methodName, argClass);
            // 判断是否存在@DS注解
            if (method.isAnnotationPresent(DataSourceAnnotation.class)) {
                DataSourceAnnotation annotation = method.getAnnotation(DataSourceAnnotation.class);
                // 取出注解中的数据源名
                dataSource = annotation.value();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 切换数据源
        DataSourceContextHolder.setDB(dataSource);
    }
}
