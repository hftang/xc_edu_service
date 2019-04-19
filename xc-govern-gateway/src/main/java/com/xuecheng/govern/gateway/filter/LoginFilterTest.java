package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author hftang
 * @date 2019-04-18 14:33
 * @desc 测试用已废弃 component
 * shouldFilter 是 false
 */
//@Component
public class LoginFilterTest extends ZuulFilter {


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
        return false;
    }

    //判断下头上是否有Authoriration
    @Override
    public Object run() throws ZuulException {

        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        HttpServletResponse response = requestContext.getResponse();


        String authorization = request.getHeader("Authorization");

        if (StringUtils.isEmpty(authorization)) {
            //拒绝访问
            requestContext.setSendZuulResponse(false);
            //设置响应的代码
            requestContext.setResponseStatusCode(200);
            //构建响应信息
            ResponseResult responseResult = new ResponseResult(CommonCode.UNAUTHENTICATED);
            //转成json
            String toJSONString = JSON.toJSONString(responseResult);

            requestContext.setResponseBody(toJSONString);
            //转成json  content-type
            response.setContentType("application/json;charset=utf-8");

            return null;


        }


        return null;
    }
}
