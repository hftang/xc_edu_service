package com.xuecheng.framework.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * openFeign的拦截器
 *
 * @author hftang
 * @date 2019-04-24 17:04
 * @desc 每一次feign 发送请求时 都会走这个拦截器  在哪里用就在哪里定义 bean
 */
public class FeignClientInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {

        //获取header 找到jwt令牌
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()){
                    String headName = headerNames.nextElement();
                    String headerValue = request.getHeader(headName);
                    //将jwt令牌向下传递
                    requestTemplate.header(headName, headerValue);
                }
            }


        }


    }
}
