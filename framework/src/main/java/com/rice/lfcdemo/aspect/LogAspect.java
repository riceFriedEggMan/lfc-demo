package com.rice.lfcdemo.aspect;

import com.alibaba.fastjson.JSON;
import com.rice.lfcdemo.annotation.SystemLog;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Component
@Slf4j
@Aspect
public class LogAspect {
    @Pointcut("@annotation(com.rice.lfcdemo.annotation.SystemLog)")
    public void pt(){}

    @Around("pt()")
    public Object printLog(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();
        Object ret;
        try {
            handlerBefore(joinPoint);
            ret = joinPoint.proceed();
            handlerAfter(ret);
        } finally {
            log.info("=======End=======" + System.lineSeparator());
        }
        String methodName = joinPoint.getSignature().toShortString();
        long end = System.nanoTime();
        long duration = end - start;
        log.info("方法" + methodName + "耗时：" + duration / 1_000_000);
        return ret;
    }

    private void handlerBefore(ProceedingJoinPoint joinPoint) {
        log.info("====================statr==================");
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        SystemLog systemLog = getSystemLog(joinPoint);
        log.info("URL                   : {}", request.getRequestURL());
        log.info("BusinessName                   : {}", systemLog.bussinessName());
        log.info("HTTP Method                   : {}", request.getMethod());
        log.info("Class Method                   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), ((MethodSignature) joinPoint.getSignature()).getName());
        log.info("IP                   : {}", request.getRemoteHost());
        log.info("Request Args                  : {}", JSON.toJSONString(joinPoint.getArgs()));
    }

    private SystemLog getSystemLog(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SystemLog annotation = method.getAnnotation(SystemLog.class);
        return annotation;
    }

    private void handlerAfter(Object ret){
        log.info("Response      :{}", JSON.toJSONString(ret));
    }

}
