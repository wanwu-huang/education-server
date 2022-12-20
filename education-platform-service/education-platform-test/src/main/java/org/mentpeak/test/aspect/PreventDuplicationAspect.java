package org.mentpeak.test.aspect;

import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.mentpeak.core.log.exception.PlatformApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DigestUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 防止重复操作切面（处理切面注解）
 */
@Aspect
@Component
public class PreventDuplicationAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 定义切点
     */
    @Pointcut("@annotation(org.mentpeak.test.aspect.PreventDuplication)")
    public void preventDuplication() {
    }

    /**
     * 环绕通知 （可以控制目标方法前中后期执行操作，目标方法执行前后分别执行一些代码）
     *
     * @param joinPoint
     * @return
     */
    @Around("preventDuplication()")
    public Object before(ProceedingJoinPoint joinPoint) throws Exception {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Assert.notNull(request, "request cannot be null.");
        //获取执行方法
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        //获取防重复提交注解
        PreventDuplication annotation = method.getAnnotation(PreventDuplication.class);

        // 获取token以及方法标记，生成redisKey和redisValue
//        String token = request.getHeader(Constants.TOKEN);
        String token = request.getHeader("platform-auth");
        String redisKey = "PREVENT_DUPLICATION_PREFIX"
                .concat(token)
                .concat(getMethodSign(method, joinPoint.getArgs()));
        String redisValue = redisKey.concat(annotation.value()).concat("submit duplication");

        if (!redisTemplate.hasKey(redisKey)) {
            //设置防重复操作限时标记（前置通知）
            redisTemplate.opsForValue().set(redisKey, redisValue, annotation.expireSeconds(), TimeUnit.SECONDS);
            try {
                //正常执行方法并返回
                //ProceedingJoinPoint类型参数可以决定是否执行目标方法，且环绕通知必须要有返回值，返回值即为目标方法的返回值
                return joinPoint.proceed();
            } catch (Throwable throwable) {
                //确保方法执行异常实时释放限时标记(异常后置通知)
                redisTemplate.delete(redisKey);
                throw new RuntimeException(throwable);
            }
        } else {
            throw new PlatformApiException("请勿重复提交");
        }
    }

    /**
     * 生成方法标记：采用数字签名算法SHA1对方法签名字符串加签
     *
     * @param method
     * @param args
     * @return
     */
    private String getMethodSign(Method method, Object... args) {

        Object[] arguments = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof ServletRequest || args[i] instanceof ServletResponse || args[i] instanceof MultipartFile) {
                //ServletRequest不能序列化，从入参里排除，否则报异常：java.lang.IllegalStateException: It is illegal to call this method if the current request is not in asynchronous mode (i.e. isAsyncStarted() returns false)
                //ServletResponse不能序列化 从入参里排除，否则报异常：java.lang.IllegalStateException: getOutputStream() has already been called for this response
                continue;
            }
            arguments[i] = args[i];
        }
        StringBuilder sb = new StringBuilder(method.toString());
//        Object o = arguments[0];
//        if (o instanceof PaperQuestionDTO) {
//            PaperQuestionDTO convert = Func.convert(o, PaperQuestionDTO.class);
//            if (!ObjectUtils.isEmpty(convert.getPaperId())) {
//                sb.append(convert.getPaperId());
//            }
//            if (!ObjectUtils.isEmpty(convert.getQuestionId())) {
//                sb.append(convert.getQuestionId());
//            }
//            if (!ObjectUtils.isEmpty(convert.getOptionId())) {
//                sb.append(convert.getOptionId());
//            }
//        }

        for (Object arg : arguments) {
            sb.append(toString(arg));
        }
        return DigestUtils.sha1DigestAsHex(sb.toString());
    }

    private String toString(Object arg) {
        if (Objects.isNull(arg)) {
            return "null";
        }
        if (arg instanceof Number) {
            return arg.toString();
        }

        return JSONObject.toJSONString(arg);
    }
}
