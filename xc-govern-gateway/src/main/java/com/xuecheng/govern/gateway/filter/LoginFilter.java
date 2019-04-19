package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.govern.gateway.service.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author hftang
 * @date 2019-04-18 15:18
 * @desc
 */
@Component
public class LoginFilter extends ZuulFilter {

    @Autowired
    AuthService authService;


    /**
     * 过滤器类型
     * <p>
     * pre 请求在被路由之前执行
     * routing 在路由请求时调用
     * post 在 routing和 error过滤器之后调用
     * error 处理请求发生错误时调用
     *
     * @return
     */
    @Override
    public String filterType() {
        return "pre";
    }

    //过滤器的序号 越小越被优先执行

    @Override
    public int filterOrder() {
        return 0;
    }

    //判断是否要执行这个过滤器 返回false就没有效果了
    @Override
    public boolean shouldFilter() {
        return true;
    }

    //判断下头上是否有Authoriration
    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();

        String tokenFromCookie = authService.getTokenFromCookie(request);

        if (StringUtils.isEmpty(tokenFromCookie)) {
            this.access_denied();//拒绝访问
            return null;
        }

        //从head中jwt
        String jwtFromHeader = authService.getJwtFromHeader(request);
        if (StringUtils.isEmpty(jwtFromHeader)) {
            this.access_denied();//拒绝访问
            return null;
        }
        //从redis 获取的过期时间

        long expire = authService.getExpire(tokenFromCookie);
        if (expire < 0) {
            this.access_denied();//拒绝访问
            return null;
        }

        return null;
    }

    //拒绝访问
    private void access_denied() {

        RequestContext requestContext = RequestContext.getCurrentContext();
        requestContext.setSendZuulResponse(false);
        requestContext.setResponseStatusCode(200);
        ResponseResult responseResult = new ResponseResult(CommonCode.UNAUTHENTICATED);

        String jsonString = JSON.toJSONString(responseResult);

        requestContext.setResponseBody(jsonString);

        HttpServletResponse response = requestContext.getResponse();

        response.setContentType("application/json;charset=utf-8");

    }


}
