package com.nenu.interceptors;

import com.nenu.utils.JwtUtil;
import com.nenu.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.concurrent.TimeUnit;

//拦截器写完还需要在config中注册
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        //请求头方式传递token
        String token = request.getHeader("Authorization");
//        System.out.println("LoginInterception...");
        try{
            //从redis中获取token
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            String redisToken = operations.get(token);
            if(redisToken == null){
                //token失效
                throw new RuntimeException();
            }

            Map<String, Object> claims = JwtUtil.verifyToken(token);

            //把业务数据存储到ThreadLocal中，仅此一行
            ThreadLocalUtil.set(claims);
//            System.out.println("放行");
            return true;     //放行
        }catch (Exception e){
            response.setStatus(401);
//            System.out.println("不放行");
            return false;   //不放行
        }
    }

    //防止内存泄露，清空ThreadLocal中的数据
    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final Exception ex) throws Exception {
        ThreadLocalUtil.remove();
    }
}
